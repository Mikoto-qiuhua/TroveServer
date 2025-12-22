package org.qiuhua.troveserver.api.skill;

import lombok.Getter;
import net.minestom.server.entity.Entity;
import org.jetbrains.annotations.NotNull;
import org.qiuhua.troveserver.api.condition.ISkillCondition;
import org.qiuhua.troveserver.skill.SkillMetadata;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 单个技能机制？
 * 我期望是 全部玩家使用同一个机制对象 然后单独处理冷却机制
 */
public abstract class AbstractSkillMechanic {

    /**
     * 技能id
     */
    @Getter
    @NotNull
    protected final String skillId;

    /**
     * 施法者条件列表
     */
    @Getter
    protected final List<ISkillCondition> conditions = new ArrayList<>();
    /**
     * 技能目标条件列表
     */
    @Getter
    protected final List<ISkillCondition> conditionsTarget = new ArrayList<>();
    /**
     * 技能触发者条件列表
     */
    @Getter
    protected final List<ISkillCondition> conditionsTrigger = new ArrayList<>();

    /**
     * 这个技能的冷却列表
     */
    protected final ConcurrentHashMap<UUID, Long> cooldowns = new ConcurrentHashMap<>();

    /**
     * 技能的全局冷却
     */
    protected static final ConcurrentHashMap<UUID, Long> globalCooldowns = new ConcurrentHashMap<>();

    /**
     * 这个技能的父系技能对象
     */
    @Getter
    protected final AbstractSkillMechanic parent;


    protected AbstractSkillMechanic(@NotNull String skillName) {
        this.skillId = skillName;
        this.parent = null;
    }

    protected AbstractSkillMechanic(@NotNull String skillId, AbstractSkillMechanic parent) {
        this.skillId = skillId;
        this.parent = parent;
    }


    /**
     * 执行这个技能
     * @param skillMetadata 技能元数据 全部动态数据都从此处获取
     * @param casterEntity 施法者
     * @return
     */
    public abstract boolean execute(SkillMetadata skillMetadata, Entity casterEntity);



    /**
     * 检查这个技能是否在冷却
     *
     * @param casterEntity 施法者
     * @return
     */
    public boolean isCooldown(Entity casterEntity){
        return this.getCooldown(casterEntity) > 0.0F;
    }

    /**
     * 获取技能冷却时间
     * @param casterEntity
     * @return
     */
    public float getCooldown(Entity casterEntity) {
        long time = System.currentTimeMillis();
        long last = this.cooldowns.getOrDefault(casterEntity.getUuid(), 0L);
        return time >= last ? 0.0F : (float)(last - time) / 1000.0F;
    }

    /**
     * 设置技能的下个冷却时间
     * @param casterEntity
     * @param cooldown 单位秒
     */
    public void setCooldown(Entity casterEntity, double cooldown) {
        if (cooldown > (double)0.0F) {
            this.cooldowns.put(casterEntity.getUuid(), (long)((double)System.currentTimeMillis() + cooldown * (double)1000.0F));
        } else {
            this.cooldowns.remove(casterEntity.getUuid());
        }
    }


    /**
     * 设置全局冷却
     * @param casterEntity
     * @param cooldown
     */
    public void setGlobalCooldown(Entity casterEntity, double cooldown){
        if (cooldown > (double)0.0F) {
            globalCooldowns.put(casterEntity.getUuid(), (long)((double)System.currentTimeMillis() + cooldown * (double)1000.0F));
        } else {
            globalCooldowns.remove(casterEntity.getUuid());
        }
    }

    /**
     * 获取技能冷却时间
     * @param casterEntity
     * @return
     */
    public static float getGlobalCooldown(Entity casterEntity) {
        long time = System.currentTimeMillis();
        long last = globalCooldowns.getOrDefault(casterEntity.getUuid(), 0L);
        return time >= last ? 0.0F : (float)(last - time) / 1000.0F;
    }

    /**
     * 是否处于全局冷却中
     * @param casterEntity
     * @return
     */
    public static boolean isGlobalCooldown(Entity casterEntity){
        return getGlobalCooldown(casterEntity) > 0.0F;
    }



}
