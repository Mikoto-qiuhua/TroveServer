package org.qiuhua.troveserver.listener;

import net.minestom.server.MinecraftServer;
import net.minestom.server.entity.GameMode;
import net.minestom.server.entity.Player;
import net.minestom.server.event.GlobalEventHandler;
import net.minestom.server.event.entity.EntityDamageEvent;
import net.minestom.server.event.inventory.InventoryPreClickEvent;
import net.minestom.server.event.item.PickupItemEvent;
import net.minestom.server.event.player.AsyncPlayerConfigurationEvent;
import net.minestom.server.instance.Instance;
import net.minestom.server.inventory.AbstractInventory;
import net.minestom.server.item.ItemStack;
import org.qiuhua.troveserver.Main;
import org.qiuhua.troveserver.config.ServerConfig;
import org.qiuhua.troveserver.entity.DropItemEntity;
import org.qiuhua.troveserver.module.item.ItemManager;
import org.qiuhua.troveserver.player.RPGPlayer;
import org.qiuhua.troveserver.module.world.WorldManager;

public class GlobalListener {


    public GlobalListener(){
        GlobalEventHandler globalEventHandler = MinecraftServer.getGlobalEventHandler();
        globalEventHandler.addListener(AsyncPlayerConfigurationEvent.class, this::onAsyncPlayerConfigurationEvent);
        globalEventHandler.addListener(InventoryPreClickEvent.class, this::onInventoryPreClickEvent);
        globalEventHandler.addListener(PickupItemEvent.class, event -> {
            if(event.isCancelled()) return;
            if(event.getItemEntity() instanceof DropItemEntity && event.getEntity() instanceof RPGPlayer rpgPlayer){
                String itemId = ItemManager.getItemId(event.getItemStack());
                if(itemId == null) return;
                rpgPlayer.getInventory().addItemStack(ItemManager.giveItem(itemId, event.getItemStack().amount()));
                Main.getLogger().debug("{} 尝试拾取一个物品 {}", rpgPlayer.getUsername(), itemId);
                event.getItemEntity().remove();
            }
        });
    }

    /**
     * 为进入的玩家注册出生点
     * @param event
     */
    public void onAsyncPlayerConfigurationEvent(AsyncPlayerConfigurationEvent event){
        Instance instance = WorldManager.getInstance(ServerConfig.world_default);
        if(instance == null) return;
        event.setSpawningInstance(instance);
        if(event.getPlayer() instanceof RPGPlayer rpgPlayer){
            rpgPlayer.setRespawnPoint(ServerConfig.world_respawnPoint);
            rpgPlayer.setGameMode(GameMode.ADVENTURE);
        }
    }



    public void onInventoryPreClickEvent(InventoryPreClickEvent event){
        AbstractInventory abstractInventory = event.getInventory();
        RPGPlayer rpgPlayer = (RPGPlayer) event.getPlayer();
        Main.getLogger().debug("{} 点击了 {} 的槽位 {}", rpgPlayer.getUsername(), abstractInventory, event.getSlot());
    }




}
