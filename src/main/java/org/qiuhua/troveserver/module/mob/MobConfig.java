package org.qiuhua.troveserver.module.mob;

import lombok.Getter;
import net.minestom.server.entity.LivingEntity;
import net.minestom.server.tag.Tag;
import org.qiuhua.troveserver.api.entity.AbstractEntity;
import org.qiuhua.troveserver.module.attribute.AttributeManager;
import org.qiuhua.troveserver.utils.yaml.ConfigurationSection;
import org.qiuhua.troveserver.utils.yaml.file.YamlConfiguration;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class MobConfig {

    @Getter
    private final YamlConfiguration config;

    @Getter
    private final String mobId;

    /**
     * 展示名称
     */
    @Getter
    private final String displayName;

    /**
     * 是否显示血条纹理
     */
    @Getter
    private final Boolean displayHealth;


    @Getter
    private final String mobClassId;



    private final Map<String, MobSettingsData> mobSettingsDataMap = new HashMap<>();



    public MobConfig(String mobId, YamlConfiguration config){
        this.config = config;
        this.mobId = mobId;
        displayName = config.getString("DisplayName", null);
        displayHealth = config.getBoolean("DisplayHealth", false);
        mobClassId = config.getString("MobClassId", null);
        ConfigurationSection settingsSection = config.getConfigurationSection("Settings");
        if(settingsSection != null){
            for (String key : settingsSection.getKeys(false)){
                MobSettingsData mobSettingsData = new MobSettingsData(key, settingsSection.getConfigurationSection(key));
                mobSettingsDataMap.put(key, mobSettingsData);
            }
        }
    }


    /**
     * 给一个实体添加配置
     * @param settingsId
     * @param entity
     */
    public void setSettings(String settingsId, AbstractEntity entity){
        MobSettingsData mobSettingsData  = mobSettingsDataMap.get(settingsId);
        if(mobSettingsData == null){
            mobSettingsData = mobSettingsDataMap.get("default");
        }
        if(mobSettingsData == null) return;
        //添加属性
        entity.addAttribute(mobSettingsData.getAttributeCompileGroup(), "base_attribute_entity");
        //添加tag
        setEntityTag(entity, mobSettingsData.getTagMap());
        entity.setDropLootTable(mobSettingsData.getDropLootTable());
        entity.setRewardLootTable(mobSettingsData.getRewardLootTable());
    }


    /**
     * 给实体添加自定义tag
     * @param entity
     * @param tagMap
     * @return
     */
    private static LivingEntity setEntityTag(LivingEntity entity, Map<String, Object> tagMap){
        tagMap.forEach((key, value) -> {
            if (value instanceof String) {
                entity.setTag(Tag.String(key), (String) value);
            } else if (value instanceof Integer) {
                entity.setTag(Tag.Integer(key), (Integer) value);
            } else if (value instanceof Boolean) {
                entity.setTag(Tag.Byte(key), (byte) ((Boolean) value ? 1 : 0));
            } else if (value instanceof Double) {
                entity.setTag(Tag.Double(key), (Double) value);
            } else if (value instanceof Float) {
                entity.setTag(Tag.Float(key), (Float) value);
            }
        });
        return entity;
    }


}
