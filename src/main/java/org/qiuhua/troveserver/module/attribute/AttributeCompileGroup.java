package org.qiuhua.troveserver.module.attribute;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.qiuhua.troveserver.Main;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@ToString
public class AttributeCompileGroup {

    /**
     * 存储该组的全部属性数据 key相同的应该合并为一个
     */
    @Getter
    private final Map<String, CompileData> compileDataMap = new HashMap<>();

    private final List<AttributeNBTData> attributeNBTDataList = new ArrayList<>();

    public AttributeCompileGroup(AttributeNBTData attributeNBTData){
        String attributeKey = attributeNBTData.getAttributeKey();
        compileDataMap.put(attributeKey, new CompileData(attributeNBTData));
        this.attributeNBTDataList.add(attributeNBTData);
    }

    public AttributeCompileGroup(List<AttributeNBTData> attributeNBTDataList){
        attributeNBTDataList.forEach(attributeNBTData -> {
            if(attributeNBTData == null) return;
            String attributeKey = attributeNBTData.getAttributeKey();
            if(!compileDataMap.containsKey(attributeKey)){
                compileDataMap.put(attributeKey, new CompileData(attributeNBTData));
            }else {
                CompileData compileData = compileDataMap.get(attributeKey);
                compileData.addAttributeData(attributeNBTData);
            }
            this.attributeNBTDataList.add(attributeNBTData);
        });

    }





    /**
     * 单个数据数据的记录
     */
    @ToString
    public static class CompileData {

        /**
         * 属性key名称
         */
        @Getter
        private final String attributeKey;

        /**
         * 属性值 数值类型
         */
        @Getter
        @Setter
        private Double amount = 0.0;

        /**
         * 属性值 百分比类型
         */
        @Getter
        @Setter
        private Double percent = 0.0;

        @Getter
        private String read_pattern = "Default";

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


        public CompileData(AttributeNBTData attributeNBTData){
            this.attributeKey = attributeNBTData.getAttributeKey();
            if(attributeNBTData.getRead_pattern().equals("Percent")){
                this.percent = attributeNBTData.getAmount();
            }else {
                this.amount = attributeNBTData.getAmount();
            }
            AttributeDataConfig attributeDataConfig = AttributeManager.getAttributesDataConfigMap().get(this.attributeKey);
            if(attributeDataConfig != null){
                this.read_pattern = attributeDataConfig.getRead_pattern();
                this.max = attributeDataConfig.getMax();
                this.min = attributeDataConfig.getMin();
            }
        }

        public CompileData(String attributeKey){
            this.attributeKey = attributeKey;
            AttributeDataConfig attributeDataConfig = AttributeManager.getAttributesDataConfigMap().get(this.attributeKey);
            if(attributeDataConfig != null){
                this.read_pattern = attributeDataConfig.getRead_pattern();
                this.max = attributeDataConfig.getMax();
                this.min = attributeDataConfig.getMin();
            }
        }

        /**
         * 获取总和
         * @return
         */
        public Double total(){
            Double result;
            if(read_pattern.equals("Default")){
                result = amount * (1 + percent);
            }else {
                result = percent;
            }
            // 确保结果在[min, max]范围内
            if (min != null) {
                result = Math.max(min, result);
            }
            if (max != null) {
                result = Math.min(max, result);
            }

            return result;
        }


        /**
         * 追加一个属性数据进去
         * @param attributeNBTData
         */
        public void addAttributeData(AttributeNBTData attributeNBTData){
            if(attributeNBTData.getRead_pattern().equals("Percent")){
                this.percent = percent + attributeNBTData.getAmount();
            }else {
                this.amount = amount + attributeNBTData.getAmount();
            }
        }

        public void addAttributeData(CompileData compileData){
            this.amount = amount + compileData.amount;
            this.percent = percent + compileData.percent;
        }


    }



}
