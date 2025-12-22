package org.qiuhua.troveserver.skill.targeter;

import net.minestom.server.coordinate.Pos;
import net.minestom.server.coordinate.Vec;
import net.minestom.server.entity.Entity;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class PosTargetSelector {


    /**
     * 选取视角前方第X格方块的位置 （基准: 施法者朝向）
     * @param entity 实体
     * @param forward 前方第几格位置的方块
     * @param rotate 视角准线的旋转角度
     * @param useEyeLocation 是否令基点位于实体眼部
     * @param lockPitch 是否忽视视角俯仰视角度
     */
    public static Pos forwardTarget(@NotNull Entity entity, @NotNull Double forward, @Nullable Float rotate, @Nullable Boolean useEyeLocation, @Nullable Boolean lockPitch){
        if (rotate == null) rotate = 0.0f;
        if (useEyeLocation == null) useEyeLocation = false;
        if (lockPitch == null) lockPitch = false;

        Pos pos = entity.getPosition();

        // 设置到眼睛高度
        if(useEyeLocation){
            pos = pos.add(0, entity.getEyeHeight(), 0);
        }

        // 锁定俯仰角：创建用于计算的方向位置
        float finalPitch = lockPitch ? 0.0f : pos.pitch();
        Pos directionPos = lockPitch ? pos.withPitch(0.0f) : pos;

        // 使用锁定后的俯仰角计算方向向量
        Vec directionVec = getDirectionVector(directionPos.yaw(), directionPos.pitch());

        // 应用旋转
        if (rotate != 0.0f) {
            directionVec = rotateDirectionVector(directionVec, rotate);
        }

        // 应用距离
        directionVec = directionVec.normalize().mul(forward);

        // 计算最终位置
        Pos finalPos = new Pos(
                pos.x() + directionVec.x(),
                pos.y() + directionVec.y(),
                pos.z() + directionVec.z(),
                pos.yaw(),
                finalPitch  // 使用调整后的pitch
        );

        return finalPos;
    }
























    /**
     * 获取方向向量（如果使用Minestom的Vec类）
     */
    private static Vec getDirectionVector(float yaw, float pitch) {
        double yawRad = Math.toRadians(yaw);
        double pitchRad = Math.toRadians(pitch);

        double x = -Math.sin(yawRad) * Math.cos(pitchRad);
        double y = -Math.sin(pitchRad);
        double z = Math.cos(yawRad) * Math.cos(pitchRad);

        return new Vec(x, y, z);
    }

    /**
     * 旋转方向向量
     */
    private static Vec rotateDirectionVector(Vec direction, float rotate) {
        double rotateRad = Math.toRadians(rotate);
        double cosRot = Math.cos(rotateRad);
        double sinRot = Math.sin(rotateRad);

        double newX = direction.x() * cosRot - direction.z() * sinRot;
        double newZ = direction.x() * sinRot + direction.z() * cosRot;

        return new Vec(newX, direction.y(), newZ);
    }
}
