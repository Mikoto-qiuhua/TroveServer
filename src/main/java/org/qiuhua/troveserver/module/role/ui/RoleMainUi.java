package org.qiuhua.troveserver.module.role.ui;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import lombok.Getter;
import lombok.Setter;
import net.kyori.adventure.nbt.BinaryTag;
import net.minestom.server.MinecraftServer;
import net.minestom.server.codec.Transcoder;
import net.minestom.server.entity.Player;
import net.minestom.server.item.ItemStack;
import org.qiuhua.troveserver.Main;
import org.qiuhua.troveserver.api.config.IConfig;
import org.qiuhua.troveserver.arcartx.core.config.ui.type.UI;
import org.qiuhua.troveserver.arcartx.core.ui.ArcartXUIRegistry;
import org.qiuhua.troveserver.arcartx.core.ui.adapter.ArcartXUI;
import org.qiuhua.troveserver.arcartx.core.ui.adapter.CallBackType;
import org.qiuhua.troveserver.arcartx.internal.network.packet.NetworkMessageSender;
import org.qiuhua.troveserver.module.attribute.AttributeManager;
import org.qiuhua.troveserver.module.role.RoleData;
import org.qiuhua.troveserver.player.RPGPlayer;
import org.qiuhua.troveserver.utils.FileUtils;
import org.qiuhua.troveserver.utils.task.SchedulerManager;
import org.qiuhua.troveserver.utils.yaml.file.YamlConfiguration;

