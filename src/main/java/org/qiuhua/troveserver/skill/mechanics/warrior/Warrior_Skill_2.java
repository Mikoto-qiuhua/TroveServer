package org.qiuhua.troveserver.skill.mechanics.warrior;

import net.kyori.adventure.key.Key;
import net.kyori.adventure.sound.Sound;
import net.minestom.server.MinecraftServer;
import net.minestom.server.collision.BoundingBox;
import net.minestom.server.coordinate.Pos;
import net.minestom.server.coordinate.Vec;
import net.minestom.server.entity.Entity;
import net.minestom.server.entity.metadata.display.AbstractDisplayMeta;
import org.qiuhua.troveserver.Main;
import org.qiuhua.troveserver.api.skill.AbstractSkillMechanic;
import org.qiuhua.troveserver.entity.display.ItemDisplayEntity;
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
 * 骑士职业的2技能
 * 朝前方冲刺
 *
 */
public class Warrior_Skill_2 extends AbstractSkillMechanic {

    /**
     * 突进的动画
     */
    private static final ItemAnimationController itemAnimation2 = new ItemAnimationController(
            10,
            "warrior:skill2_6", "warrior:skill2_7", "warrior:skill2_8", "warrior:skill2_9", "warrior:skill2_10", "warrior:skill2_11");

    /**
     * 开始突进前在脚底生成的动画
     */
    private static final ItemAnimationController itemAnimation1 = new ItemAnimationController(
            10,
            "warrior:skill2_1", "warrior:skill2_2", "warrior:skill2_3", "warrior:skill2_4", "warrior:skill2_5");


    public Warrior_Skill_2() {
        super("warrior_skill_2");
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


        if(casterEntity instanceof RPGPlayer rpgPlayer){
            //设置冷却
            rpgPlayer.setItemCooldown("warrior:skill2", (int) (cooldown * 20));
            //武器模型动画

        }

        //创建一个往前的抛射物
        ProjectileMechanic projectileMechanic = new ProjectileMechanic();
        projectileMechanic.setSpeed(5).setRemoveDuration(9);
        projectileMechanic.setHitBox(new BoundingBox(3.5,3,3.5)).getItemDisplayMeta().setScale(new Vec(2.5));
        projectileMechanic.getItemDisplayMeta().setTranslation(new Pos(0, 1.3, 0));

        ItemDisplayEntity itemDisplayEntity = new ItemDisplayEntity();
        itemDisplayEntity.setBillboardConstraints(AbstractDisplayMeta.BillboardConstraints.FIXED).setScale(new Vec(3)).setRemoveDuration(10);

        projectileMechanic.setOnStart(projectile -> {
            itemDisplayEntity.spawnEntity(projectile.getInstance(), projectile.getOriginPos());
            projectile.getViewersAsAudience().playSound(Sound.sound(Key.key("warrior:warrior_charge"), Sound.Source.PLAYER, 1f, 1f));
        });

        projectileMechanic.setOnTick((tick, projectile) -> {
            projectile.getItemDisplayMeta().setItemStack(itemAnimation2.getFrame(tick));
            itemDisplayEntity.setItem(itemAnimation1.getFrame(tick));
            LungeMechanic.lunge(casterEntity, projectile.getPosition(), 1.0, 0.0);
        });

        projectileMechanic.setOnHitEntity((entity, projectile) -> {
            PhysicalDamage physicalDamage = new PhysicalDamage(casterEntity, entity, projectile.getPosition(), skillDamage);
            entity.damage(physicalDamage);
        });

        projectileMechanic.shoot(casterEntity, PosTargetSelector.forwardTarget(casterEntity, 1.0, null, false, true), PosTargetSelector.forwardTarget(casterEntity, 10.0, null, false, true));


        return true;
    }











}
