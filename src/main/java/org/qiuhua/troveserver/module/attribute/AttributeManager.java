package org.qiuhua.troveserver.module.attribute;

import lombok.Getter;
import net.kyori.adventure.nbt.CompoundBinaryTag;
import net.minestom.server.MinecraftServer;
import net.minestom.server.entity.Entity;
import net.minestom.server.entity.attribute.Attribute;
import net.minestom.server.entity.attribute.AttributeInstance;
import net.minestom.server.entity.attribute.AttributeModifier;
import net.minestom.server.entity.attribute.AttributeOperation;
import net.minestom.server.item.ItemStack;
import net.minestom.server.tag.Tag;
import org.jetbrains.annotations.Nullable;
import org.qiuhua.troveserver.Main;
import org.qiuhua.troveserver.api.entity.AbstractEntity;
import org.qiuhua.troveserver.config.ConfigManager;
import org.qiuhua.troveserver.module.attribute.command.AttributeCommand;
import org.qiuhua.troveserver.module.attribute.config.AttributeConfig;
import org.qiuhua.troveserver.module.attribute.config.AttributeInfoConfig;
import org.qiuhua.troveserver.module.attribute.event.EntityAttributeAddEvent;
import org.qiuhua.troveserver.module.attribute.event.EntityAttributeRemoveEvent;
import org.qiuhua.troveserver.module.attribute.event.EntityAttributeUpdateEvent;
import org.qiuhua.troveserver.player.RPGPlayer;
import org.qiuhua.troveserver.utils.StringUtils;

import java.util.*;

public class AttributeManager {


    public static void init(){
        ConfigManager.loadConfig("attribute", "config", new AttributeConfig());
        ConfigManager.loadConfig("attribute", "attributes", new AttributeInfoConfig());
        new AttributeCommand();
    }

    /**
     * 记录全部属性的配置
     */
    @Getter
    private final static Map<String, AttributeDataConfig> attributesDataConfigMap = new HashMap<>();


    /**
     * 注册一个属性
     * @param key
     * @param attributeConfig
     */
    public static void register(String key, AttributeDataConfig attributeConfig){
        attributesDataConfigMap.put(key, attributeConfig);
    }

    /**
     * 获取对应的属性配置
     * @param attributeName
     * @return
     */
    @Nullable
    public static AttributeDataConfig getAttributeDataConfig(String attributeName){
        for (AttributeDataConfig attributeDataConfig : attributesDataConfigMap.values()){
            if(attributeDataConfig.getNames().contains(attributeName)) {
                return attributeDataConfig;
            }
        }
        return null;
    }


    /**
     * 获取AttributeModifiers的内部nbt结构
     * 他应当被添加进AttributeModifiers NBT内才有效
     * @param attributeNBTDataList
     * @return
     */
    public static CompoundBinaryTag getAttributesNBT(List<AttributeNBTData> attributeNBTDataList){
        CompoundBinaryTag.Builder compoundBinaryTagBuilder = CompoundBinaryTag.builder();
        attributeNBTDataList.forEach(attributeNBTData -> {
            CompoundBinaryTag modifier = CompoundBinaryTag.builder()
                    .putString("AttributeKey", attributeNBTData.getAttributeKey())
                    .putString("AttributeName", attributeNBTData.getAttributeName())
                    .putString("Read_Pattern", attributeNBTData.getRead_pattern())
                    .putDouble("Amount", attributeNBTData.getAmount())
                    .putInt("EnchantmentCount", attributeNBTData.getEnchantmentCount()).build();
            compoundBinaryTagBuilder.put(UUID.randomUUID().toString(), modifier);
        });
        return compoundBinaryTagBuilder.build();
    }

    /**
     * 获取该字符串上的属性信息
     * 他的格式应当是 属性名称:数值%  或  属性名称:数值
     * @param attribute
     * @return
     */
    public static AttributeNBTData getAttributeNBTData(String attribute){
        String[] parts = attribute.split(":");
        if(parts.length != 2) return null;
        String key = parts[0];
        AttributeDataConfig attributeDataConfig = AttributeManager.getAttributeDataConfig(key);
        if(attributeDataConfig == null) {
            return null;
        }
        String value = parts[1];
        if(value.endsWith("%")){
            value = value.replaceAll("%", "");
            Double amount = StringUtils.stringToDouble(value);
            if(amount == null) return null;
            amount = amount / 100;
            return new AttributeNBTData(attributeDataConfig.getAttributeKey(), key, "Percent", amount);
        }else {
            Double amount = StringUtils.stringToDouble(value);
            if(amount == null) return null;
            return new AttributeNBTData(attributeDataConfig.getAttributeKey(), key, "Default", amount);
        }
    }





