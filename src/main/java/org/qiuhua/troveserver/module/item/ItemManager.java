package org.qiuhua.troveserver.module.item;

import net.kyori.adventure.nbt.CompoundBinaryTag;
import net.minestom.server.component.DataComponents;
import net.minestom.server.item.ItemStack;
import net.minestom.server.item.component.TooltipDisplay;
import net.minestom.server.item.component.UseCooldown;
import net.minestom.server.tag.Tag;
import org.jetbrains.annotations.Nullable;
import org.qiuhua.troveserver.Main;
import org.qiuhua.troveserver.config.ConfigManager;
import org.qiuhua.troveserver.module.attribute.AttributeManager;
import org.qiuhua.troveserver.module.attribute.AttributeNBTData;
import org.qiuhua.troveserver.module.item.command.ItemCommand;
import org.qiuhua.troveserver.module.item.config.ItemConfig;
import org.qiuhua.troveserver.module.item.config.ItemFileConfig;
import org.qiuhua.troveserver.utils.StringUtils;
import org.qiuhua.troveserver.utils.yaml.ConfigurationSection;


import java.util.*;

public class ItemManager {

    public final static Map<String, ItemCompileData> allItem = new HashMap<>();

    /**
     * 属性模版
     */
    public final static Map<String, AttributeRandomTemplate> allAttributeRandomTemplate = new HashMap<>();


    public static void init(){
        ConfigManager.loadConfig("item", "config", new ItemConfig());
        ConfigManager.loadConfig("item", "items", new ItemFileConfig());
        new ItemCommand();


    }


    /**
     * 获取这个物品的物品库id
     * @param itemStack
     * @return 可能为null
     */
    @Nullable
    public static String getItemId(ItemStack itemStack){
        return itemStack.getTag(Tag.String("ItemId"));

    }



    /**
     * 获取一个物品
     * @param itemId
     * @return
     */
    public static ItemStack giveItem(String itemId){
        ItemCompileData itemCompileData = allItem.get(itemId);
        if(itemCompileData == null) {
            Main.getLogger().warn("尝试获取一个不存在的物品ID {}", itemId);
            return ItemStack.AIR;
        };
        return itemCompileData.getItemStack();
    }


    /**
     * 获取一个物品
     * @param itemId
     * @param amount
     * @return
     */
    public static ItemStack giveItem(String itemId, Integer amount){
        ItemCompileData itemCompileData = allItem.get(itemId);
        if(itemCompileData == null) {
            Main.getLogger().warn("尝试获取一个不存在的物品ID {}", itemId);
            return ItemStack.AIR;
        };
        ItemStack itemStack = itemCompileData.getItemStack();
        return itemStack.withAmount(amount);
    }


    /**
     * 使用配置节点生成物品
     * @param config
     * @param itemId
     * @return
     */
    public static ItemStack buildItem(ConfigurationSection config, String itemId){
        ItemCompileData itemCompileData = new ItemCompileData(config, itemId);
        return buildItem(itemCompileData);

    }

    /**
     * 使用数据生成一个全新的物品
     * @param itemCompileData
     * @return
     */
    public static ItemStack buildItem(ItemCompileData itemCompileData){
        ItemStack.Builder builder = ItemStack.builder(itemCompileData.getMaterial());
        //设置物品id
        builder.setTag(Tag.String("ItemId"), itemCompileData.getItemId());
        //设置最大堆叠
        builder.maxStackSize(itemCompileData.getMax_stack_size());
        //设置模型
        setItemModel(builder, itemCompileData.getModel_id());
        //设置是否隐藏tip
        setHideTooltip(builder, itemCompileData.isHide_tooltip());
        //设置冷却组
        setItemCooldownGroup(builder, itemCompileData.getCooldownGroup());
        //设置自定义tag
        setItemTag(builder, itemCompileData.getTagMap());
        //尝试获取属性列表
        List<String> attributes = itemCompileData.getAttributes();
        //Main.getLogger().debug(attributes.toString());
        List<AttributeNBTData> attributeNBTDataList = AttributeManager.getListAttributeNBTData(attributes);
        //设置属性tag
        setItemAttributesTag(builder, attributeNBTDataList);
        //lore列表
        List<String> lore = itemCompileData.getLore();
        //更新lore中的属性字符
        lore = updateLoreAttributes(lore, attributeNBTDataList);
        //更新lore中的自定义tag
        lore = updateLoreTag(lore, itemCompileData.getTagMap());
        builder.lore(StringUtils.stringToComponent(lore));
        //更新物品的名称
        String customName = itemCompileData.getCustom_name();
        if(customName != null){
            customName = updateCustomName(customName, itemCompileData.getTagMap());
            builder.customName(StringUtils.stringToComponent(customName));
        }
        return builder.build();
    }




