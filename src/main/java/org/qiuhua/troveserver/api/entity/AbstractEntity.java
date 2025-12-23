package org.qiuhua.troveserver.api.entity;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import net.minestom.server.coordinate.Pos;
import net.minestom.server.entity.EntityCreature;
import net.minestom.server.entity.EntityType;

import net.minestom.server.entity.LivingEntity;
import net.minestom.server.entity.damage.Damage;
import net.minestom.server.instance.Instance;
import org.jetbrains.annotations.Nullable;
import org.qiuhua.troveserver.Main;
import org.qiuhua.troveserver.api.attribute.IAttribute;
import org.qiuhua.troveserver.api.fight.AbstractDamage;
import org.qiuhua.troveserver.loot.LootTable;
import org.qiuhua.troveserver.module.attribute.EntityAttributesData;
import org.qiuhua.troveserver.player.RPGPlayer;



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
     * 获取当前实体
     *
     * @return
     */
    @Override
    public LivingEntity getEntity() {
        return this;
    }

}