    /**
     * 获取物品上的属性
     * 物品包含多个属性 所以他的结果应该是属性组
     * @param itemStack
     * @return
     */
    public static AttributeCompileGroup getItemAttributeGroup(ItemStack itemStack){
        List<AttributeNBTData> attributeNBTDataList = getItemAttributeNBTData(itemStack);
        return new AttributeCompileGroup(attributeNBTDataList);
    }

    /**
     * 获取物品上的属性
     * 物品包含多个属性 所以他的结果应该是属性组
     * @param itemStackList
     * @return
     */
    public static AttributeCompileGroup getItemAttributeGroup(List<ItemStack> itemStackList){
        List<AttributeNBTData> attributeNBTDataList = new ArrayList<>();
        itemStackList.forEach(itemStack -> {
            attributeNBTDataList.addAll(getItemAttributeNBTData(itemStack));
        });
        return new AttributeCompileGroup(attributeNBTDataList);
    }

    /**
     * 从字符列表中获取属性组
     * @param attributes
     * @return
     */
    public static AttributeCompileGroup getStringAttributeGroup(List<String> attributes){
        List<AttributeNBTData> attributeNBTDataList = new ArrayList<>();
        attributes.forEach(string -> {
            attributeNBTDataList.add(getAttributeNBTData(string));
        });
        return new AttributeCompileGroup(attributeNBTDataList);
    }





    /**
     * 从物品上面重新获取属性合集
     * @param itemStack
     * @return
     */
    public static List<AttributeNBTData> getItemAttributeNBTData(ItemStack itemStack){
        List<AttributeNBTData> attributeNBTDataList = new ArrayList<>();
        // 获取物品的 NBT 标签
        CompoundBinaryTag compoundBinaryTag = (CompoundBinaryTag) itemStack.getTag(Tag.NBT("AttributeModifiers"));
        if(compoundBinaryTag == null || compoundBinaryTag.isEmpty()) return attributeNBTDataList;
        for (String key : compoundBinaryTag.keySet()){
            CompoundBinaryTag attributeTag = compoundBinaryTag.getCompound(key);
            AttributeNBTData attributeData = parseAttributeTag(attributeTag);
            attributeNBTDataList.add(attributeData);
        }
        return attributeNBTDataList;
    }

    /**
     * 从list上面获取属性
     * @param attributes
     * @return
     */
    public static List<AttributeNBTData> getListAttributeNBTData(List<String> attributes){
        List<AttributeNBTData> attributeNBTDataList = new ArrayList<>();
        attributes.forEach(s -> {
            AttributeNBTData attributeNBTData = AttributeManager.getAttributeNBTData(s);
            if(attributeNBTData != null){
                attributeNBTDataList.add(attributeNBTData);
            }
        });
        return attributeNBTDataList;
    }



    /**
     * 解析单个自定义属性标签
     * @param attributeTag 属性复合标签
     * @return AttributeNBTData 对象，如果解析失败则返回 null
     */
    public static AttributeNBTData parseAttributeTag(CompoundBinaryTag attributeTag) {
        // 根据你的自定义结构读取字段
        String attributeKey = attributeTag.getString("AttributeKey", "");
        String attributeName = attributeTag.getString("AttributeName", "");
        String readPattern = attributeTag.getString("Read_Pattern", "");
        double amount = attributeTag.getDouble("Amount", 0.0);
        Integer enchantmentCount = attributeTag.getInt("EnchantmentCount", 0);
        if(attributeKey.isEmpty() || attributeName.isEmpty() || readPattern.isEmpty()) return null;
        // 创建 AttributeNBTData 对象
        return new AttributeNBTData(attributeKey, attributeName, readPattern, amount, enchantmentCount);
    }

    /**
     * 将AttributeNBTData数据合集转换成 字符串 map格式
     * @return
     */
    public static Map<String, String> attributeNBTDataToMap(List<AttributeNBTData> attributeNBTDataList){
        Map<String, String> map = new HashMap<>();
        attributeNBTDataList.forEach(attributeNBTData -> {
            String string;
            if(attributeNBTData.getRead_pattern().equals("Default")){
                string = attributeNBTData.getAmount().toString();
            }else {
                string = attributeNBTData.getAmount() * 100 + "%";
            }
            map.put(attributeNBTData.getAttributeName(), string);
        });
        return map;
    }

