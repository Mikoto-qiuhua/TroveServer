package org.qiuhua.troveserver.api.entity;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import net.minestom.server.MinecraftServer;
import net.minestom.server.coordinate.Pos;
import net.minestom.server.entity.EntityCreature;
import net.minestom.server.entity.EntityType;

import net.minestom.server.entity.attribute.Attribute;
import net.minestom.server.entity.attribute.AttributeInstance;
import net.minestom.server.entity.attribute.AttributeModifier;
import net.minestom.server.entity.attribute.AttributeOperation;
import net.minestom.server.entity.damage.Damage;
import net.minestom.server.instance.Instance;
import org.jetbrains.annotations.Nullable;
import org.qiuhua.troveserver.Main;
import org.qiuhua.troveserver.api.attribute.IAttribute;
import org.qiuhua.troveserver.api.fight.AbstractDamage;
import org.qiuhua.troveserver.loot.LootTable;
import org.qiuhua.troveserver.module.attribute.AttributeCompileGroup;
import org.qiuhua.troveserver.module.attribute.EntityAttributesData;
import org.qiuhua.troveserver.module.attribute.config.AttributeConfig;
import org.qiuhua.troveserver.module.attribute.event.EntityAttributeAddEvent;
import org.qiuhua.troveserver.module.attribute.event.EntityAttributeRemoveEvent;
import org.qiuhua.troveserver.module.attribute.event.EntityAttributeUpdateEvent;
import org.qiuhua.troveserver.player.RPGPlayer;

import java.util.HashMap;
import java.util.Map;


public abstract class AbstractEntity extends EntityCreature implements IAttribute{


    /**
     * 属性数据 临时性的无需缓存
     */
    @Getter
    private final EntityAttributesData entityAttributesData = new EntityAttributesData();

    /**
     * 掉落物战利品表
     */
    @Getter
    @Setter
    @Accessors(chain = true)
    @Nullable
    private LootTable dropLootTable = null;

    /**
     * 奖励战利品表
     */
    @Getter
    @Setter
    @Accessors(chain = true)
    @Nullable
    private LootTable rewardLootTable = null;



    /**
     * 这个实体的持续时间
     * null为永久持续
     */
    @Getter
    @Setter
    @Accessors(chain=true)
    private Integer removeDuration = null;

    public AbstractEntity(EntityType entityType) {
        super(entityType);
    }


    public abstract void spawnEntity(Instance instance, Pos pos);

    /**
     * 这里重写了更新实体
     * 实现倒计时移除 当duration=0时将实体移除
     * @param time
     */
    @Override
    public void update(long time){
        if(removeDuration != null){
            if(removeDuration <= 0){
                remove();
                Main.getLogger().debug("实体 {} 因已达到存活时间而卸载", this);
                return;
            }else {
                removeDuration--;
            }
        }
        super.update(time);

    }


    /**
     * 这里重写了damage 因为实体受伤需要显示全息文本
     * @param damage
     * @return
     */
    @Override
    public boolean damage(Damage damage){
        if(damage.getAmount() <= 0) return true;
        boolean isCancelled = !super.damage(damage);
        if(!isCancelled && damage instanceof AbstractDamage abstractDamage){
            abstractDamage.spawnHDMessage();
        }
        return isCancelled;
    }

    @Override
    public void remove() {
        super.remove();
    }

    /**
     * 死亡时尝试生成掉落物和奖励
     */
    @Override
    public void kill(){
        if(lastDamage.getSource() instanceof RPGPlayer rpgPlayer){
            if(dropLootTable != null){
                dropLootTable.run(instance, position, rpgPlayer, true);
            }
            if(rewardLootTable != null){
                rewardLootTable.run(instance, position, rpgPlayer, false);
            }
        }
        super.kill();
    }




    /**
     * 添加一个属性源
     * @param attributeCompileGroup
     * @param source
     */
    public void addAttribute(AttributeCompileGroup attributeCompileGroup, String source){
        EntityAttributeAddEvent entityAttributeAddEvent = new EntityAttributeAddEvent(this, attributeCompileGroup, source);
        MinecraftServer.getGlobalEventHandler().call(entityAttributeAddEvent);
        if(entityAttributeAddEvent.isCancelled()) return;
        entityAttributesData.addAttribute(entityAttributeAddEvent.getAttributeCompileGroup(), entityAttributeAddEvent.getSource());
    }

    /**
     * 移除一个属性
     * @param source
     */
    public void removeAttribute(String source){
        EntityAttributeRemoveEvent entityAttributeRemoveEvent = new EntityAttributeRemoveEvent(this, source);
        MinecraftServer.getGlobalEventHandler().call(entityAttributeRemoveEvent);
        if(entityAttributeRemoveEvent.isCancelled()) return;
        entityAttributesData.removeAttribute(entityAttributeRemoveEvent.getSource());
    }

    /**
     * 获取指定属性的实际结果
     * @param attributeKey
     * @return
     */
    public Double getAttributeTotal(String attributeKey){
        return entityAttributesData.getAttributeTotal(attributeKey);
    }

    /**
     * 获取指定属性的实际加算合计
     * @param attributeKey
     * @return
     */
    public Double getAttributeAmount(String attributeKey){
        return entityAttributesData.getAttributeAmount(attributeKey);
    }

    /**
     * 获取指定属性的实际乘算合计
     * @param attributeKey
     * @return
     */
    public Double getAttributePercent(String attributeKey){
        return entityAttributesData.getAttributePercent(attributeKey);
    }

    /**
     * 获取这个属性的最大值 如果是null则代表没有
     * @param attributeKey
     * @return
     */
    @Nullable
    public Double getAttributeMax(String attributeKey){
        return entityAttributesData.getAttributeMax(attributeKey);
    }

    /**
     * 获取这个属性的最小值 如果是null则代表没有
     * @param attributeKey
     * @return
     */
    @Nullable
    public Double getAttributeMin(String attributeKey){
        return entityAttributesData.getAttributeMin(attributeKey);
    }

    /**
     * 更新实体身上的属性
     */
    public void updateAttribute(){
        EntityAttributeUpdateEvent entityAttributeUpdateEvent = new EntityAttributeUpdateEvent(this, entityAttributesData);
        MinecraftServer.getGlobalEventHandler().call(entityAttributeUpdateEvent);
        if(entityAttributeUpdateEvent.isCancelled()) return;
        entityAttributeUpdateEvent.getEntityAttributesData().update();
        updateVanilla();
    }

    /**
     * 处理原版属性更新
     */
    public void updateVanilla(){
        Map<String, Double> map = new HashMap<>(entityAttributesData.getAttributeMap());
        if(map.isEmpty()) return;
        AttributeConfig.vanillaAttributeConfigMap.forEach((key, value) -> {
            Attribute attribute = Attribute.fromKey(key);
            if(attribute != null){
                AttributeInstance attributeInstance = getAttribute(attribute);
                attributeInstance.clearModifiers();
                Double result = value.total(map);
                AttributeModifier attributeModifier = new AttributeModifier("custom:" + key, result, AttributeOperation.ADD_VALUE);
                attributeInstance.addModifier(attributeModifier);
                if(!value.getVanilla()){
                    attributeInstance.setBaseValue(0);
                }
                Main.getLogger().debug("实体 {} 的原版属性 {} 修改为 {}", getUuid(), key, attributeModifier);
            }
        });
    }




}
