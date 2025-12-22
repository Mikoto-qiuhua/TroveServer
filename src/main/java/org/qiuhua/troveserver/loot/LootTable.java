package org.qiuhua.troveserver.loot;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import net.minestom.server.coordinate.Pos;
import net.minestom.server.entity.attribute.Attribute;
import net.minestom.server.instance.Instance;
import org.apache.commons.math3.distribution.EnumeratedDistribution;
import org.apache.commons.math3.util.Pair;
import org.qiuhua.troveserver.Main;
import org.qiuhua.troveserver.api.loot.AbstractLootReward;
import org.qiuhua.troveserver.loot.loots.ItemLoot;
import org.qiuhua.troveserver.player.RPGPlayer;
import org.qiuhua.troveserver.utils.script.JavaScript;
import org.qiuhua.troveserver.utils.yaml.ConfigurationSection;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

public class LootTable {


    /**
     * 触发需要的条件
     */
    @Getter
    @Setter
    @Accessors(chain = true)
    private String condition = "";


    /**
     * 共享战利品的范围 若为0则不共享
     */
    @Getter
    @Setter
    @Accessors(chain = true)
    private Integer sharingScope = 0;

    /**
     * 最小数量
     */
    @Getter
    @Setter
    @Accessors(chain = true)
    private int min = 1;

    /**
     * 最大数量
     */
    @Getter
    @Setter
    @Accessors(chain = true)
    private int max = 1;

    /**
     * 是否可以重复
     */
    @Getter
    @Setter
    @Accessors(chain = true)
    private boolean repeat = false;

    /**
     * 奖励列表
     */
    @Getter
    private final List<AbstractLootReward> lootRewardList = new ArrayList<>();




    /**
     *
     * @param condition 条件
     * @param sharingScope 共享范围
     */
    public LootTable(String condition, Integer sharingScope){
        this.condition = condition;
        this.sharingScope = sharingScope;
        JavaScript.precompile(condition);
    }

    public LootTable(ConfigurationSection config){
        condition = config.getString("condition", "");
        sharingScope = config.getInt("sharingScope", 0);
        max = config.getInt("max", 1);
        min = config.getInt("min", 1);
        config.getBoolean("repeat", false);
        JavaScript.precompile(condition);

        //处理掉落物表
        List<Map<?,?>> dropLootList = config.getMapList("list");

        dropLootList.forEach(map -> {
            String type = (String) map.get("type");
            switch (type){
                case "ITEM" -> lootRewardList.add(new ItemLoot(map));
            }
        });


    }




    /**
     * 添加一个奖励
     * @param lootReward
     * @return
     */
    public LootTable addLootReward(AbstractLootReward lootReward){
        lootRewardList.add(lootReward);
        return this;
    }

    public LootTable addLootReward(List<AbstractLootReward> lootRewards){
        lootRewardList.addAll(lootRewards);
        return this;
    }



    /**
     * 检查这个掉落标是否激活的条件
     * @param rpgPlayer
     * @return
     */
    public Boolean checkCondition(RPGPlayer rpgPlayer){
        return (Boolean) JavaScript.evaluate(condition, rpgPlayer);
    }


    /**
     * 在指定位置 针对指定玩家运行这个掉落表
     * @param instance
     * @param pos
     * @param triggerPlayer
     * @param isDrop
     * @return
     */
    public void run(Instance instance, Pos pos, RPGPlayer triggerPlayer, Boolean isDrop){
        if(lootRewardList.isEmpty()) return;
        Set<RPGPlayer> rpgPlayers = getEligiblePlayers(instance, pos, triggerPlayer);
        giveLootToPlayers(instance, pos, rpgPlayers, isDrop);
        Main.getLogger().debug("战利品已成功发放给 {} 个玩家", rpgPlayers.size());
    }


