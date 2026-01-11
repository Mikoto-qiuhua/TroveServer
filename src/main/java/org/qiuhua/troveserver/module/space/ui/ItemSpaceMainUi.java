package org.qiuhua.troveserver.module.space.ui;

import net.minestom.server.entity.Player;
import org.qiuhua.troveserver.Main;
import org.qiuhua.troveserver.api.config.IConfig;
import org.qiuhua.troveserver.arcartx.core.ui.ArcartXUIRegistry;
import org.qiuhua.troveserver.arcartx.core.ui.adapter.ArcartXUI;
import org.qiuhua.troveserver.arcartx.core.ui.adapter.CallBackType;
import org.qiuhua.troveserver.module.role.ui.RoleMainUi;
import org.qiuhua.troveserver.player.RPGPlayer;
import org.qiuhua.troveserver.utils.FileUtils;
import org.qiuhua.troveserver.utils.yaml.file.YamlConfiguration;

import java.io.File;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class ItemSpaceMainUi implements IConfig {


    /**
     * 界面对象
     */
    private static ArcartXUI arcartXUI;

    private static YamlConfiguration config;

    /**
     * 玩家当前查看的界面数据
     */
    private final static ConcurrentHashMap<UUID, ViewUiData> viewUiDataMap = new ConcurrentHashMap<>();


    /**
     * 重载配置
     */
    @Override
    public void reload() {
        if (!(new File(FileUtils.getDataFolder() , "itemspace/ui.yml").exists())){
            FileUtils.saveResource("itemspace/ui.yml", false);
        }
        config = FileUtils.loadFile("itemspace/ui.yml");
        ArcartXUIRegistry.reload("ItemSpace:Main", config);
        Main.getLogger().info("已重新加载物品空间界面UI | ID: ItemSpace:Main");

    }

    /**
     * 加载配置
     */
    @Override
    public void load() {
        if (!(new File(FileUtils.getDataFolder() , "itemspace/ui.yml").exists())){
            FileUtils.saveResource("itemspace/ui.yml", false);
        }
        config = FileUtils.loadFile("itemspace/ui.yml");
        registerUi();
    }


    private static void registerUi(){
        arcartXUI = ArcartXUIRegistry.register("ItemSpace:Main", config);
        arcartXUI.registerCallBack(CallBackType.CLOSE, callData -> {
            Player player = callData.getPlayer();
            viewUiDataMap.remove(player.getUuid());
        });


        //回调函数 包处理
        arcartXUI.registerCallBack(CallBackType.PACKET, callData -> {
            //包头
            String identifier = callData.getIdentifier();
            //数据合集
            List<String> data = callData.getData();
            Player player = callData.getPlayer();
            if(data.isEmpty()) return;
            Main.getLogger().debug("ItemSpace:Main 收到包 {} | {}", identifier, data);
            if(player instanceof RPGPlayer rpgPlayer){
                switch (identifier){


                }



            }





        });
        Main.getLogger().info("已注册物品空间界面UI | ID: ItemSpace:Main");
    }


    /**
     * 自己看自己
     * @param rpgPlayer
     */
    public static void open(RPGPlayer rpgPlayer){
        ViewUiData viewUiData = getViewUiData(rpgPlayer);
        viewUiData.targetRpgPlayer = rpgPlayer;
        arcartXUI.open(rpgPlayer, ()->{
            sendItemSpaceListPacket(rpgPlayer);
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
            sendItemSpaceListPacket(rpgPlayer);
        });
    }




    /**
     * 发送空间列表的数据包
     * 数据结构
     * List<Map> 每个空间一个map
     *      Map
     *          name 空间的名称
     *          uuid 空间的uuid
     *          sizeMax 最大可解锁容量
     *          unlockSize 已解锁容量
     *          size 当前已使用容量
     * @param rpgPlayer
     */
    private static void sendItemSpaceListPacket(RPGPlayer rpgPlayer){
        //当前查看的界面数据
        ItemSpaceMainUi.ViewUiData viewUiData = getViewUiData(rpgPlayer);
        List<Map<String, Object>> itemSpaceListDataPacket = new ArrayList<>();
        //获取空间列表
        viewUiData.getTargetRpgPlayer().getItemSpaceMap().values().forEach(itemSpaceData -> {
            Map<String, Object> map = new HashMap<>();
            map.put("name", itemSpaceData.getName());
            map.put("uuid", itemSpaceData.getUuid());
            map.put("sizeMax", itemSpaceData.getItemSpaceConfigData().getSizeMax());
            map.put("unlockSize", itemSpaceData.getUnlockSize());
            map.put("size", itemSpaceData.getItemMap().size());
            itemSpaceListDataPacket.add(map);

        });
        arcartXUI.sendPacket(rpgPlayer, "ItemSpaceListPacket", itemSpaceListDataPacket);
    }




    /**
     * 获取查看的界面数据
     * @param rpgPlayer
     * @return
     */
    private static ViewUiData getViewUiData(RPGPlayer rpgPlayer){
        return viewUiDataMap.computeIfAbsent(rpgPlayer.getUuid(), k -> new ViewUiData(rpgPlayer));
    }




    private static class ViewUiData {

        private final RPGPlayer rpgPlayer;

        /**
         * 被查看的目标
         */
        private RPGPlayer targetRpgPlayer;

        /**
         * 当前查看的空间UUID
         */
        private UUID uuid;

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
