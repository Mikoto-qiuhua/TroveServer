package org.qiuhua.troveserver.module.mob;

import lombok.Getter;
import org.qiuhua.troveserver.loot.LootTable;
import org.qiuhua.troveserver.module.attribute.AttributeCompileGroup;
import org.qiuhua.troveserver.module.attribute.AttributeManager;
import org.qiuhua.troveserver.utils.yaml.ConfigurationSection;

import java.util.HashMap;
import java.util.Map;

public class MobSettingsData {

    @Getter
    private final String settingsId;

    private final ConfigurationSection config;

    /**
     * tag列表
     */
    @Getter
    private final Map<String, Object> tagMap = new HashMap<>();

    /**
     * 属性数据
     */
    @Getter
    private final AttributeCompileGroup attributeCompileGroup;

    /**
     * 掉落物战利品表
     */
    @Getter
    private LootTable dropLootTable;

    /**
     * 奖励战利品表
     */
    @Getter
    private LootTable rewardLootTable;

    public MobSettingsData(String settingsId, ConfigurationSection config){
        this.settingsId = settingsId;
        this.config = config;
        ConfigurationSection tagSection = config.getConfigurationSection("tag");
        if(tagSection != null){
            for (String key : tagSection.getKeys(false)){
                tagMap.put(key, tagSection.get(key));
            }
        }
        attributeCompileGroup = AttributeManager.getStringAttributeGroup(config.getStringList("attributes"));
        ConfigurationSection dropSection = config.getConfigurationSection("drop");
        if(dropSection != null){
            dropLootTable = new LootTable(dropSection);
        }

        ConfigurationSection rewardSection = config.getConfigurationSection("reward");
        if(rewardSection != null){
            rewardLootTable = new LootTable(rewardSection);
        }
    }




}
