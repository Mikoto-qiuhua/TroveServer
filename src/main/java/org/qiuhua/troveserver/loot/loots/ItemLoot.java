package org.qiuhua.troveserver.loot.loots;

import net.minestom.server.MinecraftServer;
import net.minestom.server.coordinate.Pos;
import net.minestom.server.instance.Instance;
import net.minestom.server.item.ItemStack;
import org.qiuhua.troveserver.Main;
import org.qiuhua.troveserver.api.loot.AbstractLootReward;
import org.qiuhua.troveserver.entity.DropItemEntity;
import org.qiuhua.troveserver.loot.event.GivePlayerLootRewardEvent;
import org.qiuhua.troveserver.module.item.ItemManager;
import org.qiuhua.troveserver.player.RPGPlayer;

import java.util.Map;
import java.util.Set;

public class ItemLoot extends AbstractLootReward {

    /**
     * 数量
     */
    private final Integer amount;


    public ItemLoot(String type, Double weight, String condition, String key, Integer amount) {
        super(type, weight, condition, key);
        this.amount = amount;
    }

    public ItemLoot(Map<?,?> data){
        super((String) data.get("type"), (Double) data.get("weight"), (String) data.get("condition"), (String) data.get("key"));
        this.amount = (Integer) data.get("amount");
    }





    /**
     * 直接给予玩家
     *
     * @param rpgPlayer
     * @return
     */
    @Override
    public boolean run(RPGPlayer rpgPlayer) {
        GivePlayerLootRewardEvent givePlayerLootRewardEvent = new GivePlayerLootRewardEvent(rpgPlayer);
        MinecraftServer.getGlobalEventHandler().call(givePlayerLootRewardEvent);
        if(givePlayerLootRewardEvent.isCancelled()) return false;
        //检查条件和概率 有一个是false则不执行
        if(!givePlayerLootRewardEvent.isSkipCheck() && !checkCondition(rpgPlayer)) return false;
        ItemStack itemStack = ItemManager.giveItem(getKey(), amount);
        if(itemStack == ItemStack.AIR) return false;
        if(!rpgPlayer.getInventory().addItemStack(itemStack)){
            Main.getLogger().debug("{} 背包已满 无法获取 {}", rpgPlayer.getUsername(), getKey());
            return false;
        }
        return true;
    }

    /**
     * 在世界上生成
     * @param instance
     * @param pos
     * @param rpgPlayer
     * @return
     */
    @Override
    public boolean run(Instance instance, Pos pos, RPGPlayer rpgPlayer) {
        if(getDisplayItem() == null || getDisplayItem() == ItemStack.AIR) return false;
        GivePlayerLootRewardEvent givePlayerLootRewardEvent = new GivePlayerLootRewardEvent(rpgPlayer);
        MinecraftServer.getGlobalEventHandler().call(givePlayerLootRewardEvent);
        if(givePlayerLootRewardEvent.isCancelled()) return false;
        //检查条件和概率 有一个是false则不执行
        if(!givePlayerLootRewardEvent.isSkipCheck() && !checkCondition(rpgPlayer)) return false;
        ItemStack itemStack = ItemManager.giveItem(getKey(), amount);
        if(itemStack == ItemStack.AIR) return false;
        //生成掉落物
        DropItemEntity dropItemEntity = new DropItemEntity(itemStack, Set.of(rpgPlayer));
        dropItemEntity.setLootReward(this).setSputtering(true);
        dropItemEntity.spawnEntity(instance, pos);
        return true;
    }
}
