package org.qiuhua.troveserver.api.loot;

import lombok.Getter;
import net.minestom.server.coordinate.Pos;
import net.minestom.server.instance.Instance;
import net.minestom.server.item.ItemStack;
import org.qiuhua.troveserver.Main;
import org.qiuhua.troveserver.module.item.ItemManager;
import org.qiuhua.troveserver.player.RPGPlayer;
import org.qiuhua.troveserver.utils.script.JavaScript;

import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

public abstract class AbstractLootReward {


    /**
     * 战利品类型
     */
    @Getter
    private final String type;

    /**
     * 触发概率
     */
    @Getter
    private final Double weight;

    /**
     * js条件 先检查这个才检查概率
     */
    private final String condition;

    /**
     * key 默认情况下这个作为展示物品的索引
     */
    @Getter
    private final String key;

    @Getter
    private final ItemStack displayItem;


    public AbstractLootReward(String type, Double weight, String condition, String key){
        this.weight = weight;
        this.type = type;
        this.condition = condition;
        this.key = key;
        displayItem = ItemManager.giveItem(key);
        JavaScript.precompile(condition);
    }


    /**
     * 检查js条件
     * @param rpgPlayer
     * @return
     */
    public boolean checkCondition(RPGPlayer rpgPlayer){
        return (Boolean) JavaScript.evaluate(condition, rpgPlayer);
    }




    /**
     * 直接给予玩家
     * @param rpgPlayer
     * @return
     */
    public abstract boolean run(RPGPlayer rpgPlayer);

    /**
     * 在世界上生成
     * @param instance
     * @param pos
     * @param rpgPlaye
     * @return
     */
    public abstract boolean run(Instance instance, Pos pos, RPGPlayer rpgPlaye);



}
