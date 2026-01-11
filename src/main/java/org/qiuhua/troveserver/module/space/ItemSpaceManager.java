package org.qiuhua.troveserver.module.space;

import org.qiuhua.troveserver.Main;
import org.qiuhua.troveserver.config.ConfigManager;


import org.qiuhua.troveserver.module.space.command.ItemSpaceCommand;
import org.qiuhua.troveserver.module.space.config.ItemSpaceConfigData;
import org.qiuhua.troveserver.module.space.config.ItemSpaceFileConfig;
import org.qiuhua.troveserver.module.space.ui.ItemSpaceMainUi;
import org.qiuhua.troveserver.player.RPGPlayer;

import java.util.UUID;

public class ItemSpaceManager {

    public static void init(){
        ConfigManager.loadConfig("itemspace", "spaces", new ItemSpaceFileConfig());
        ConfigManager.loadConfig("itemspace", "ui", new ItemSpaceMainUi());

        new ItemSpaceCommand();
    }


    /**
     * 给玩家创建一个物品空间
     * @param rpgPlayer
     * @param itemSpaceId
     * @return
     */
    public static boolean createItemSpace(RPGPlayer rpgPlayer, String itemSpaceId){
        ItemSpaceConfigData itemSpaceConfigData = ItemSpaceFileConfig.allItemSpaceConfig.get(itemSpaceId);
        if(itemSpaceConfigData == null) return false;
        ItemSpaceData itemSpaceData = new ItemSpaceData(rpgPlayer, itemSpaceConfigData, UUID.randomUUID());
        rpgPlayer.getItemSpaceMap().put(itemSpaceData.getUuid(), itemSpaceData);
        Main.getLogger().debug("{} 创建了一个 {}/{} 物品空间", rpgPlayer.getUsername(), itemSpaceData.getName(), itemSpaceData.getUuid());
        return true;
    }






}
