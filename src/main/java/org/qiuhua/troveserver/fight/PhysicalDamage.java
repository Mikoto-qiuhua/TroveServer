package org.qiuhua.troveserver.fight;

import net.kyori.adventure.text.Component;
import net.minestom.server.coordinate.Point;
import net.minestom.server.coordinate.Pos;
import net.minestom.server.coordinate.Vec;
import net.minestom.server.entity.Entity;
import net.minestom.server.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.qiuhua.troveserver.Main;
import org.qiuhua.troveserver.api.fight.AbstractDamage;
import org.qiuhua.troveserver.entity.display.TextDisplayEntity;
import org.qiuhua.troveserver.player.RPGPlayer;


public class PhysicalDamage extends AbstractDamage {


    /**
     *
     * @param source
     * @param attacker
     * @param sourcePosition
     * @param multiplier
     */
    public PhysicalDamage(@Nullable Entity source, @Nullable Entity attacker, @Nullable Point sourcePosition, Double multiplier) {
        super(source, attacker, sourcePosition, multiplier);
        setAmount(calculateDamage());
    }


    /**
     * 计算伤害
     */
    @Override
    public float calculateDamage() {
        float value;
        //先算攻击的倍率
        double attack = getSourceAttribute("物理伤害") * getMultiplier();
        //是否暴击
        if(getSourceAttribute("暴击几率") >= Math.random()){
            attack = attack * getSourceAttribute("暴击伤害");
            setCriticalHit(true);
        }else {
            setCriticalHit(false);
        }
        value = (float) attack;
        Main.getLogger().debug("伤害计算结果 {}", value);
        return value;
    }

    /**
     * 生成全息文本的消息提示
     *
     */
    @Override
    public void spawnHDMessage() {
        if(getSource() instanceof RPGPlayer rpgPlayer && getAmount() > 0){
            String damage;
            if(isCriticalHit()){
                damage = "&6";
            }else {
                damage = "&f";
            }
            damage = damage + getAmount();
            TextDisplayEntity textEntity = new TextDisplayEntity(damage);
            textEntity.setSeeThrough(true).setTransformationDuration(20).setTranslation(new Pos(0,2,0)).setScale(new Vec(2)).setRemoveDuration(20);
            textEntity.setAutoViewable(false);
            textEntity.spawnEntity(getAttacker().getInstance(), getAttacker().getPosition().add(0, getAttacker().getEyeHeight(), 0));
            textEntity.addViewer(rpgPlayer);
        }

    }


    /**
     * 构建死亡时的玩家消息
     *
     * @param killed
     * @return
     */
    @Override
    public @Nullable Component buildDeathMessage(@NotNull Player killed) {
        return null;
    }

    /**
     * 构建死亡时屏幕上面的那个文本
     *
     * @param killed
     * @return
     */
    @Override
    public @Nullable Component buildDeathScreenText(@NotNull Player killed) {
        return null;
    }
}
