package org.qiuhua.troveserver.module.item;

import lombok.Getter;
import net.minestom.server.item.ItemStack;
import net.minestom.server.item.Material;
import org.jetbrains.annotations.Nullable;
import org.qiuhua.troveserver.Main;
import org.qiuhua.troveserver.utils.StringUtils;
import org.qiuhua.troveserver.utils.yaml.ConfigurationSection;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


public class ItemCompileData {
    /**
     * 默认编译好的物品对象
     */
    private final ItemStack itemStack;
    @Getter
    private final ConfigurationSection config;
    /**
     * 物品id
     */
    @Getter
    private final String itemId;

    /**
     * 物品的原始lore
     */
    private final List<String> lore;

    /**
     * 物品的原始名称
     */
    @Getter
    @Nullable
    private final String custom_name;

    /**
     * 物品的材质
     */
    @Getter
    private final Material material;

    /**
     * 物品的最大堆叠数量
     */
    @Getter
    private final int max_stack_size;

    /**
     * 是否隐藏tip
     */
    @Getter
    private final boolean hide_tooltip;

    /**
     * 自定义tag
     */
    @Getter
    private final Map<String, Object> tagMap = new HashMap<>();

    /**
     * 物品的冷却组
     */
    @Getter
    @Nullable
    private final String cooldownGroup;

    /**
     * 物品所使用的模型id
     */
    @Getter
    @Nullable
    private final String model_id;

    /**
     * 这里是obj 因为他有可能是字符串 也有可能是list
     */
    @Nullable
    private final Object attributes;

    public ItemCompileData(ConfigurationSection config, String itemId){
        this.config = config;
        this.itemId = itemId;
        lore = StringUtils.colorCodeConversion(config.getStringList("lore"));
        custom_name = StringUtils.colorCodeConversion(config.getString("custom_name", null));
        String strMaterial = "minecraft:" + config.getString("material", "air");
        material = Material.fromKey(strMaterial);
        max_stack_size = config.getInt("max_stack_size", 1);
        hide_tooltip = config.getBoolean("hide_tooltip", false);
        ConfigurationSection tagSection = config.getConfigurationSection("tag");
        if(tagSection != null){
            for (String key : tagSection.getKeys(false)){
                tagMap.put(key, tagSection.get(key));
            }
        }
        cooldownGroup = config.getString("cooldownGroup", null);
        model_id = config.getString("model_id", null);
        attributes = config.get("attributes", null);
        this.itemStack = ItemManager.buildItem(this);
    }

    /**
     * 会检查这个物品是有随机的词条模版
     * @return
     */
    public ItemStack getItemStack(){
        if(attributes instanceof String){
            return ItemManager.buildItem(this);
        }
        return this.itemStack;
    }

    /**
     * 尝试获取属性
     * @return
     */
    public List<String> getAttributes() {
        switch (attributes) {
            case null -> {
                return List.of();
            }
            case List<?> list -> {
                return list.stream()
                        .filter(String.class::isInstance)
                        .map(String.class::cast)
                        .collect(Collectors.toCollection(ArrayList::new));
            }
            case String templateId -> {
                AttributeRandomTemplate template = ItemManager.allAttributeRandomTemplate.get(templateId);
                if (template != null) {
                    List<String> generated = template.generateRandomAttributes();
                    return generated != null ? new ArrayList<>(generated) : List.of();
                }
                return List.of();
            }
            default -> {
            }
        }
        return List.of();
    }


    /**
     * 获取这个物品描述的副本
     * @return
     */
    public List<String> getLore() {
        return new ArrayList<>(lore);
    }


}
