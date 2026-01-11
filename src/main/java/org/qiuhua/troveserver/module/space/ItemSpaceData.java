package org.qiuhua.troveserver.module.space;

import lombok.Getter;
import lombok.Setter;
import net.minestom.server.item.ItemStack;
import org.jetbrains.annotations.Nullable;
import org.qiuhua.troveserver.Main;
import org.qiuhua.troveserver.module.space.config.ItemSpaceConfigData;
import org.qiuhua.troveserver.player.RPGPlayer;

import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
public class ItemSpaceData {



    /**
     * 这个空间引用的配置
     * 这样可以实现仓库升级 去更换一个引用的配置
     */
    @Getter
    private final ItemSpaceConfigData itemSpaceConfigData;

    /**
     * 所属的玩家对象
     * 可以是无主的
     */
    @Getter
    @Nullable
    private final RPGPlayer rpgPlayer;


    /**
     * 这里所指的是空间的uuid 用于索引对应的空间
     */
    @Getter
    private final UUID uuid;

    /**
     * 这里是自定义的空间名称
     */
    @Getter @Setter
    private String name;

    /**
     * 是否自动拾取物品
     */
    @Getter @Setter
    private boolean isAutoPickup;


    /**
     * 这个空间已解锁的存储大小
     */
    @Getter @Setter
    private int unlockSize;

    /**
     * 物品的存储列表
     */
    @Getter
    private final ConcurrentHashMap<UUID, ItemPile> itemMap = new ConcurrentHashMap<>();



    public ItemSpaceData(RPGPlayer rpgPlayer, ItemSpaceConfigData itemSpaceConfigData, UUID uuid){
        this.itemSpaceConfigData = itemSpaceConfigData;
        this.uuid = uuid;
        this.rpgPlayer = rpgPlayer;
        this.name = itemSpaceConfigData.getId();
        isAutoPickup = itemSpaceConfigData.isAutoPickup_enable();
        unlockSize = itemSpaceConfigData.getUnlockSize();
    }

    public ItemSpaceData(ItemSpaceConfigData itemSpaceConfigData, UUID uuid){
        this.itemSpaceConfigData = itemSpaceConfigData;
        this.uuid = uuid;
        this.rpgPlayer = null;
        this.name = itemSpaceConfigData.getId();
        isAutoPickup = itemSpaceConfigData.isAutoPickup_enable();
        unlockSize = itemSpaceConfigData.getUnlockSize();
    }


    /**
     * 添加指定数量的物品
     * @param itemStack
     * @param quantity
     */
    public void putItem(ItemStack itemStack, Integer quantity){
        //有库存就堆叠数量 没有就添加一个新的进去
        for (ItemPile itemPile : itemMap.values()){
            if(itemPile.itemStack.isSimilar(itemStack)){
                itemPile.quantity = itemPile.quantity + quantity;
                Main.getLogger().debug("{} 空间添加物品 {} 个", this.uuid, quantity);
                return;
            }
        }
        itemMap.put(UUID.randomUUID(), new ItemPile(itemStack, quantity));
        Main.getLogger().debug("{} 空间新增物品 {} 个", this.uuid, quantity);
    }

    /**
     * 添加一个物品
     * @param itemStack
     */
    public void putItem(ItemStack itemStack){
        //当前的物品数量
        Integer quantity = itemStack.amount();
        putItem(itemStack, quantity);
    }


    /**
     * 拿取一个物品的指数量 最多不能超过这个物品的最大堆叠
     * @param uuid 缓存中对应物品的uuid
     * @param quantity
     * @return
     */
    public ItemStack takeItem(UUID uuid, Integer quantity){
        ItemPile itemPile = itemMap.get(uuid);
        if(itemPile == null) return ItemStack.AIR;
        Integer nowQuantity = itemPile.quantity;
        ItemStack itemStack = itemPile.itemStack;
        //要拿的物品数量大于物品堆叠数量 则拿取物品的最大堆叠数量
        if(quantity > itemStack.maxStackSize()) quantity = itemStack.maxStackSize();
        //如果要拿的数量大于当前库存数量 则拿取当前剩余库存数量
        if(quantity > nowQuantity) {
            quantity = nowQuantity;
            itemMap.remove(uuid);
        }else {
            itemPile.quantity = nowQuantity - quantity;
        }
        Main.getLogger().debug("{} 空间移除物品 {} 个", this.uuid, quantity);
        return itemStack.withAmount(quantity);
    }


    /**
     * 解锁槽位
     * @param amount
     * @return
     */
    public Boolean unlockSlot(Integer amount){
        //如果当前解锁槽位是-1 或者 最大槽位是-1 那就不执行
        if(unlockSize == -1 || itemSpaceConfigData.getSizeMax() == -1) return false;
        //如果当前解锁的槽位大于或者等于最大槽位 就结束
        if(unlockSize >= itemSpaceConfigData.getSizeMax()) return false;

        unlockSize = unlockSize + amount;
        Main.getLogger().debug("{} 空间解锁槽位 {} 个,当前槽位数量{}/{}", this.uuid, amount, unlockSize, itemSpaceConfigData.getSizeMax());
        return true;

    }


    private static class ItemPile{
        private final ItemStack itemStack;

        private int quantity;

        public ItemPile(ItemStack itemStack, int quantity){
            this.itemStack = itemStack;
            this.quantity = quantity;
        }
    }


}