import java.io.File;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class RoleMainUi implements IConfig {


    /**
     * 界面对象
     */
    private static ArcartXUI arcartXUI;

    private static YamlConfiguration config;

    /**
     * 玩家当前查看的界面数据
     */
    private final static ConcurrentHashMap<UUID, ViewUiData> viewUiData = new ConcurrentHashMap<>();


    /**
     * 重载配置
     */
    @Override
    public void reload() {
        if (!(new File(FileUtils.getDataFolder() , "role/ui.yml").exists())){
            FileUtils.saveResource("role/ui.yml", false);
        }
        config = FileUtils.loadFile("role/ui.yml");
        ArcartXUIRegistry.reload("Role:Main", config);
        Main.getLogger().info("已重新加载角色界面UI | ID: Role:Main");

        //临时 顺带重载资源
        MinecraftServer.getConnectionManager().getOnlinePlayers().forEach(player -> {
            NetworkMessageSender.sendResourceReload(player, true);
        });

    }

    /**
     * 加载配置
     */
    @Override
    public void load() {
        if (!(new File(FileUtils.getDataFolder() , "role/ui.yml").exists())){
            FileUtils.saveResource("role/ui.yml", false);
        }
        config = FileUtils.loadFile("role/ui.yml");
        registerUi();
    }


    private static void registerUi(){
        arcartXUI = ArcartXUIRegistry.register("Role:Main", config);
        arcartXUI.registerCallBack(CallBackType.CLOSE, callData -> {
            Player player = callData.getPlayer();
            viewUiData.remove(player.getUuid());
        });


        //回调函数 包处理
        arcartXUI.registerCallBack(CallBackType.PACKET, callData -> {
            //包头
            String identifier = callData.getIdentifier();
            //数据合集
            List<String> data = callData.getData();
            Player player = callData.getPlayer();
            if(data.isEmpty()) return;
            Main.getLogger().debug("收到包 {} | {}", identifier, data);
            if(player instanceof RPGPlayer rpgPlayer){
                switch (identifier){
                    //玩家查看角色包
                    case "viewRole":
                        if(data.size() == 1){
                            sendViewRolePacket(rpgPlayer, data.getFirst());
                        }
                        break;

                }



            }





        });
        Main.getLogger().info("已注册角色界面UI | ID: Role:Main");
    }


    /**
     * 自己看自己
     * @param rpgPlayer
     */
    public static void open(RPGPlayer rpgPlayer){
        ViewUiData viewUiData = getViewUiData(rpgPlayer);
        viewUiData.targetRpgPlayer = rpgPlayer;
        arcartXUI.open(rpgPlayer, ()->{
            sendRoleListPacket(rpgPlayer);
        });
    }

    /**
     * 看目标
     * @param rpgPlayer
     * @param targetRpgPlayer
     */
    public static void open(RPGPlayer rpgPlayer, RPGPlayer targetRpgPlayer){
        ViewUiData viewUiData = getViewUiData(rpgPlayer);
        viewUiData.targetRpgPlayer = targetRpgPlayer;
        arcartXUI.open(rpgPlayer, ()->{

        });
    }


    /**
     * 发送角色列表的数据包
     * 数据结构
     * List<Map> 每个角色一个map
     *      Map
     *          id 角色的id
     *          level 角色的等级
     *          state 解锁状态
     * @param rpgPlayer
     */
    private static void sendRoleListPacket(RPGPlayer rpgPlayer){
        //当前查看的界面数据
        ViewUiData viewUiData = getViewUiData(rpgPlayer);
        List<Map<String, Object>> roleListDataPacket = new ArrayList<>();

        //获取角色列表
        List<RoleData> roleIdList = viewUiData.getTargetRpgPlayer().getRoleDataMap().values().stream().toList();
        //遍历每个角色对象
        roleIdList.forEach(roleData -> {
            Map<String, Object> roleDataPacket = new HashMap<>();
            roleDataPacket.put("id", roleData.getRoleId());
            roleDataPacket.put("level", roleData.getLevel());
            roleDataPacket.put("state", roleData.getRoleUnlockedState());
            roleListDataPacket.add(roleDataPacket);
        });

        arcartXUI.sendPacket(rpgPlayer, "RoleListPacket", roleListDataPacket);
    }

    /**
     * 发送一个角色信息包
     * 数据结构
     *      Map
     *          id 角色的id
     *          level 角色的等级
     *          state 解锁状态
     *          exp 当前经验值
     *          expMax 当前最大经验值
     *          isUse 是否在使用  如果查看的角色不属于自己 则默认返回true
     *          attributes 属性Map
     *              key 属性名称
     *              vale 属性值
     *          skills 技能列表 Map
     *              skillName 技能名称
     *              packet 技能自定义数据包
     *          equipSlotMap 装备槽位 Map  key是槽位id  value是对应的物品json
     *
     *
     * @param rpgPlayer
     * @param roleId
     */
    private static void sendViewRolePacket(RPGPlayer rpgPlayer, String roleId){
        //当前查看的界面数据
        ViewUiData viewUiData = getViewUiData(rpgPlayer);
        if(viewUiData.roleId != null && viewUiData.roleId.equals(roleId)) return;
        RoleData roleData = viewUiData.getTargetRpgPlayer().getRoleDataMap().get(roleId);
        if(roleData == null) return;
        viewUiData.roleId = roleId;
        Map<String, Object> roleDataPacket = new HashMap<>();
        roleDataPacket.put("id", roleData.getRoleId());
        roleDataPacket.put("level", roleData.getLevel());
        roleDataPacket.put("state", roleData.getRoleUnlockedState());
        roleDataPacket.put("exp", roleData.getExp());
        roleDataPacket.put("expMax", roleData.getExpMax());
        //当查看的目标不是自己时 默认返回true 不显示使用按钮
        if(rpgPlayer != viewUiData.targetRpgPlayer){
            roleDataPacket.put("isUse", true);
        }else {
            if(rpgPlayer.getUseRoleData() == roleData){
                roleDataPacket.put("isUse", true);
            }else {
                roleDataPacket.put("isUse", false);
            }
        }
        roleDataPacket.put("attributes", AttributeManager.attributeCompileGroupToMap(roleData.getAttributeCompileGroup()));
        List<Object> skills = new ArrayList<>();
        roleData.getRoleSkillDataList().forEach(roleSkillData -> {
            Map<String, Object> map = new HashMap<>();
            map.put("skillName", roleSkillData.getSkillName());
            map.put("packet", roleSkillData.getSkillConfigData().getPacket());
            skills.add(map);
        });
        roleDataPacket.put("skills", skills);
        Map<String, Object> equipSlotMap = new HashMap<>();
        roleData.getEquipSlotMap().forEach((slotId, equipSlotData) -> {
            String itemJson = ItemStack.CODEC.encode(Transcoder.JSON, equipSlotData.getItemStack()).orElseThrow().toString();
            equipSlotMap.put(slotId, itemJson);
            Main.getLogger().debug(itemJson);
        });
        roleDataPacket.put("equipSlotMap", equipSlotMap);
        arcartXUI.sendPacket(rpgPlayer, "RoleDataPacket", roleDataPacket);
    }



    /**
     * 获取查看的界面数据
     * @param rpgPlayer
     * @return
     */
    private static ViewUiData getViewUiData(RPGPlayer rpgPlayer){
        return viewUiData.computeIfAbsent(rpgPlayer.getUuid(), k -> new ViewUiData(rpgPlayer));
    }



    private static class ViewUiData {

        private final RPGPlayer rpgPlayer;

        /**
         * 被查看的目标
         */
        private RPGPlayer targetRpgPlayer;

        /**
         * 当前查看的角色
         */
        private String roleId;

        public ViewUiData(RPGPlayer rpgPlayer){
            this.rpgPlayer = rpgPlayer;
        }

        public RPGPlayer getTargetRpgPlayer(){
            if(targetRpgPlayer == null){
                return rpgPlayer;
            }
            return targetRpgPlayer;
        }


    }


}
