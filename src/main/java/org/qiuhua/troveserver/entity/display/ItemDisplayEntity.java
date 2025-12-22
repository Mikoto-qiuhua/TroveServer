package org.qiuhua.troveserver.entity.display;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import net.minestom.server.coordinate.Pos;
import net.minestom.server.entity.EntityType;
import net.minestom.server.entity.metadata.display.ItemDisplayMeta;
import net.minestom.server.instance.Instance;
import net.minestom.server.item.ItemStack;
import org.qiuhua.troveserver.Main;
import org.qiuhua.troveserver.api.entity.AbstractDisplayEntity;


public class ItemDisplayEntity extends AbstractDisplayEntity {

    /**
     * 需要显示的物品
     */
    @Getter
    @Accessors(chain=true)
    private ItemStack itemStack;

    @Getter
    private final ItemDisplayMeta itemDisplayMeta;

    /**
     * 显示的模式
     */
    @Setter
    private ItemDisplayMeta.DisplayContext displayContext = ItemDisplayMeta.DisplayContext.NONE;



    public ItemDisplayEntity(ItemStack itemStack) {
        super(EntityType.ITEM_DISPLAY);
        this.itemStack = itemStack;
        itemDisplayMeta = (ItemDisplayMeta) getEntityMeta();
    }

    public ItemDisplayEntity() {
        super(EntityType.ITEM_DISPLAY);
        this.itemStack = null;
        itemDisplayMeta = (ItemDisplayMeta) getEntityMeta();
    }

    /**
     * 设置需要显示的物品
     * @param itemStack
     * @return
     */
    public ItemDisplayEntity setItem(ItemStack itemStack){
        itemDisplayMeta.setItemStack(itemStack);
        return this;
    }


    @Override
    public void spawnEntity(Instance instance, Pos pos){
        if(itemStack != null){
            itemDisplayMeta.setItemStack(itemStack);
        }
        itemDisplayMeta.setDisplayContext(displayContext);
        super.spawnEntity(instance, pos);
    }





}
