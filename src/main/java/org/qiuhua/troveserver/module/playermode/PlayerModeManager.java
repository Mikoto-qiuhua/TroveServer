package org.qiuhua.troveserver.module.playermode;


import net.minestom.server.MinecraftServer;
import net.minestom.server.entity.EquipmentSlot;
import net.minestom.server.event.EventFilter;
import net.minestom.server.event.EventNode;
import net.minestom.server.event.inventory.InventoryPreClickEvent;
import net.minestom.server.event.item.ItemDropEvent;
import net.minestom.server.event.player.PlayerSwapItemEvent;
import net.minestom.server.event.trait.PlayerEvent;
import net.minestom.server.inventory.AbstractInventory;
import net.minestom.server.inventory.PlayerInventory;
import net.minestom.server.item.ItemStack;
import net.minestom.server.potion.Potion;
import net.minestom.server.potion.PotionEffect;
import org.qiuhua.troveserver.Main;
import org.qiuhua.troveserver.player.RPGPlayer;
import org.qiuhua.troveserver.module.playermode.event.PlayerModeSwitchEvent;
import org.qiuhua.troveserver.module.playermode.listener.BattleModeListener;
import org.qiuhua.troveserver.module.playermode.listener.BuildModeListener;
import org.qiuhua.troveserver.module.role.RoleData;
import org.qiuhua.troveserver.module.role.config.RoleConfig;
import org.qiuhua.troveserver.module.role.event.RoleSwitchEvent;

import java.util.ArrayList;
import java.util.List;

public class PlayerModeManager {


    /**
     * 玩家模式使用的节点
     * 这里面的监听器 只有玩家是登入状态才会触发
     */
    public static final EventNode<PlayerEvent> playerModeNode = EventNode.type("PlayerMode_Node", EventFilter.PLAYER, ((playerEvent, player) -> {
        RPGPlayer rpgPlayer = (RPGPlayer) player;
        return rpgPlayer.getIsLogin();
    }));

    /**
     * 建造模式节点
     * 只有玩家当前是建造模式才会触发
     */
    public static final EventNode<PlayerEvent> buildModeNode = EventNode.type("BuildMode_Node", EventFilter.PLAYER, ((playerEvent, player) -> {
        RPGPlayer rpgPlayer = (RPGPlayer) player;
        return rpgPlayer.getPlayerMode() == PlayerMode.Build;
    }));

    /**
     * 战斗模式节点
     * 只有玩家当前是战斗模式才会触发
     */
    public static final EventNode<PlayerEvent> battleModeNode = EventNode.type("BattleMode_Node", EventFilter.PLAYER, ((playerEvent, player) -> {
        RPGPlayer rpgPlayer = (RPGPlayer) player;
        return rpgPlayer.getPlayerMode() == PlayerMode.Battle;
    }));


    /**
     * 禁止玩家交互的槽位列表
     * 这里是禁止合成栏位、副手栏位
     */
    private static final List<Integer> disableClickSlotList = List.of(36, 37, 38, 39, 40, 45);


