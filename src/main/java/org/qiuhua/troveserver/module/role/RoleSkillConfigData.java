package org.qiuhua.troveserver.module.role;

import lombok.Getter;
import org.jetbrains.annotations.Nullable;
import org.qiuhua.troveserver.Main;
import org.qiuhua.troveserver.api.meta.IMeta;
import org.qiuhua.troveserver.api.skill.AbstractSkillMechanic;
import org.qiuhua.troveserver.utils.StringUtils;
import org.qiuhua.troveserver.utils.yaml.ConfigurationSection;
import org.qiuhua.troveserver.utils.yaml.file.YamlConfiguration;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * 这个是属于技能配置文件的配置数据
 */
public class RoleSkillConfigData implements IMeta {

    /**
     * 技能自带的元数据
     */
    @Getter
    private final Map<String, Object> meta = new HashMap<>();

    /**
     * 技能名字
     */
    @Getter
    private final String skillName;


    /**
     * 技能机制
     */
    @Getter
    @Nullable
    private final AbstractSkillMechanic skillMechanic;

    /**
     * 存储了当前技能提供的属性数据
     */
    @Getter
    private final Map<String, AttributesData> attributesDataMap = new HashMap<>();

    /**
     * 自定义数据包
     */
    private final Map<String, Object> packet = new HashMap<>();



    public RoleSkillConfigData(String skillName, YamlConfiguration config, @Nullable AbstractSkillMechanic skillMechanic){
        this.skillMechanic = skillMechanic;
        this.skillName = skillName;
        ConfigurationSection metaSection = config.getConfigurationSection("Meta");
        for (String key : metaSection.getKeys(false)){
            meta.put(key, metaSection.get(key));
        }
        //加载属性数据
        ConfigurationSection attributesSection = config.getConfigurationSection("Attributes");
        if(attributesSection != null){
            for(String key : attributesSection.getKeys(false)){
                AttributesData attributesData = new AttributesData(key, attributesSection.getConfigurationSection(key));
                attributesDataMap.put(key, attributesData);
            }
        }
        //处理自定义数据包
        ConfigurationSection packetSection = config.getConfigurationSection("Packet");
        if(packetSection != null){
            for (String key : packetSection.getKeys(false)) {
                packet.put(key, packetSection.get(key));
            }
        }


        Main.getLogger().debug("技能 {} 加载 {}", skillName, toString());
    }

    @Override
    public String toString(){
        return "skillId: " + (skillMechanic == null ? "null" : skillMechanic.getSkillId()) +
                ", metaSize: " + meta.size() +
                ", attributesDataSize: " + attributesDataMap.size() +
                ", packetSize: " + packet.size();
    }


    /**
     * 获取自定义数据包 会替换内部的属性占位符
     * @return
     */
    public Map<String, Object> getPacket(){
        Map<String, Object> packet = new HashMap<>(this.packet);
        //创建批量替换占位符
        Map<String, String> replacements = new HashMap<>();
        //添加属性数据
        attributesDataMap.forEach((id, attributesData) -> {
            Double result = attributesData.result(1);
            String newKey = "<" + id + ">";
            replacements.put(newKey, result.toString());
        });
        //对每个内容都尝试替换
        for (Map.Entry<String, Object> entry : packet.entrySet()) {
            String key = entry.getKey();
            Object value = entry.getValue();
            if(value instanceof List){
                List<String> list = (List<String>) value;
                list = StringUtils.replacePlaceholders(list, replacements);
                packet.put(key, list);
            }else {
                packet.put(key, StringUtils.replacePlaceholders(value.toString(), replacements));
            }
        }
        return packet;
    }


    /**
     * 这里面会解析数据中的占位符
     * @return
     */
    public Map<String, Object> getMetaData(Integer level) {
        Map<String, Object> result = new HashMap<>(meta);
        for (Map.Entry<String, Object> entry : meta.entrySet()) {
            Object value = entry.getValue();
            if (value instanceof String stringValue) {
                // 使用正则表达式提取 <> 中的内容
                String extracted = extractContentFromAngleBrackets(stringValue);
                AttributesData attributesData = attributesDataMap.get(extracted);
                if(attributesData != null){
                    value = attributesData.result(level);
                }
            }
            result.put(entry.getKey(), value);
        }
        return result;
    }





    private static String extractContentFromAngleBrackets(String input) {
        if (input == null) return null;
        // 正则表达式：匹配 < 和 > 之间的内容（非贪婪模式）
        Pattern pattern = Pattern.compile("<(.*?)>");
        Matcher matcher = pattern.matcher(input);

        if (matcher.find()) {
            return matcher.group(1); // 返回第一个匹配的内容
        }
        // 如果没有找到 <>，返回原字符串
        return input;
    }






}
