package org.qiuhua.troveserver.module.role;

import lombok.Getter;
import org.qiuhua.troveserver.utils.yaml.ConfigurationSection;

public class AttributesData {

    /**
     * 属性的id
     */
    @Getter
    private final String id;

    /**
     * 这个占位符的基础值
     */
    @Getter
    private final Double base;

    /**
     * 这个占位符每级提升的值
     */
    @Getter
    private final Double perLevel;

    /**
     * 最大值
     */
    @Getter
    private final Double max;

    /**
     * 最小值
     */
    @Getter
    private final Double min;

    @Getter
    private final String string;

    public AttributesData(String id, Double base, Double perLevel) {
        this.id = id;
        this.base = base;
        this.perLevel = perLevel;
        this.max = null;
        this.min = null;
        this.string = null;
    }

    public AttributesData(String id, Double base, Double perLevel, Double max, Double min) {
        this.id = id;
        this.base = base;
        this.perLevel = perLevel;
        this.max = max;
        this.min = min;
        this.string = null;
    }

    public AttributesData(String id, ConfigurationSection config){
        this.id = id;
        this.base = config.getDouble("base");
        this.perLevel = config.getDouble("per-level");
        Double max = null;
        Double min = null;
        this.string = config.getString("string");
        if(config.contains("max")){
            max = config.getDouble("max");
        }
        if(config.contains("min")){
            min = config.getDouble("min");
        }
        this.max = max;
        this.min = min;
    }


    public Double result(Integer level) {
        double result = base + (perLevel * level);
        // 确保结果在[min, max]范围内
        if (min != null) {
            result = Math.max(min, result);
        }
        if (max != null) {
            result = Math.min(max, result);
        }
        return result;
    }

    public String getStringResult(Integer level){
        double result = base + (perLevel * level);
        // 确保结果在[min, max]范围内
        if (min != null) {
            result = Math.max(min, result);
        }
        if (max != null) {
            result = Math.min(max, result);
        }
        if(string != null && !string.isEmpty()){
            return string.replace("{value}", String.valueOf(result));
        }
        return id + ":" + result;

    }



}
