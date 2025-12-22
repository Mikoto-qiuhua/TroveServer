package org.qiuhua.troveserver.entity;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import net.kyori.adventure.text.Component;
import net.minestom.server.coordinate.Point;
import net.minestom.server.coordinate.Pos;
import net.minestom.server.entity.EntityType;
import net.minestom.server.entity.Player;
import net.minestom.server.entity.ai.goal.RandomStrollGoal;
import net.minestom.server.entity.ai.target.LastEntityDamagerTarget;
import net.minestom.server.event.entity.EntityDamageEvent;
import net.minestom.server.instance.Instance;
import net.minestom.server.instance.block.Block;
import org.jetbrains.annotations.NotNull;
import org.qiuhua.troveserver.Main;
import org.qiuhua.troveserver.api.entity.AbstractEntity;
import org.qiuhua.troveserver.module.attribute.AttributeManager;
import org.qiuhua.troveserver.module.models.AnimationManager;
import org.qiuhua.troveserver.module.models.ModelsData;
import org.qiuhua.troveserver.utils.task.SchedulerManager;

import java.util.List;
import java.util.concurrent.atomic.AtomicReference;


public class ModelEntity extends AbstractEntity {

    /**
     * 实体的模型数据
     */
    @Getter
    @Accessors(chain=true)
    private final ModelsData modelsData;


    /**
     * 当前的状态
     */
    @Getter
    @Setter
    private State state = State.IDLE;

    /**
     * 垂死 已经死亡了 但还在播放死亡动画
     */
    @Getter
    @Setter
    private boolean isDying = false;

    /**
     * 实体的名称
     */
    @Getter
    @Setter
    @Accessors(chain=true)
    private String displayName = null;

    /**
     * 是否显示血条纹理
     */
    @Getter
    @Setter
    @Accessors(chain=true)
    private boolean displayHealth = false;

    /**
     * 上一次的位置
     */
    @Getter
    @Setter
    private Pos beforePosition = Pos.ZERO;


    public ModelEntity(String modelId, EntityType entityType) {
        super(entityType);
        setInvisible(true);
        setBoundingBox(0,0,0);
        modelsData = new ModelsData(modelId, this);
        eventNode().addListener(EntityDamageEvent.class, this::onEntityDamageEvent);
//        addAttribute(AttributeManager.getStringAttributeGroup(List.of("移动速度:1")), "123");
//        addAIGroup(
//                List.of(
//                        new RandomStrollGoal(this, 20) // Walk around
//                ),
//                List.of(
//                        new LastEntityDamagerTarget(this, 32) // First target the last entity which attacked you
//                )
//        );
//        SchedulerManager.scheduleDelayed(40, ()->{
//            addAttribute(AttributeManager.getStringAttributeGroup(List.of("移动速度:0")), "123");
//            updateAttribute();
//        });

    }

    public ModelEntity(String modelId) {
        super(EntityType.ARMOR_STAND);
        setInvisible(true);
        setNoGravity(true);
        setBoundingBox(0,0,0);
        modelsData = new ModelsData(modelId, this);
        eventNode().addListener(EntityDamageEvent.class, this::onEntityDamageEvent);
    }







    public void spawnEntity(Instance instance, Pos pos){
        String name = "";
        if(displayName != null){
            name = displayName;
        }
        if(displayHealth){
            name = name + "血条纹理";
        }
        if(!name.isEmpty()){
            modelsData.addTextTagEntity("nametag", Component.text(name));
        }
        setInstance(instance, pos).join();
        modelsData.init(instance, pos);
        updateAttribute();
        //播放完出生动画就添加ai
        boolean spawnAnimation = modelsData.getAnimationManager().playOnceAnimation("spawn", true, animationManager -> {

        });
        //如果出生动画没播放 那就立即添加ai
        if(!spawnAnimation){

        }
    }


    /**
     * 这里重写了眼睛的高度 因为对于模型来说 高度应当是vfx_eyeheight骨骼的位置
     * 如果没有vfx_eyeheight骨骼 则使用原版的高度
     * @return
     */
    @Override
    public double getEyeHeight(){
        double eyeHeight;
        Pos pos = modelsData.getBonePos("vfx_eyeheight");
        if(pos != null){
            eyeHeight = pos.y() - getPosition().y();
            //Main.getLogger().debug("骨骼计算 {} - {}", pos.y(), getPosition().y());
        }else {
            eyeHeight = super.getEyeHeight();
        }
        //Main.getLogger().debug("模型实体的眼睛高度 {}", eyeHeight);
        return eyeHeight;
    }



    /**
     * 控制实体的模型
     * 这里在同步位置
     * @param time
     */
    @Override
    public void update(long time) {
        updateModelPos();
        checkState();
        modelsData.getAnimationManager().playRepeatAnimation(state.toString());
        super.update(time);
        beforePosition = position;
    }

    /**
     * 更新模型的位置和朝向
     */
    public void updateModelPos(){
        if(!beforePosition.samePoint(position)){
            modelsData.setPosition(position);
            //Main.getLogger().debug("更新位置");
        }
        if(!beforePosition.sameView(position)){
            modelsData.setGlobalRotation(position.yaw());
            //Main.getLogger().debug("更新朝向");
        }
    }


    @Override
    public void updateNewViewer(@NotNull Player player) {
        super.updateNewViewer(player);
        modelsData.addViewer(player);
    }

    @Override
    public void updateOldViewer(@NotNull Player player) {
        super.updateOldViewer(player);
        if(!isDying){
            modelsData.removeViewer(player);
        }

    }

    @Override
    public void kill(){
        //播放完死亡动画就立即移除
        boolean deathAnimation = modelsData.getAnimationManager().playOnceAnimation("death", true, animationManager -> {
            isDying = false;
            modelsData.destroy();
            Main.getLogger().debug("成功播放完死亡动画 卸载模型数据");
        });
        //如果没播放死亡动画 那就立即移除
        if(!deathAnimation){
            modelsData.destroy();
            Main.getLogger().debug("无死亡动画 卸载模型数据");
        }
        super.kill();
    }

    /**
     * 状态检查和更新
     */
    public void checkState() {
        State previousState = state;
        if (beforePosition.samePoint(position)) {
            if (isFly()) {
                state = State.FLYIDLE;
            } else {
                state = State.IDLE;
            }
        } else {
            if (isFly()) {
                state = State.FLYWALK;
            } else {
                state = State.WALK;
            }
        }
        if (previousState != state) {
            Main.getLogger().debug("实体 {} 状态从 {} 切换为 {} tick{}",
                    this.getUuid(), previousState, state, getAliveTicks());
        }

    }




    /**
     * 未完成
     * @return
     */
    public boolean isFly(){
        //地面无飞行
        if(isOnGround()) return false;
        //骑乘无飞行
        if(hasPassenger()) return false;
        //无重力则是飞行
        if(hasNoGravity()) return true;
        // 检查脚部下方的方块
        Point belowPos = position.add(0, -1, 0);
        Block blockBelow = instance.getBlock(belowPos);
        return blockBelow.isAir();
    }


    /**
     * 实体受伤事件
     * 这里要播放受伤动画
     * @param event
     */
    public void onEntityDamageEvent(EntityDamageEvent event){
        if(event.isCancelled()) return;
        modelsData.getAnimationManager().playOnceAnimation("damage", false, animationManager -> {

        });

    }



    /**
     * 实体当前的状态
     */
    public enum State{
        IDLE, WALK, FLYIDLE, FLYWALK;

        @Override
        public String toString() {
            return name().toLowerCase();
        }
    }


}
