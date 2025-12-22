package org.qiuhua.troveserver.module.attribute;


import org.jetbrains.annotations.Nullable;
import org.qiuhua.troveserver.Main;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class EntityAttributesData {

    /**
     * 全部属性源
     */
    private final ConcurrentHashMap<String, AttributeCompileGroup> attributeSourceMap = new ConcurrentHashMap<>();

    /**
     * 全部属性数据的缓存
     */
    private final ConcurrentHashMap<String, AttributeCompileGroup.CompileData> attributeCompileDataMap = new ConcurrentHashMap<>();


    public EntityAttributesData(){
        //生成全部属性的CompileData 每个属性只有一个CompileData
        AttributeManager.getAttributesDataConfigMap().keySet().forEach(key -> {
            AttributeCompileGroup.CompileData compileData = new AttributeCompileGroup.CompileData(key);
            this.attributeCompileDataMap.put(key, compileData);
        });
    }

    /**
     * 添加一个属性源
     * @param attributeCompileGroup
     * @param source
     */
    public void addAttribute(AttributeCompileGroup attributeCompileGroup, String source){
        this.attributeSourceMap.put(source, attributeCompileGroup);
    }

    /**
     * 移除一个属性源
     * @param source
     */
    public void removeAttribute(String source){
        this.attributeSourceMap.remove(source);
    }


    /**
     * 获取全部属性的最终计算值
     * @return
     */
    public Map<String, Double> getAttributeMap(){
        Map<String, Double> map = new HashMap<>();
        attributeCompileDataMap.forEach((key, value) -> {
            map.put(key, value.total());
        });
        return map;
    }


    public Double getAttributeTotal(String attributeKey){
        Double result = 0.0;
        if(attributeCompileDataMap.containsKey(attributeKey)){
            result = attributeCompileDataMap.get(attributeKey).total();
        }
        return result;
    }

    public Double getAttributeAmount(String attributeKey){
        Double result = 0.0;
        if(attributeCompileDataMap.containsKey(attributeKey)){
            result = attributeCompileDataMap.get(attributeKey).getAmount();
        }
        return result;
    }

    public Double getAttributePercent(String attributeKey){
        Double result = 0.0;
        if(attributeCompileDataMap.containsKey(attributeKey)){
            result = attributeCompileDataMap.get(attributeKey).getPercent();
        }
        return result;
    }

    @Nullable
    public Double getAttributeMax(String attributeKey){
        return attributeCompileDataMap.get(attributeKey).getMax();
    }

    @Nullable
    public Double getAttributeMin(String attributeKey){
        return attributeCompileDataMap.get(attributeKey).getMin();
    }

    /**
     * 从属性源中获取属性添加进预编译后的缓存中
     */
    public void update(){
        attributeCompileDataMap.clear();
        //生成全部属性的CompileData 每个属性只有一个CompileData
        AttributeManager.getAttributesDataConfigMap().keySet().forEach(key -> {
            AttributeCompileGroup.CompileData compileData = new AttributeCompileGroup.CompileData(key);
            this.attributeCompileDataMap.put(key, compileData);
        });
        //先处理全部基础属性
        attributeSourceMap.values().forEach(attributeCompileGroup -> {
            attributeCompileGroup.getCompileDataMap().values().forEach(compileData -> {
                String attributeKey = compileData.getAttributeKey();
                if(attributeCompileDataMap.containsKey(attributeKey)){
                    attributeCompileDataMap.get(attributeKey).addAttributeData(compileData);
                }
            });
        });
        //处理映射
        attributeSourceMap.values().forEach(attributeCompileGroup -> {
            attributeCompileGroup.getCompileDataMap().values().forEach(compileData -> {
                String attributeKey = compileData.getAttributeKey();
                AttributeDataConfig attributeDataConfig = AttributeManager.getAttributeDataConfig(attributeKey);
                if(attributeDataConfig != null){
                    attributeDataConfig.getMappingDataList().forEach(mappingData -> {
                        //这里是拿到这个属性本身的数值 然后传递给他的映射内计算
                        Double value = attributeCompileDataMap.get(attributeKey).total();
                        AttributeCompileGroup.CompileData compileData1 = mappingData.getCompileData(Map.of(
                                "value", value
                        ));
                        if(compileData1 != null){
                            attributeCompileDataMap.get(compileData1.getAttributeKey()).addAttributeData(compileData1);
                        }
                    });
                }
            });
        });
    }









}
