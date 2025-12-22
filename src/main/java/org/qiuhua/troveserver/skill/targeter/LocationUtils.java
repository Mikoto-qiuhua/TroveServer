package org.qiuhua.troveserver.skill.targeter;

import net.minestom.server.coordinate.Pos;
import net.minestom.server.entity.Entity;

public class LocationUtils {


    /**
     * 相对偏移
     * 按照实体朝向进行位置偏移
     * @param entity 实体对象
     * @param forward 前后
     * @param right 左右
     * @param up 上下
     * @return
     */
    public static Pos relativeLocationOffset(Entity entity, double forward, double right, double up) {
        // 获取实体当前的位置和朝向
        Pos entityPos = entity.getPosition();
       return relativeLocationOffset(entityPos, forward, right, up);
    }

    /**
     * 按照实体朝向进行位置偏移
     * @param pos 位置信息
     * @param forward 前后
     * @param right 左右
     * @param up 上下
     * @return
     */
    public static Pos relativeLocationOffset(Pos pos, double forward, double right, double up) {
        float yaw = pos.yaw();
        float pitch = pos.pitch();

        // 将角度转换为弧度
        double yawRad = Math.toRadians(yaw);
        double pitchRad = Math.toRadians(pitch);

        // 计算前后左右方向的偏移量
        // 前后方向：基于 yaw 角度的 sin 和 cos
        double forwardX = -Math.sin(yawRad) * forward;
        double forwardZ = Math.cos(yawRad) * forward;

        // 左右方向：基于 yaw + 90度 的 sin 和 cos
        double rightX = -Math.sin(yawRad + Math.PI / 2) * right;
        double rightZ = Math.cos(yawRad + Math.PI / 2) * right;

        // 上下方向：基于 pitch 角度
        double upY;
        if(pitchRad == 0){
            upY = up;
        }else {
            upY = -Math.sin(pitchRad) * up;
        }
        // 合并所有偏移量
        double offsetX = forwardX + rightX;
        double offsetY = upY;
        double offsetZ = forwardZ + rightZ;
        // 返回新的位置
        return new Pos(
                pos.x() + offsetX,
                pos.y() + offsetY,
                pos.z() + offsetZ,
                yaw,
                pitch
        );
    }


    /**
     * 绝对位置偏移
     * @param entity
     * @param offsetX
     * @param offsetY
     * @param offsetZ
     * @return
     */
    public static Pos absoluteLocationOffset(Entity entity, double offsetX, double offsetY, double offsetZ) {
        Pos entityPos = entity.getPosition();
        return absoluteLocationOffset(entityPos, offsetX, offsetY, offsetZ);

    }

    /**
     * 绝对位置偏移
     * @param pos
     * @param offsetX
     * @param offsetY
     * @param offsetZ
     * @return
     */
    public static Pos absoluteLocationOffset(Pos pos, double offsetX, double offsetY, double offsetZ) {

        return new Pos(
                pos.x() + offsetX,
                pos.y() + offsetY,
                pos.z() + offsetZ,
                pos.yaw(),
                pos.pitch()
        );

    }









}
