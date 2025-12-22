package org.qiuhua.troveserver.entity;


import lombok.Getter;
import net.minestom.server.coordinate.Pos;
import net.minestom.server.instance.Instance;
import net.minestom.server.item.ItemStack;
import org.qiuhua.troveserver.Main;
import org.qiuhua.troveserver.player.RPGPlayer;
import org.qiuhua.troveserver.utils.task.SchedulerManager;

public class PlayerBindingModelEntity extends ModelEntity {

    /**
     * 这个实体所属的玩家主人
     */
    @Getter
    private final RPGPlayer owner;

    public PlayerBindingModelEntity(String modelId, RPGPlayer rpgPlayer) {
        super(modelId);
        this.owner = rpgPlayer;
    }


    /**
     * 由于是玩家的模型 要将全部实体绑定到玩家身上
     * @param instance
     * @param pos
     */
    @Override
    public void spawnEntity(Instance instance, Pos pos){
        super.spawnEntity(instance, pos);
        owner.addPassenger(this);
        getModelsData().binding(owner);
    }

    /**
     * 这个方法会顺设置武器
     * @param instance
     * @param pos
     * @param itemStack
     */
    public void spawnEntity(Instance instance, Pos pos, ItemStack itemStack){
        spawnEntity(instance, pos);
        getModelsData().setModelItem("left_item", itemStack);
        getModelsData().setModelItem("right_item", itemStack);
    }


    @Override
    public void update(long time){
        if(!position.sameView(owner.getPosition())){
            position = position.withView(owner.getPosition().yaw(), owner.getPosition().pitch());
        }
        super.update(time);
    }


    /**
     * 更新模型的位置和朝向
     */
    @Override
    public void updateModelPos(){
        if(!getBeforePosition().samePoint(position)){
            getModelsData().setPosition(position);
            //Main.getLogger().debug("更新位置");
        }
        if(!getBeforePosition().sameView(position)){
            getModelsData().setGlobalRotation(position.yaw(), -position.pitch());
            //Main.getLogger().debug("更新朝向");
        }
    }
}
