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
import org.qiuhua.troveserver.player.RPGPlayer;
import org.qiuhua.troveserver.skill.SkillMetadata;
import org.qiuhua.troveserver.skill.event.CastSkillEvent;
import org.qiuhua.troveserver.skill.mechanics.ProjectileMechanic;
import org.qiuhua.troveserver.skill.mechanics.pos.LungeMechanic;
import org.qiuhua.troveserver.skill.targeter.PosTargetSelector;
import org.qiuhua.troveserver.utils.ItemAnimationController;

/**
 * 骑士职业的1技能
 * 朝前方砸一下带击退
 *
 */
public class Warrior_Skill_1 extends AbstractSkillMechanic {


    /**
     * 地面的特效
     */
    private static final ItemAnimationController itemAnimation = new ItemAnimationController(
            10,
            "warrior:skill1_1", "warrior:skill1_2", "warrior:skill1_3", "warrior:skill1_4", "warrior:skill1_5");


    public Warrior_Skill_1() {
        super("warrior_skill_1");
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
        SkillMetadata finalSkillMetadata = castSkillEvent.skillMetadata;


        double skillCooldown = finalSkillMetadata.getMetaDouble("cooldown", 0.6);
        double cooldown = skillCooldown - (skillCooldown * AttributeManager.getAttributeTotal(casterEntity, "冷却缩减"));
        setCooldown(casterEntity, cooldown);
        setGlobalCooldown(casterEntity, 0.6);
        double skillDamage = skillMetadata.getMetaDouble("damage", 100)/100;
        double knockBackSpeed = skillMetadata.getMetaDouble("knockBackSpeed", 1);
        double knockBackSpeedY = skillMetadata.getMetaDouble("knockBackSpeedY", 1);

        if(casterEntity instanceof RPGPlayer rpgPlayer){
            //设置冷却
            rpgPlayer.setItemCooldown("warrior:skill1", (int) (cooldown * 20));

        }


        //创建一个原地的抛射物
        ProjectileMechanic projectileMechanic = new ProjectileMechanic();
        projectileMechanic.setSpeed(0); //速度为0则是原地
        projectileMechanic.setRemoveDuration(9);
        projectileMechanic.setHitBox(new BoundingBox(4,4,4));
        double forward = 2.0;
        Pos pos = PosTargetSelector.forwardTarget(casterEntity, forward, null, false, true);
        projectileMechanic.getItemDisplayMeta().setScale(new Vec(3));
        projectileMechanic.getItemDisplayMeta().setTranslation(new Pos(0, 1.6, 0));

        projectileMechanic.setOnStart(projectile -> {
            projectile.getViewersAsAudience().playSound(Sound.sound(Key.key("warrior:warrior_slash"), Sound.Source.PLAYER, 1f, 1f));
            projectile.getViewersAsAudience().playSound(Sound.sound(Key.key("entity.generic.explode"), Sound.Source.PLAYER, 1f, 1f));
        });

        projectileMechanic.setOnTick(((time, projectile) -> {
            projectileMechanic.getItemDisplayMeta().setItemStack(itemAnimation.getFrame(projectile.getAliveTicks()));
        }));

        //命中实体
        projectileMechanic.setOnHitEntity((entity, projectile) -> {
            PhysicalDamage physicalDamage = new PhysicalDamage(casterEntity, entity, projectile.getPosition(), skillDamage);
            entity.damage(physicalDamage);
            LungeMechanic.knockBack(entity, casterEntity.getPosition(), knockBackSpeed, knockBackSpeedY);
        });
        projectileMechanic.shoot(casterEntity, pos, PosTargetSelector.forwardTarget(casterEntity, forward + 1, null, false, true));



        Main.getLogger().debug("执行成功");

        return true;
    }











}