    /**
     * 给物品添加自定义tag
     * @param builder
     * @param tagMap
     * @return
     */
    public static ItemStack.Builder setItemTag(ItemStack.Builder builder, Map<String, Object> tagMap){
        tagMap.forEach((key, value) -> {
            if (value instanceof String) {
                builder.setTag(Tag.String(key), (String) value);
            } else if (value instanceof Integer) {
                builder.setTag(Tag.Integer(key), (Integer) value);
            } else if (value instanceof Boolean) {
                builder.setTag(Tag.Byte(key), (byte) ((Boolean) value ? 1 : 0));
            } else if (value instanceof Double) {
                builder.setTag(Tag.Double(key), (Double) value);
            } else if (value instanceof Float) {
                builder.setTag(Tag.Float(key), (Float) value);
            }
        });
        return builder;
    }


    /**
     * 设置物品的渲染模型配置
     * @param builder
     * @param id 必须以命名空间的形式 命名空间:ID
     * @return
     */
    public static ItemStack.Builder setItemModel(ItemStack.Builder builder, String id){
        if(id != null){
            builder.set(DataComponents.ITEM_MODEL, id);
        }
        return builder;
    }

    /**
     * 设置物品冷却组
     * @param builder
     * @param id 必须以命名空间的形式 命名空间:ID
     * @return
     */
    public static ItemStack.Builder setItemCooldownGroup(ItemStack.Builder builder, String id){
        if(id != null){
            builder.set(DataComponents.USE_COOLDOWN, new UseCooldown(1, id));
        }
        return builder;
    }

    /**
     * 设置是否隐藏tip
     * @param builder
     * @param b
     * @return
     */
    public static ItemStack.Builder setHideTooltip(ItemStack.Builder builder, boolean b){
        builder.set(DataComponents.TOOLTIP_DISPLAY, new TooltipDisplay(b, Set.of()));
        return builder;
    }


    /**
     * 给物品设置属性数据
     * @param builder
     * @param attributeNBTDataList
     * @return
     */
    public static ItemStack.Builder setItemAttributesTag(ItemStack.Builder builder, List<AttributeNBTData> attributeNBTDataList){
        if(attributeNBTDataList.isEmpty()) return builder;
        //将属性nbt数据转换成NBT结构
        CompoundBinaryTag compoundBinaryTag = AttributeManager.getAttributesNBT(attributeNBTDataList);
        //将其添加进这个物品内
        builder.setTag(Tag.NBT("AttributeModifiers"), compoundBinaryTag);
        return builder;
    }


    /**
     * 更新展示名称
     * @param customName
     * @param tagMap
     * @return
     */
    public static String updateCustomName(String customName, Map<String, Object> tagMap){
        if(tagMap.isEmpty()) return customName;
        //创建批量替换占位符
        Map<String, String> replacements = new HashMap<>();
        //添加要替换的数据
        tagMap.forEach((key, value) -> {
            String newKey = "<" + key + ">";
            replacements.put(newKey, value.toString());
        });
        return StringUtils.replacePlaceholders(customName, replacements);
    }

    /**
     * 更新lore上的tag标签
     * @param lore
     * @param tagMap
     * @return
     */
    public static List<String> updateLoreTag(List<String> lore, Map<String, Object> tagMap){
        if(tagMap.isEmpty()) return lore;
        //创建批量替换占位符
        Map<String, String> replacements = new HashMap<>();
        //添加要替换的数据
        tagMap.forEach((key, value) -> {
            String newKey = "<" + key + ">";
            replacements.put(newKey, value.toString());
        });
        lore = StringUtils.replacePlaceholders(lore, replacements);
        return lore;
    }



    /**
     * 更新lore上的属性标签
     * @param lore
     * @param attributeNBTDataList
     * @return
     */
    public static List<String> updateLoreAttributes(List<String> lore, List<AttributeNBTData> attributeNBTDataList){
        if(!attributeNBTDataList.isEmpty()){
            List<String> attributesList = new ArrayList<>();
            attributeNBTDataList.forEach(attributeNBTData -> {
                StringBuilder format = new StringBuilder(ItemConfig.attributeFormat_format);
                String key = attributeNBTData.getAttributeName();
                String value = attributeNBTData.getAmount().toString();
                //是否是百分比数值
                if(attributeNBTData.getRead_pattern().equals("Percent")){
                    value = (attributeNBTData.getAmount()*100) + "%";
                }
                //处理属性名称和属性值
                format = new StringBuilder(format.toString().replaceAll("<key>", key).replaceAll("<value>", value));
                //处理强化次数
                int enchantmentCount = attributeNBTData.getEnchantmentCount();
                if(enchantmentCount > 0){
                    format.append(" ");
                    format.append(ItemConfig.attributeFormat_strengthenIcon.repeat(enchantmentCount));
                }
                attributesList.add(format.toString());
            });
            int attributesIndex = lore.indexOf("<attributes>");
            if (attributesIndex != -1) {
                //移除"{items}"行
                lore.remove(attributesIndex);
                //在原来的位置插入新列表
                lore.addAll(attributesIndex, attributesList);
            }
        }else {
            lore.remove("<attributes>");
        }
        return lore;
    }


}
