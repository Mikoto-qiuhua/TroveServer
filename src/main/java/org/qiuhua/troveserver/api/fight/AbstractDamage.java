package org.qiuhua.troveserver.api.fight;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import net.kyori.adventure.text.Component;
import net.minestom.server.coordinate.Point;
import net.minestom.server.entity.Entity;
import net.minestom.server.entity.Player;
import net.minestom.server.entity.damage.Damage;
import net.minestom.server.entity.damage.DamageType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.qiuhua.troveserver.api.attribute.IAttribute;
import org.qiuhua.troveserver.skill.mechanics.ProjectileMechanic;

import java.util.HashMap;
import java.util.Map;

public abstract class AbstractDamage extends Damage {


    /**
     * 抛射物实体
     */
    @Getter
    @Setter
    @Accessors(chain=true)
    private ProjectileMechanic projectile;


    /**
     * 攻击者的属性合集
     */
    private Map<String, Double> sourceAttributeMap = new HashMap<>();

    /**
     * 防御者的属性合集
     */
    private Map<String, Double> attackerAttributeMap = new HashMap<>();

    /**
     * 基础倍率
     */
    @Getter
    @Setter
    private Double multiplier;

    /**
     * 是否暴击了
     */
    @Getter
    @Setter
    private boolean isCriticalHit = false;



    /**
     *
     * @param source         造成伤害的实体
     * @param attacker       受到攻击的实体
     * @param sourcePosition 造成伤害的位置
     * @param multiplier 计算攻击的倍率
     */
    public AbstractDamage(@Nullable Entity source, @Nullable Entity attacker, @Nullable Point sourcePosition, Double multiplier) {
        super(DamageType.GENERIC, source, attacker, sourcePosition, 0);
        this.multiplier = multiplier;
        //记录防御者的属性
        if(attacker instanceof IAttribute){
            attackerAttributeMap = ((IAttribute) attacker).getEntityAttributesData().getAttributeMap();
        }
        //记录攻击者的属性
        if(source instanceof IAttribute){
            sourceAttributeMap = ((IAttribute) source).getEntityAttributesData().getAttributeMap();
        }

    }

    /**
     * 计算伤害
     */
    public abstract float calculateDamage();


    /**
     * 获取攻击者的某个指定属性
     * @param attributeKey
     * @return
     */
    public Double getSourceAttribute(String attributeKey){
        return sourceAttributeMap.getOrDefault(attributeKey, 0.0);
    }


    /**
     * 获取防御者的某个指定属性
     * @param attributeKey
     * @return
     */
    public Double getAttackerAttribute(String attributeKey){
        return attackerAttributeMap.getOrDefault(attributeKey, 0.0);
    }

    /**
     * 给防御者添加一个属性
     * @param attributeKey
     * @param value
     */
    public void addAttackerAttribute(String attributeKey, Double value){
        attackerAttributeMap.merge(attributeKey, value, Double::sum);
    }


    /**
     * 给攻击者添加一个属性
     * @param attributeKey
     * @param value
     */
    public void addSourceAttribute(String attributeKey, Double value){
        sourceAttributeMap.merge(attributeKey, value, Double::sum);
    }


    /**
     * 生成全息文本的消息提示
     */
    public abstract void spawnHDMessage();


    /**
     * 构建死亡时的玩家消息
     * @param killed
     * @return
     */
    public abstract @Nullable Component buildDeathMessage(@NotNull Player killed);

    /**
     * 构建死亡时屏幕上面的那个文本
     * @param killed
     * @return
     */
    @Override
    public abstract @Nullable Component buildDeathScreenText(@NotNull Player killed);










}
