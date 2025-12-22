package org.qiuhua.troveserver.api.entity;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import net.minestom.server.coordinate.Pos;
import net.minestom.server.coordinate.Vec;
import net.minestom.server.entity.EntityType;
import net.minestom.server.entity.metadata.display.AbstractDisplayMeta;
import net.minestom.server.instance.Instance;
import net.minestom.server.timer.TaskSchedule;


public abstract class AbstractDisplayEntity extends AbstractEntity {



    /**
     * 实体的原始世界
     */
    @Getter
    private Instance originInstance;

    /**
     * 实体的原始位置
     */
    @Getter
    private Pos originPos;


    /**
     * 基础展示实体Meta
     */
    @Getter
    private final AbstractDisplayMeta abstractDisplayMeta;


    /**
     * 实体的朝向 默认是朝向玩家
     */
    @Getter
    @Setter
    @Accessors(chain=true)
    private AbstractDisplayMeta.BillboardConstraints billboardConstraints = AbstractDisplayMeta.BillboardConstraints.CENTER;


    /**
     * 位移
     */
    @Getter
    @Setter
    @Accessors(chain=true)
    private Pos translation = null;

    /**
     * 缩放
     */
    @Getter
    @Setter
    @Accessors(chain=true)
    private Vec scale = null;


    /**
     * 变化持续时间
     */
    @Getter
    @Setter
    @Accessors(chain=true)
    private Integer transformationDuration = 0;

    /**
     * 变化开始的延迟时间
     */
    @Getter
    @Setter
    @Accessors(chain=true)
    private Integer transformationDelay = 0;

    /**
     * 传送或者视角改变的插值
     */
    @Getter
    @Setter
    @Accessors(chain=true)
    private Integer posRotInterpolationDuration = 1;

    public AbstractDisplayEntity(EntityType entityType) {
        super(entityType);
        getEntityMeta().setHasNoGravity(true);
        abstractDisplayMeta = (AbstractDisplayMeta) getEntityMeta();
        abstractDisplayMeta.setBrightness(15, 15);
    }


    /**
     * 生成实体
     */
    @Override
    public void spawnEntity(Instance instance, Pos pos){
        originPos = pos;
        originInstance = instance;
        abstractDisplayMeta.setBillboardRenderConstraints(billboardConstraints);
        abstractDisplayMeta.setPosRotInterpolationDuration(posRotInterpolationDuration);
        setInstance(instance, pos).join();
        //如果两个动画都是null则不执行后续内容
        if(translation == null && scale == null) return;
        //需要在下2个tick去渲染
        //如果仅延迟1tick那会出现生成时直接在最终渲染位置
        scheduler().scheduleTask(()->{
            abstractDisplayMeta.setNotifyAboutChanges(false);
            if(translation != null){
                abstractDisplayMeta.setTranslation(translation);
            }
            if(scale != null){
                abstractDisplayMeta.setScale(scale);
            }
            abstractDisplayMeta.setTransformationInterpolationStartDelta(transformationDelay);
            abstractDisplayMeta.setTransformationInterpolationDuration(transformationDuration);
            abstractDisplayMeta.setNotifyAboutChanges(true);
        }, TaskSchedule.tick(2), TaskSchedule.tick(1));
    }


}
