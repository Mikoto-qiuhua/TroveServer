package org.qiuhua.troveserver.utils.script.wrapper;

import net.kyori.adventure.text.Component;
import net.minestom.server.component.DataComponents;
import net.minestom.server.item.ItemStack;
import net.minestom.server.item.Material;
import net.minestom.server.tag.Tag;

public class ItemWrapper {
    private final ItemStack item;

    public ItemWrapper(ItemStack item) {
        this.item = item;
    }

    /**
     * 获取原始物品对象
     * @return
     */
    public ItemStack getOriginal() {
        return item;
    }

    /**
     * 获取物品材质
     * @return
     */
    public String getMaterial() {
        return item.material().name();
    }

    /**
     * 获取物品数量
     * @return
     */
    public int getAmount() {
        return item.amount();
    }

    /**
     * 获取物品名称
     * @return
     */
    public String getName() {
        if(item == null) return "";
        Component customName = item.get(DataComponents.CUSTOM_NAME);
        return customName != null ? customName.toString() : "";
    }

    /**
     * 物品是否存在
     * @return
     */
    public boolean getExists() {
        return item != null && !item.isAir();
    }

    /**
     * 判断名称是否一样
     * @param name
     * @return
     */
    public boolean hasName(String name) {
        if(item == null) return false;
        Component customName = item.get(DataComponents.CUSTOM_NAME);
        return customName != null && customName.toString().equals(name);
    }

    /**
     * 判断材质是否一样
     * @param material
     * @return
     */
    public boolean hasMaterial(String material) {
        return item != null && item.material().name().equals(material);
    }

    /**
     * 判断材质是否一样
     * @param material
     * @return
     */
    public boolean hasMaterial(Material material) {
        return item != null && item.material() == material;
    }

    /**
     * 判断是否有这个自定义tag
     * @param tag
     * @param value
     * @return
     */
    public boolean hasTag(String tag, Object value){
        if (value instanceof String) {
            String aString = item.getTag(Tag.String(tag));
            return aString != null && aString.equals(value);
        } else if (value instanceof Integer) {
            Integer aInteger = item.getTag(Tag.Integer(tag));
            return aInteger != null && aInteger == value;
        } else if (value instanceof Boolean) {
            Boolean aBoolean = item.getTag(Tag.Boolean(tag));
            return aBoolean != null && aBoolean;
        } else if (value instanceof Double) {
            Double aDouble = item.getTag(Tag.Double(tag));
            return aDouble != null && aDouble == value;
        } else if (value instanceof Float) {
            Float aFloat = item.getTag(Tag.Float(tag));
            return aFloat != null && aFloat == value;
        }
        return false;

    }






}
