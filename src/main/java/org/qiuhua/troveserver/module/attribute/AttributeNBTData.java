package org.qiuhua.troveserver.module.attribute;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@ToString
public class AttributeNBTData {

    /**
     * 属性名称
     */
    @Getter
    private final String attributeName;

    /**
     * 属性key名称
     */
    @Getter
    private final String attributeKey;

    /**
     * 计算方式
     */
    @Getter
    private final String read_pattern;

    /**
     * 属性值
     */
    @Getter
    private final Double amount;

    @Getter
    @Setter
    private Integer enchantmentCount = 1;


    public AttributeNBTData(String attributeKey, String attributeName, String readPattern, Double amount){
        this.attributeName = attributeName;
        this.attributeKey = attributeKey;
        this.read_pattern = readPattern;
        this.amount = amount;
    }

    public AttributeNBTData(String attributeKey, String attributeName, String readPattern, Double amount, Integer enchantmentCount){
        this.attributeName = attributeName;
        this.attributeKey = attributeKey;
        this.read_pattern = readPattern;
        this.amount = amount;
    }



}
