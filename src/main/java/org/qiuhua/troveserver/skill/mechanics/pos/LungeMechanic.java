package org.qiuhua.troveserver.skill.mechanics.pos;

import net.minestom.server.coordinate.Point;
import net.minestom.server.coordinate.Vec;
import net.minestom.server.entity.Entity;
import net.minestom.server.entity.LivingEntity;
import net.minestom.server.entity.attribute.Attribute;

public class LungeMechanic{

    /**
     * 朝指定位置冲刺  这个不会考虑实体本身当前的速度 而是以一个固定的速度值覆盖
     * 这个速度值会*10倍进行计算
     * @param entity 实体
     * @param point 位置
     * @param speed 速度
     * @param speedY Y轴速度
     */
    public static void lunge(Entity entity, Point point, double speed, double speedY){
        speed = speed * 10;
        speedY = speedY * 10;
        Point currentPos = entity.getPosition();

        double dx = point.x() - currentPos.x();
        double dy = point.y() - currentPos.y();
        double dz = point.z() - currentPos.z();

        double distance = Math.sqrt(dx * dx + dy * dy + dz * dz);
        if (distance > 0) {
            dx /= distance;
            dz /= distance;
            entity.setVelocity(new Vec(dx * speed, speedY, dz * speed));
        }
    }

    /**
     * 基于坐标位置的击退 会考虑击退抗性
     * @param entity
     * @param point
     * @param speed
     */
    public static void knockBack(Entity entity, Point point, double speed, double speedY){

        double knockback_resistance = 1;
        if(entity instanceof LivingEntity livingEntity){
            knockback_resistance = 1 - livingEntity.getAttributeValue(Attribute.KNOCKBACK_RESISTANCE);
        }
        //实体的击退抗性(0-1之间 1表示完全抵抗)
        knockback_resistance = Math.max(0, knockback_resistance);
        //应用击退抗性
        speed = (speed * 10) * knockback_resistance;
        speedY = (speedY * 10) * knockback_resistance;

        Point currentPos = entity.getPosition();

        //实体位置 - 击退源点 (远离击退源点)
        double dx = currentPos.x() - point.x();    //远离X
        double dy = currentPos.y() - point.y();    //远离Y
        double dz = currentPos.z() - point.z();    //反远离Z

        double distance = Math.sqrt(dx * dx + dy * dy + dz * dz);
        if (distance > 0) {
            dx /= distance;
            dz /= distance;
            entity.setVelocity(new Vec(dx * speed, speedY, dz * speed));
        }
    }

}
