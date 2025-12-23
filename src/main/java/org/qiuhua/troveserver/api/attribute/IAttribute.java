package org.qiuhua.troveserver.api.attribute;

import net.minestom.server.MinecraftServer;
import net.minestom.server.entity.Entity;
import net.minestom.server.entity.LivingEntity;
import net.minestom.server.entity.attribute.Attribute;
import net.minestom.server.entity.attribute.AttributeInstance;
import net.minestom.server.entity.attribute.AttributeModifier;
import net.minestom.server.entity.attribute.AttributeOperation;
import org.jetbrains.annotations.Nullable;
import org.qiuhua.troveserver.Main;
import org.qiuhua.troveserver.module.attribute.AttributeCompileGroup;
import org.qiuhua.troveserver.module.attribute.EntityAttributesData;
import org.qiuhua.troveserver.module.attribute.config.AttributeConfig;
import org.qiuhua.troveserver.module.attribute.event.EntityAttributeAddEvent;
import org.qiuhua.troveserver.module.attribute.event.EntityAttributeRemoveEvent;
import org.qiuhua.troveserver.module.attribute.event.EntityAttributeUpdateEvent;

import java.util.HashMap;
import java.util.Map;

public interface IAttribute {

    /**
     * 添加一个属性源
     * @param attributeCompileGroup
     * @param source
     */
    default void addAttribute(AttributeCompileGroup attributeCompileGroup, String source){
        if(getEntity() != null) {
            EntityAttributeAddEvent entityAttributeAddEvent = new EntityAttributeAddEvent(getEntity(), attributeCompileGroup, source);
            MinecraftServer.getGlobalEventHandler().call(entityAttributeAddEvent);
            if(entityAttributeAddEvent.isCancelled()) return;
            attributeCompileGroup = entityAttributeAddEvent.getAttributeCompileGroup();
            source = entityAttributeAddEvent.getSource();
        }
        getEntityAttributesData().addAttribute(attributeCompileGroup, source);
    }

    /**
     * 移除一个属性
     * @param source
     */
    default void removeAttribute(String source){
        if(getEntity() != null) {
            EntityAttributeRemoveEvent entityAttributeRemoveEvent = new EntityAttributeRemoveEvent(getEntity(), source);
            MinecraftServer.getGlobalEventHandler().call(entityAttributeRemoveEvent);
            if(entityAttributeRemoveEvent.isCancelled()) return;
            source = entityAttributeRemoveEvent.getSource();
        }
        getEntityAttributesData().removeAttribute(source);
    }

    /**
     * 获取指定属性的实际结果
     * @param attributeKey
     * @return
     */
    default Double getAttributeTotal(String attributeKey){
        return getEntityAttributesData().getAttributeTotal(attributeKey);
    }

    /**
     * 获取指定属性的实际加算合计
     * @param attributeKey
     * @return
     */
    default Double getAttributeAmount(String attributeKey){
        return getEntityAttributesData().getAttributeAmount(attributeKey);
    }

    /**
     * 获取指定属性的实际乘算合计
     * @param attributeKey
     * @return
     */
    default Double getAttributePercent(String attributeKey){
        return getEntityAttributesData().getAttributePercent(attributeKey);
    };

    /**
     * 获取这个属性的最大值 如果是null则代表没有
     * @param attributeKey
     * @return
     */
    @Nullable
    default Double getAttributeMax(String attributeKey){
        return getEntityAttributesData().getAttributeMax(attributeKey);
    };

    /**
     * 获取这个属性的最小值 如果是null则代表没有
     * @param attributeKey
     * @return
     */
    @Nullable
    default Double getAttributeMin(String attributeKey){
        return getEntityAttributesData().getAttributeMin(attributeKey);
    };

    /**
     * 更新实体身上的属性
     */
    default void updateAttribute(){
        if(getEntity() == null) return;
        EntityAttributeUpdateEvent entityAttributeUpdateEvent = new EntityAttributeUpdateEvent(getEntity(), getEntityAttributesData());
        MinecraftServer.getGlobalEventHandler().call(entityAttributeUpdateEvent);
        if(entityAttributeUpdateEvent.isCancelled()) return;
        entityAttributeUpdateEvent.getEntityAttributesData().update();
        updateVanilla();
    }

    /**
     * 处理原版属性更新
     */
    default void updateVanilla(){
        if(getEntity() == null) return;
        Map<String, Double> map = new HashMap<>(getEntityAttributesData().getAttributeMap());
        if(map.isEmpty()) return;
        AttributeConfig.vanillaAttributeConfigMap.forEach((key, value) -> {
            Attribute attribute = Attribute.fromKey(key);
            if(attribute != null){
                AttributeInstance attributeInstance = getEntity().getAttribute(attribute);
                attributeInstance.clearModifiers();
                Double result = value.total(map);
                AttributeModifier attributeModifier = new AttributeModifier("custom:" + key, result, AttributeOperation.ADD_VALUE);
                attributeInstance.addModifier(attributeModifier);
                if(!value.getVanilla()){
                    attributeInstance.setBaseValue(0);
                }
                Main.getLogger().debug("实体 {} 的原版属性 {} 修改为 {}", getEntity().getUuid(), key, attributeModifier);
            }
        });

    };

    /**
     * 属性合集
     * @return
     */
    EntityAttributesData getEntityAttributesData();

    /**
     * 获取当前实体
     * @return
     */
    LivingEntity getEntity();



}
