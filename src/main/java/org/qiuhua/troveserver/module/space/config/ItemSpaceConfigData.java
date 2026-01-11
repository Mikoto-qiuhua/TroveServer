package org.qiuhua.troveserver.module.space.config;

import lombok.Getter;
import org.qiuhua.troveserver.player.RPGPlayer;
import org.qiuhua.troveserver.utils.script.JavaScript;
import org.qiuhua.troveserver.utils.yaml.file.YamlConfiguration;

public class ItemSpaceConfigData {

    /**
     * 这个配置的id
     */
    @Getter
    private final String id;

    /**
     * 是否自动拾取物品
     */
    @Getter
    private final boolean autoPickup_enable;

    /**
     * 切换自动拾取的条件
     */
    private final String autoPickup_condition;


    /**
     * 默认解锁槽位的大小
     */
    @Getter
    private final int unlockSize;

    /**
     * 这个可以存储物品的大小
     */
    @Getter
    private final int sizeMax;

    public ItemSpaceConfigData(String id , YamlConfiguration config){
        this.id = id;
        autoPickup_enable = config.getBoolean("AutoPickUp.enable", false);
        autoPickup_condition = config.getString("AutoPickUp.condition", "");
        unlockSize = config.getInt("UnlockSize", -1);
        sizeMax = config.getInt("SizeMax", -1);
        //进行js条件预编译
        JavaScript.precompile(autoPickup_condition);
    }


    /**
     * 检查条件
     * @param rpgPlayer
     * @return
     */
    public boolean checkCondition(RPGPlayer rpgPlayer){
        return (Boolean) JavaScript.evaluate(autoPickup_condition, rpgPlayer);
    }

}
