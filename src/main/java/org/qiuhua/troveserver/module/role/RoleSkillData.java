package org.qiuhua.troveserver.module.role;


import lombok.Getter;
import org.qiuhua.troveserver.api.meta.IMeta;
import org.qiuhua.troveserver.utils.yaml.ConfigurationSection;

import java.util.HashMap;
import java.util.Map;

/**
 * 这个是属于角色的技能配置数据
 */
public class RoleSkillData implements IMeta {


    /**
     * 触发器 -1=左键  其余为对应的快捷栏按键 正常情况只支持到0-3
     * 0为右键技能
     * 1-3为三个主动技能
     */
    @Getter
    private final String trigger;

    /**
     * 元数据 会覆盖技能配置本身的那个数据
     * 技能配置那边的数据为全部默认使用的数据值 也可以添加自定义的
     * 仅支持基础类型数据
     */
    @Getter
    private final Map<String, Object> meta = new HashMap<>();

    @Getter
    private final String skillName;

    @Getter
    private final RoleSkillConfigData roleSkillConfigData;

    public RoleSkillData(String skillName, ConfigurationSection config){
        this.skillName = skillName;
        trigger = config.getString("trigger");
        roleSkillConfigData = RoleManager.skillMechanicMap.get(skillName);
        if(roleSkillConfigData != null){
            meta.putAll(roleSkillConfigData.getMeta());
        }
        ConfigurationSection metaSection = config.getConfigurationSection("meta");
        if(metaSection != null){
            for (String key : metaSection.getKeys(false)){
                meta.put(key, metaSection.get(key));
            }
        }
    }


    /**
     * 这里面会解析数据中的占位符
     * @return
     */
    public Map<String, Object> getMetaData(Integer level) {
        Map<String, Object> result = new HashMap<>(meta);

        for (Map.Entry<String, Object> entry : meta.entrySet()) {
            Object value = entry.getValue();

            if (value instanceof String stringValue && roleSkillConfigData != null) {
                // 使用正则表达式提取 <> 中的内容
                String extracted = extractContentFromAngleBrackets(stringValue);
                AttributesData attributesData = roleSkillConfigData.getAttributesDataMap().get(extracted);
                if(attributesData != null){
                    value = attributesData.result(level);
                }
            }
            result.put(entry.getKey(), value);
        }

        return result;
    }



    private String extractContentFromAngleBrackets(String input) {
        if (input == null) return null;

        // 正则表达式：匹配 < 和 > 之间的内容（非贪婪模式）
        java.util.regex.Pattern pattern = java.util.regex.Pattern.compile("<(.*?)>");
        java.util.regex.Matcher matcher = pattern.matcher(input);
        if (matcher.find()) {
            return matcher.group(1); // 返回第一个匹配的内容
        }
        // 如果没有找到 <>，返回原字符串
        return input;
    }












}
