package org.qiuhua.troveserver.module.playermode.listener;

import net.minestom.server.event.inventory.InventoryPreClickEvent;
import net.minestom.server.event.player.*;
import net.minestom.server.inventory.AbstractInventory;
import net.minestom.server.inventory.PlayerInventory;
import net.minestom.server.inventory.click.Click;
import org.qiuhua.troveserver.module.playermode.PlayerModeManager;

import java.util.List;

public class BattleModeListener {



    public BattleModeListener(){
        PlayerModeManager.battleModeNode.addListener(PlayerBlockPlaceEvent.class, this::onPlayerBlockPlaceEvent);
        PlayerModeManager.battleModeNode.addListener(PlayerBlockBreakEvent.class, this::onPlayerBlockBreakEvent);
        PlayerModeManager.battleModeNode.addListener(InventoryPreClickEvent.class, this::onInventoryPreClickEvent);
        PlayerModeManager.battleModeNode.addListener(PlayerChangeHeldSlotEvent.class, this::onPlayerChangeHeldSlotEvent);
    }

    /**
     * 当玩家放置方块时
     * 取消放置
     * @param event
     */
    public void onPlayerBlockPlaceEvent(PlayerBlockPlaceEvent event){
        event.setCancelled(true);
    }

    /**
     * 当玩家破坏方块时
     * 取消挖掘
     * @param event
     */
    public void onPlayerBlockBreakEvent(PlayerBlockBreakEvent event){
        event.setCancelled(true);
    }


    /**
     * 禁止玩家交互的槽位列表
     * 这里是禁止交互整个快捷栏位
     */
    private final List<Integer> disableClickSlotList = List.of(0, 1, 2, 3, 4, 5, 6, 7, 8);

    /**
     * 战斗模式下禁止点击快捷栏位
     * @param event
     */
    public void onInventoryPreClickEvent(InventoryPreClickEvent event){
        AbstractInventory abstractInventory = event.getInventory();
        if(abstractInventory instanceof PlayerInventory){
            int slot = event.getSlot();
            //禁止使用快捷键移动物品
            if(event.getClick() instanceof Click.HotbarSwap click){
                if(disableClickSlotList.contains(click.hotbarSlot())){
                    event.setCancelled(true);
                    return;
                }
            }
            if(disableClickSlotList.contains(slot)){
                event.setCancelled(true);
            }
        }
    }

    /**
     * 战斗模式下禁止切换槽位
     * @param event
     */
    public void onPlayerChangeHeldSlotEvent(PlayerChangeHeldSlotEvent event){
        event.setCancelled(true);
    }






}