    public static void init(){
        MinecraftServer.getGlobalEventHandler().addChild(playerModeNode.setPriority(1));
        playerModeNode.addChild(buildModeNode);
        playerModeNode.addChild(battleModeNode);
        new BuildModeListener();
        new BattleModeListener();

        //玩家切换角色之后时候 将物品栏更新
        playerModeNode.addListener(RoleSwitchEvent.class, (event -> {
            RPGPlayer rpgPlayer = event.getPlayer();
            if(rpgPlayer.getPlayerMode() == PlayerMode.Battle){
                switchBattleInventory(rpgPlayer, false);
            }
        }));
        //切换战斗模式时替换物品栏
        playerModeNode.addListener(PlayerModeSwitchEvent.class, (event -> {
            if(event.isCancelled()) return;
            RPGPlayer rpgPlayer = event.getPlayer();
            if(event.getPlayerMode() == PlayerMode.Battle){
                //切换的模式是战斗模式 那就切换战斗物品栏 只有玩家是有模式的情况下才会进行记录 这里是为了避免第一次进服时玩家物品栏为空导致覆盖旧数据
                switchBattleInventory(rpgPlayer, rpgPlayer.getPlayerMode() != null);
                //战斗模式下要给一个负数急迫药水 这样可以避免玩家挥手
                Potion hastePotion = new Potion(
                        PotionEffect.HASTE,     // 效果类型
                        999999999,                  // 放大器（0=1级，1=2级）
                        -1,       // 持续时间（刻）
                        0    // 标志位：环境效果 + 粒子 + 图标
                );
                rpgPlayer.addEffect(hastePotion);

            }else {
                switchBuildInventory(rpgPlayer);
                //建造模式清理药水
                rpgPlayer.removeEffect(PotionEffect.HASTE);
            }

        }));
        //注册潜行+f键切换模式 注册进PlayerMode_Node中
        //该事件必须被取消
        playerModeNode.addListener(PlayerSwapItemEvent.class, event -> {
            RPGPlayer rpgPlayer = (RPGPlayer) event.getPlayer();
            if(rpgPlayer.isSneaking()){
                if(rpgPlayer.getPlayerMode() == PlayerMode.Battle){
                    rpgPlayer.setPlayerMode(PlayerMode.Build);
                }else {
                    rpgPlayer.setPlayerMode(PlayerMode.Battle);
                }
            }
            event.setCancelled(true);
        });
        //完全禁止交互的槽位
        //只有玩家物品栏禁止交互
        playerModeNode.addListener(InventoryPreClickEvent.class, event -> {
            AbstractInventory abstractInventory = event.getInventory();
            if(abstractInventory instanceof PlayerInventory){
                int slot = event.getSlot();
                if(disableClickSlotList.contains(slot)){
                    event.setCancelled(true);
                }
            }
        });
        //取消丢弃物品事件
        playerModeNode.addListener(ItemDropEvent.class, event -> {
            event.setCancelled(true);
        });



    }


    /**
     * 将物品栏切换到建造物品栏
     * @param rpgPlayer
     */
    public static void switchBuildInventory(RPGPlayer rpgPlayer){
        rpgPlayer.setEquipment(EquipmentSlot.OFF_HAND, RoleConfig.sprintItem);
        for(int i = 0; i <= 8; i++){
            List<ItemStack> list = rpgPlayer.getBuildModeInventory();
            ItemStack itemStack = ItemStack.AIR;
            if(list.size() >= i+1){
                itemStack = list.get(i);
            }
            rpgPlayer.getInventory().setItemStack(i, itemStack);
            Main.getLogger().debug(itemStack.toString());
        }
        Main.getLogger().debug("{} 将快捷栏替换为建造栏位", rpgPlayer.getUsername());

    }

    /**
     * 切换到战斗物品栏
     * @param rpgPlayer
     * @param isRecord 是否记录切换前的物品
     */
    public static void switchBattleInventory(RPGPlayer rpgPlayer, Boolean isRecord){
        rpgPlayer.setEquipment(EquipmentSlot.OFF_HAND, RoleConfig.sprintItem);
        //记录当前快捷栏的物品
        List<ItemStack> list = new ArrayList<>();
        for(int i = 0; i <= 8; i++){
            ItemStack itemStack = rpgPlayer.getInventory().getItemStack(i);
            list.add(itemStack);
            //顺手将用透明物品填充
            rpgPlayer.getInventory().setItemStack(i, RoleConfig.fillItem);
        }
        //如果需要记录 才执行记录
        if(isRecord){
            rpgPlayer.getBuildModeInventory().clear();
            rpgPlayer.getBuildModeInventory().addAll(list);
        }

        //将主手修改到槽位4
        rpgPlayer.setHeldItemSlot((byte) 4);
        if(isRecord){
            Main.getLogger().debug("{} 记录快捷栏,并替换为战斗栏位", rpgPlayer.getUsername());
        }else {
            Main.getLogger().debug("{} 未记录快捷栏,并替换为战斗栏位", rpgPlayer.getUsername());
        }
    }






}