    /**
     * 将AttributeCompileGroup数据转换成 字符串 map格式
     * @return
     */
    public static Map<String, String> attributeCompileGroupToMap(AttributeCompileGroup attributeCompileGroup){
        Map<String, String> map = new HashMap<>();
        attributeCompileGroup.getCompileDataMap().values().forEach(compileData -> {
            String string;
            if(compileData.getRead_pattern().equals("Default")){
                string = String.format("%.2f", compileData.total());
            }else {
                string = String.format("%.2f", compileData.total() * 100) + "%";
            }
            map.put(compileData.getAttributeKey(), string);
        });
        return map;
    }


    /**
     * 添加一个属性源
     * @param attributeCompileGroup
     * @param source
     */
    public static void addAttribute(Entity entity, AttributeCompileGroup attributeCompileGroup, String source){
        if(entity instanceof AbstractEntity abstractEntity){
            abstractEntity.addAttribute(attributeCompileGroup, source);
        }else if (entity instanceof RPGPlayer rpgPlayer){
            rpgPlayer.addAttribute(attributeCompileGroup, source);
        }
    }

    /**
     * 移除一个属性
     * @param source
     */
    public static void removeAttribute(Entity entity, String source){
        if(entity instanceof AbstractEntity abstractEntity){
            abstractEntity.removeAttribute(source);
        }else if (entity instanceof RPGPlayer rpgPlayer){
            rpgPlayer.removeAttribute(source);
        }
    }

    /**
     * 获取指定属性的实际结果
     * @param attributeKey
     * @return
     */
    public static Double getAttributeTotal(Entity entity, String attributeKey){
        Double total = 0.0;
        if(entity instanceof AbstractEntity abstractEntity){
            total = abstractEntity.getAttributeTotal(attributeKey);
        }else if (entity instanceof RPGPlayer rpgPlayer){
            total = rpgPlayer.getAttributeTotal(attributeKey);
        }
        return total;
    }

    /**
     * 获取指定属性的实际加算合计
     * @param attributeKey
     * @return
     */
    public static Double getAttributeAmount(Entity entity, String attributeKey){
        Double amount = 0.0;
        if(entity instanceof AbstractEntity abstractEntity){
            amount = abstractEntity.getAttributeAmount(attributeKey);
        }else if (entity instanceof RPGPlayer rpgPlayer){
            amount = rpgPlayer.getAttributeAmount(attributeKey);
        }
        return amount;
    }

    /**
     * 获取指定属性的实际乘算合计
     * @param attributeKey
     * @return
     */
    public static Double getAttributePercent(Entity entity, String attributeKey){
        Double percent = 0.0;
        if(entity instanceof AbstractEntity abstractEntity){
            percent = abstractEntity.getAttributePercent(attributeKey);
        }else if (entity instanceof RPGPlayer rpgPlayer){
            percent = rpgPlayer.getAttributePercent(attributeKey);
        }
        return percent;
    }

    /**
     * 获取这个属性的最大值 如果是null则代表没有
     * @param attributeKey
     * @return
     */
    @Nullable
    public static Double getAttributeMax(Entity entity, String attributeKey){
        Double max = 0.0;
        if(entity instanceof AbstractEntity abstractEntity){
            max = abstractEntity.getAttributePercent(attributeKey);
        }else if (entity instanceof RPGPlayer rpgPlayer){
            max = rpgPlayer.getAttributePercent(attributeKey);
        }
        return max;
    }

    /**
     * 获取这个属性的最小值 如果是null则代表没有
     * @param attributeKey
     * @return
     */
    @Nullable
    public static Double getAttributeMin(Entity entity, String attributeKey){
        Double min = 0.0;
        if(entity instanceof AbstractEntity abstractEntity){
            min = abstractEntity.getAttributePercent(attributeKey);
        }else if (entity instanceof RPGPlayer rpgPlayer){
            min = rpgPlayer.getAttributePercent(attributeKey);
        }
        return min;
    }


    /**
     * 更新实体身上的属性
     */
    public static void updateAttribute(Entity entity){
        if(entity instanceof AbstractEntity abstractEntity){
            abstractEntity.updateAttribute();
        }else if (entity instanceof RPGPlayer rpgPlayer){
            rpgPlayer.updateAttribute();
        }
    }

    /**
     * 处理玩家的原版属性更新
     */
    public static void updateVanilla(Entity entity){
        if(entity instanceof AbstractEntity abstractEntity){
            abstractEntity.updateVanilla();
        }else if (entity instanceof RPGPlayer rpgPlayer){
            rpgPlayer.updateVanilla();
        }
    }



}
