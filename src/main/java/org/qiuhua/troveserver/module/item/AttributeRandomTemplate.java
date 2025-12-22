package org.qiuhua.troveserver.module.item;

import lombok.Getter;
import org.apache.commons.math3.distribution.EnumeratedDistribution;
import org.apache.commons.math3.util.Pair;
import org.qiuhua.troveserver.utils.yaml.ConfigurationSection;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;

public class AttributeRandomTemplate {

    private final ConfigurationSection config;

    private final String id;

    /**
     * 最小数量
     */
    private final int min;

    /**
     * 最大数量
     */
    private final int max;

    /**
     * 是否可以重复
     */
    private final boolean repeat;

    private final List<Pair<AttributeConfig, Double>> attributeWeights = new ArrayList<>();

    private EnumeratedDistribution<AttributeConfig> baseDistribution;


    public AttributeRandomTemplate(ConfigurationSection config, String id){
        this.id = id;
        this.config = config;
        max = config.getInt("max", 1);
        min = config.getInt("min", 1);
        repeat = config.getBoolean("repeat", false);
        List<Map<?,?>> mapList = config.getMapList("attribute");
        mapList.forEach(map -> {
            String name = map.get("name").toString();
            int min = Integer.parseInt(map.get("min").toString());
            int max = Integer.parseInt(map.get("max").toString());
            double weight = Double.parseDouble(map.get("weight").toString());
            AttributeConfig attributeConfig = new AttributeConfig(name, min, max, weight);
            attributeWeights.add(new Pair<>(attributeConfig, weight));
        });
        if(!attributeWeights.isEmpty()){
            baseDistribution = new EnumeratedDistribution<>(attributeWeights);
        }

    }



    /**
     * 随机生成属性列表
     * @return 生成的属性列表，格式为：["最大生命值加成:15%", "移动速度:30%"]
     */
    public List<String> generateRandomAttributes() {
        List<String> result = new ArrayList<>();

        if(attributeWeights.isEmpty()) {
            return result;
        }

        //确定要生成的属性数量
        int attributeCount = determineAttributeCount();

        //根据是否重复选择不同的策略
        if(repeat) {
            generateWithRepeat(result, attributeCount);
        } else {
            generateWithoutRepeat(result, attributeCount);
        }

        return result;
    }

    /**
     * 允许重复时的生成策略
     */
    private void generateWithRepeat(List<String> result, int count) {
        for(int i = 0; i < count; i++) {
            AttributeConfig config = baseDistribution.sample();
            int value = ThreadLocalRandom.current().nextInt(config.getMax() - config.getMin() + 1) + config.getMin();
            String attributeStr = config.getName().replace("<value>", String.valueOf(value));
            result.add(attributeStr);
        }
    }

    /**
     * 不允许重复时的生成策略
     */
    private void generateWithoutRepeat(List<String> result, int count) {
        //检查是否有足够的属性供选择
        if(count > attributeWeights.size()) {
            count = attributeWeights.size(); // 不能超过可用属性数
        }

        //复制一份可用的配置，避免修改原始数据
        List<Pair<AttributeConfig, Double>> availableWeights = new ArrayList<>(attributeWeights);

        for(int i = 0; i < count; i++) {
            // 为剩余的可选属性创建新的分布
            EnumeratedDistribution<AttributeConfig> currentDistribution =
                    new EnumeratedDistribution<>(availableWeights);

            AttributeConfig config = currentDistribution.sample();
            int value = ThreadLocalRandom.current().nextInt(config.getMax() - config.getMin() + 1) + config.getMin();
            String attributeStr = config.getName().replace("<value>", String.valueOf(value));
            result.add(attributeStr);
            //移除已选择的属性
            availableWeights.removeIf(pair -> pair.getKey().equals(config));
        }
    }





    /**
     * 确定要生成的属性数量
     */
    private int determineAttributeCount() {
        if (min == max) {
            return min;
        }
        // 使用 ThreadLocalRandom 提高性能
        return ThreadLocalRandom.current().nextInt(min, max + 1);
    }


    /**
     * 属性配置内部类
     */
    @Getter
    private static class AttributeConfig {
        private final String name;
        private final int min;
        private final int max;
        private final double weight;

        public AttributeConfig(String name, int min, int max, double weight) {
            this.name = name;
            this.min = min;
            this.max = max;
            this.weight = weight;
        }
    }

}
