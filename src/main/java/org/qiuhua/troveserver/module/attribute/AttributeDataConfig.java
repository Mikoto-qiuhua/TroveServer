package org.qiuhua.troveserver.module.attribute;

import lombok.Getter;
import org.qiuhua.troveserver.utils.yaml.ConfigurationSection;

import java.util.ArrayList;
import java.util.List;

public class AttributeDataConfig {

    @Getter
    private final ConfigurationSection config;

    @Getter
    private final List<String> names = new ArrayList<>();

    @Getter
    private final String attributeKey;

    @Getter
    private final List<MappingData> mappingDataList = new ArrayList<>();

    /**
     * Default 先加算 在乘算百分比  Percent仅加算百分比
     */
    @Getter
    private final String read_pattern;

    /**
     * 属性的最大值 在最后计算的时候是不会超过这个值
     */
    @Getter
    private Double max = null;

    /**
     * 属性的最小值 在最后计算的时候不会低于这个值
     */
    @Getter
    private Double min = null;

    public AttributeDataConfig(ConfigurationSection config, String attributeKey){
        this.config = config;
        this.attributeKey = attributeKey;
        this.read_pattern = config.getString("read_pattern", "Default");
        if(config.contains("max")){
            this.max = config.getDouble("max");
        }
        if(config.contains("min")){
            this.min = config.getDouble("min");
        }
        List<String> names = config.getStringList("names");
        if(names.isEmpty()){
            this.names.add(attributeKey);
        }else {
            this.names.addAll(names);
        }
        List<String> mapping = config.getStringList("mapping");
        if(!mapping.isEmpty()){
            mapping.forEach(s -> {
                mappingDataList.add(new MappingData(s, attributeKey));
            });
        }
    }







}
