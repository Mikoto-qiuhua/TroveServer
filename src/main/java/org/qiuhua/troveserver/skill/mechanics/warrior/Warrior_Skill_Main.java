package org.qiuhua.troveserver.skill.mechanics.warrior;

import net.kyori.adventure.key.Key;
import net.kyori.adventure.sound.Sound;
import net.minestom.server.MinecraftServer;
import net.minestom.server.collision.BoundingBox;
import net.minestom.server.coordinate.Pos;
import net.minestom.server.coordinate.Vec;
import net.minestom.server.entity.Entity;
import org.qiuhua.troveserver.Main;
import org.qiuhua.troveserver.api.skill.AbstractSkillMechanic;
import org.qiuhua.troveserver.fight.PhysicalDamage;
import org.qiuhua.troveserver.module.attribute.AttributeManager;
import org.qiuhua.troveserver.skill.SkillMetadata;
import org.qiuhua.troveserver.skill.event.CastSkillEvent;
import org.qiuhua.troveserver.skill.mechanics.ProjectileMechanic;
import org.qiuhua.troveserver.player.RPGPlayer;
import org.qiuhua.troveserver.utils.ItemAnimationController;
import org.qiuhua.troveserver.skill.targeter.PosTargetSelector;

import java.util.concurrent.ConcurrentHashMap;

/**
 * 战士职业的左键攻击技能
 * 触发挥刀
 * 第一次挥刀为右手
 * 两次触发的间隔小于3s 则左右互换挥刀
 */
public class Warrior_Skill_Main extends AbstractSkillMechanic {


    /**
     * 第一次攻击的动画
     */
    private static final ItemAnimationController itemAnimationAttack1 = new ItemAnimationController(10, "warrior:leftclick_attack_1", "warrior:leftclick_attack_2", "warrior:leftclick_attack_3", "warrior:leftclick_attack_4");

    /**
     * 第二次的攻击动画
     */
    private static final ItemAnimationController itemAnimationAttack2 = new ItemAnimationController(10, "warrior:leftclick_attack_1_1", "warrior:leftclick_attack_2_1", "warrior:leftclick_attack_3_1", "warrior:leftclick_attack_4_1");

    /**
     * 连续攻击需要的间隔
     */
    private static final Long interval = 1000L;

    /**
     * 全部实体上一次攻击的时间
     */
    private static final ConcurrentHashMap<Entity, Long> attackTime = new ConcurrentHashMap<>();



    public Warrior_Skill_Main() {
        super("warrior_skill_main");
    }




    @Override
    public boolean execute(SkillMetadata skillMetadata, Entity casterEntity) {
        if(isCooldown(casterEntity) || isGlobalCooldown(casterEntity)){
            if(casterEntity instanceof RPGPlayer rpgPlayer){
                Main.getLogger().debug("{} 技能 {} 冷却中", rpgPlayer.getUsername(), getSkillId());
            }else {
                Main.getLogger().debug("{} 技能 {} 冷却中", casterEntity.getUuid(), getSkillId());
            }
            return false;
        };

        CastSkillEvent castSkillEvent = new CastSkillEvent(this, casterEntity, skillMetadata);
        MinecraftServer.getGlobalEventHandler().call(castSkillEvent);
        if(castSkillEvent.isCancelled()) return false;


        double skillCooldown = skillMetadata.getMetaDouble("cooldown", 1);
        double attackSpeed = AttributeManager.getAttributeTotal(casterEntity, "攻击速度");
        if (attackSpeed <= 0) {
            attackSpeed = 0; // 默认值
        }
        //计算攻击速度 和冷却缩减算法不同
        double cooldown = skillCooldown / attackSpeed;
        setCooldown(casterEntity, cooldown);

        //伤害倍率
        double skillDamage = skillMetadata.getMetaDouble("damage", 100)/100;


        //当前时间
        Long nowTime = System.currentTimeMillis();
        //上次时间
        Long lastTime = attackTime.get(casterEntity);
        //获取播放的物品动画
        ItemAnimationController itemAnimationController;
        String animationId;
        if(lastTime != null && nowTime-lastTime <= interval){
            itemAnimationController = itemAnimationAttack2;
            attackTime.remove(casterEntity);
            animationId = "attack2";
        }else {
            itemAnimationController = itemAnimationAttack1;
            attackTime.put(casterEntity, nowTime);
            animationId = "attack1";
        }

        //武器模型动画
        if(casterEntity instanceof RPGPlayer rpgPlayer){

        }

        //创建一个原地的抛射物
        ProjectileMechanic projectileMechanic = new ProjectileMechanic();
        projectileMechanic.setSpeed(0); //速度为0则是原地
        projectileMechanic.setRemoveDuration(9);
        projectileMechanic.setHitBox(new BoundingBox(3.5,2,3.5));
        double forward = 2.0;
        Pos pos = PosTargetSelector.forwardTarget(casterEntity, forward, null, false, false);
        projectileMechanic.getItemDisplayMeta().setScale(new Vec(3));

        projectileMechanic.setOnStart(projectile -> {
            projectile.getViewersAsAudience().playSound(Sound.sound(Key.key("warrior:warrior_slash"), Sound.Source.PLAYER, 1f, 1f));
        });

        //更新物品
        ItemAnimationController finalItemAnimationController = itemAnimationController;
        projectileMechanic.setOnTick(((time, projectile) -> {
            projectileMechanic.getItemDisplayMeta().setItemStack(finalItemAnimationController.getFrame(projectile.getAliveTicks()));
        }));

        //命中实体
        projectileMechanic.setOnHitEntity((entity, projectile) -> {
            PhysicalDamage physicalDamage = new PhysicalDamage(casterEntity, entity, projectile.getPosition(), skillDamage);
            entity.damage(physicalDamage);
        });
        projectileMechanic.shoot(casterEntity, pos, PosTargetSelector.forwardTarget(casterEntity, forward + 1, null, false, false));
        return true;
    }











}