    /**
     * 获取符合条件的玩家
     */
    private Set<RPGPlayer> getEligiblePlayers(Instance instance, Pos pos, RPGPlayer triggerPlayer) {
        Set<RPGPlayer> rpgPlayers = new HashSet<>();
        //在看要给多少玩家
        if (sharingScope > 0) {
            rpgPlayers = instance.getNearbyEntities(pos, sharingScope).stream()
                    .filter(entity -> entity instanceof RPGPlayer) //过滤玩家
                    .map(entity -> (RPGPlayer) entity) //转换为 Player
                    .filter(this::checkCondition) //每个玩家都检查条件
                    .collect(Collectors.toSet());
        } else {
            if(checkCondition(triggerPlayer)){
                rpgPlayers.add(triggerPlayer);
            }
        }
        return rpgPlayers;
    }

    /**
     * 给所有玩家给予战利品
     * @param instance
     * @param pos
     * @param rpgPlayers
     * @return
     */
    private void giveLootToPlayers(Instance instance, Pos pos, Set<RPGPlayer> rpgPlayers, Boolean isDrop) {
        for (RPGPlayer rpgPlayer : rpgPlayers) {
            try {
                giveLootToPlayer(instance, pos, rpgPlayer, isDrop);
            } catch (Exception e) {
                Main.getLogger().error("给予 {} 战利品失败 {}", rpgPlayer.getUsername(), e);
            }
        }
    }


    /**
     * 给予单个玩家战利品
     * @param instance
     * @param pos
     * @param rpgPlayer
     */
    private void giveLootToPlayer(Instance instance, Pos pos, RPGPlayer rpgPlayer, Boolean isDrop) {
        if(lootRewardList.isEmpty()) {
            return;
        }
        List<AbstractLootReward> result = spawnLootRewardList(rpgPlayer);
        result.forEach(lootReward -> {
            try {
                if(isDrop){
                    lootReward.run(instance, pos, rpgPlayer);
                }else {
                    lootReward.run(rpgPlayer);
                }
            } catch (Exception e) {
                Main.getLogger().error("执行战利品奖励失败 {} {}", lootReward.getClass().getSimpleName(), e);
            }
        });
        Main.getLogger().debug("{} 获取战利品 {} 个", rpgPlayer.getUsername(), result.size());
    }






    private List<AbstractLootReward> spawnLootRewardList(RPGPlayer rpgPlayer) {
        int count = determineCount();
        //检查是否有足够的属性供选择
        if(count > lootRewardList.size()) {
            count = lootRewardList.size();
        }
        List<AbstractLootReward> result = new ArrayList<>();

        //获取幸运值
        Double lucky = rpgPlayer.getAttribute(Attribute.LUCK).getValue();
        List<Pair<AbstractLootReward, Double>> mutableWeights = applyLuckToWeights(lootRewardList, lucky);

        for(int i = 0; i < count; i++) {
            //为剩余的可选属性创建新的分布
            EnumeratedDistribution<AbstractLootReward> currentDistribution =
                    new EnumeratedDistribution<>(mutableWeights);

            AbstractLootReward lootReward = currentDistribution.sample();
            result.add(lootReward);
            //移除已选择的属性
            if(!repeat){
                mutableWeights.removeIf(pair -> pair.getKey().equals(lootReward));
            }
        }
        return result;
    }

    /**
     * 使用幸运值修改权重
     * 增加的权重为 幸运值*0.1
     * @param originalLootRewardList
     * @param lucky
     * @return
     */
    private List<Pair<AbstractLootReward, Double>> applyLuckToWeights(List<AbstractLootReward> originalLootRewardList, Double lucky) {
        double luckyBonus = lucky * 0.1;
        List<Pair<AbstractLootReward, Double>> result = new ArrayList<>();
        for(AbstractLootReward lootReward : originalLootRewardList) {
            result.add(new Pair<>(lootReward, lootReward.getWeight() + luckyBonus));
        }
        return result;
    }

    /**
     * 确定要生成的数量
     */
    private int determineCount() {
        if (min == max) {
            return min;
        }
        // 使用 ThreadLocalRandom 提高性能
        return ThreadLocalRandom.current().nextInt(min, max + 1);
    }


}
