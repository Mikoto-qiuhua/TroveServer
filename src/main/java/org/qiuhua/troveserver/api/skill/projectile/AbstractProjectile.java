package org.qiuhua.troveserver.api.skill.projectile;


import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import net.minestom.server.collision.BoundingBox;
import net.minestom.server.coordinate.Point;
import net.minestom.server.coordinate.Pos;
import net.minestom.server.coordinate.Vec;
import net.minestom.server.entity.Entity;
import net.minestom.server.entity.EntityType;
import net.minestom.server.entity.LivingEntity;
import net.minestom.server.entity.Player;
import net.minestom.server.entity.metadata.display.AbstractDisplayMeta;
import net.minestom.server.entity.metadata.display.ItemDisplayMeta;
import net.minestom.server.entity.metadata.other.ArmorStandMeta;
import net.minestom.server.instance.Chunk;
import net.minestom.server.instance.block.Block;
import net.minestom.server.network.packet.server.play.ParticlePacket;
import net.minestom.server.particle.Particle;
import net.minestom.server.tag.Tag;
import net.minestom.server.utils.chunk.ChunkUtils;

import org.qiuhua.troveserver.api.entity.AbstractDisplayEntity;
import org.qiuhua.troveserver.api.entity.AbstractEntity;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;


public abstract class AbstractProjectile extends AbstractDisplayEntity {
    /**
     * 发射抛射物的实体
     */
    @Getter
    @Setter
    @Accessors(chain=true)
    protected Entity shooter;

    /**
     * 是否命中方块
     */
    @Getter
    @Setter
    @Accessors(chain=true)
    protected boolean inBlock = false;

    /**
     * 是否命中玩家
     */
    @Getter
    @Setter
    @Accessors(chain=true)
    protected boolean inPlayer = false;

    /**
     * 是否命中施法者
     */
    @Getter
    @Setter
    @Accessors(chain=true)
    protected boolean inShooter = false;

    /**
     * 是否命中非玩家实体
     */
    @Getter
    @Setter
    @Accessors(chain=true)
    protected boolean inEntity = true;

    /**
     * 命中实体是否停止
     */
    @Getter
    @Setter
    @Accessors(chain=true)
    protected boolean stopAtEntity = false;


    /**
     * 命中方块是否停止
     */
    @Getter
    @Setter
    @Accessors(chain=true)
    protected boolean stopAtBlock = false;

    /**
     * 飞行速度
     */
    @Getter
    @Setter
    @Accessors(chain=true)
    protected float speed = 1;

    /**
     * 重力
     */
    @Getter
    @Setter
    @Accessors(chain=true)
    protected float gravity = 0f;


    /**
     * 开始发射位置
     */
    @Getter
    @Setter
    @Accessors(chain=true)
    protected Point startPoint;

    /**
     * 发射位置是否从眼睛高度
     */
    @Getter
    @Setter
    @Accessors(chain=true)
    protected boolean isEyeHeight = false;

    /**
     * 目标位置
     */

    @Getter
    @Setter
    @Accessors(chain=true)
    protected Point targetPoint;

    /**
     * 飞行的距离
     */
    @Getter
    @Setter
    @Accessors(chain=true)
    protected int distance = 100;

    /**
     * 同一个实体或方块被命中的延迟 毫秒
     */
    @Getter
    @Setter
    @Accessors(chain=true)
    protected int hitDelay = 10000;

    /**
     * 命中的实体缓存
     */
    protected final ConcurrentHashMap<LivingEntity, Long> hitEntityCache = new ConcurrentHashMap<>();

    /**
     * 命中的实体缓存
     */
    protected final ConcurrentHashMap<Block, Long> hitBlockCache = new ConcurrentHashMap<>();

    /**
     * 当前速度
     */
    protected Vec velocity;

    /**
     * 是否启用追踪
     */
    protected boolean isHoming = false;

    /**
     * 追踪强度 (0.0 - 1.0)
     */
    @Getter
    @Setter
    @Accessors(chain=true)
    protected float homingStrength = 0.1f;

    /**
     * 追踪范围
     */
    @Getter
    @Setter
    @Accessors(chain=true)
    protected float homingRange = 99f;

    /**
     * 当前追踪的目标（可以是动态的）
     */
    protected Entity homingTarget = null;
    protected Point homingTargetPoint = null;

    /**
     * 取消追踪距离 - 到达此距离后停止追踪
     */
    @Getter
    @Setter
    @Accessors(chain=true)
    protected float stopHomingDistance = 2f;

    /**
     * 碰撞箱大小
     */
    @Getter
    @Setter
    @Accessors(chain=true)
    protected BoundingBox hitBox = new BoundingBox(0.5, 0.5, 0.5);

    /**
     * 是否渲染碰撞箱范围
     */
    @Getter
    @Setter
    @Accessors(chain=true)
    protected boolean isHitBoxDisplay = true;


    /**
     * 上一帧位置（用于碰撞检测）
     */
    @Getter
    protected Pos previousPosition;


    @Getter
    protected final ItemDisplayMeta itemDisplayMeta;


    public AbstractProjectile() {
        super(EntityType.ITEM_DISPLAY);
        itemDisplayMeta = (ItemDisplayMeta) this.getEntityMeta();
        setBillboardConstraints(AbstractDisplayMeta.BillboardConstraints.FIXED);

    }





    public void shoot(Entity shooter, Point startPoint, Point targetPoint) {
        setNoGravity(true);
        setBoundingBox(hitBox);
        this.shooter = shooter;
        this.startPoint = startPoint;
        this.targetPoint = targetPoint;
        // 设置初始位置
        Pos spawnPos;
        if (isEyeHeight && shooter != null) {
            // 从眼睛高度发射
            spawnPos = shooter.getPosition().add(0, shooter.getEyeHeight(), 0);
        } else {
            spawnPos = new Pos(startPoint);
        }

        // 计算初始朝向
        Vec direction = targetPoint.sub(spawnPos).asVec().normalize();
        float yaw = calculateYaw(direction);
        float pitch = calculatePitch(direction);

        // 设置带朝向的位置
        spawnPos = spawnPos.withView(yaw, pitch);
        super.spawnEntity(shooter.getInstance(), spawnPos);

        // 计算速度方向
        this.velocity = direction.mul(speed/10);
        this.previousPosition = spawnPos; // 初始化上一帧位置
        onStart();
    }

    /**
     * 取消追踪，按当前方向继续飞行
     */
    public void cancelHoming() {
        this.isHoming = false;
        onHomingCanceled();
    }

    /**
     * 设置追踪实体
     * @param entity
     */
    public void setHoming(Entity entity){
        this.isHoming = true;
        this.homingTarget = entity;
    }

    /**
     * 设置追踪坐标
     * @param point
     */
    public void setHoming(Point point){
        this.isHoming = true;
        this.homingTargetPoint = point;
    }


    @Override
    public void update(long time){
        // 保存上一帧位置（必须在任何移动之前）
        this.previousPosition = getPosition();
        // 渲染碰撞箱大小
        if(isHitBoxDisplay){
            renderBoundingBoxOutline();
        }

        // 持续应用追踪逻辑
        if (isHoming) {
            applyContinuousHoming();
        }

        // 应用重力
        velocity = velocity.add(0, -(gravity * 0.001), 0);

        // 更新位置（手动控制）
        Pos currentPos = getPosition();
        Pos newPos = currentPos.add(velocity);

        // 更新朝向
        float yaw = calculateYaw(velocity.normalize());
        float pitch = calculatePitch(velocity.normalize());
        newPos = newPos.withView(yaw, pitch);

        teleport(newPos);

        // 移动后立即进行碰撞检测
        //是否检查方块
        if(inBlock){
            callBlockCollision();
        }
        //对于实体的命中需要开启其中一个才会进行计算碰撞
        if(inPlayer || inEntity){
            callEntityCollision();
        }

        // 每tick执行
        onTick(getAliveTicks());

        // 检查是否超出范围
        if (currentPos.distance(startPoint) > distance) {
            remove();
        }
        super.update(time);
    }



    /**
     * 持续追踪逻辑 - 超平滑版本
     */
    private void applyContinuousHoming() {
        if (!isHoming) return; // 如果已经取消追踪，直接返回

        Pos currentPos = getPosition();

        Point currentTarget = getCurrentTargetPosition();
        if (currentTarget == null) return;

        double distanceToTarget = currentPos.distance(currentTarget);

        // 检查是否到达取消追踪距离
        if (distanceToTarget <= stopHomingDistance) {
            cancelHoming(); // 取消追踪
            return;
        }

        // 检查目标是否在追踪范围内
        if (distanceToTarget > homingRange) {
            return;
        }

        // 计算指向目标的方向
        Vec toTarget = currentTarget.sub(currentPos).asVec().normalize();

        // 当前飞行方向
        Vec currentDirection = velocity.normalize();

        // 使用追踪强度进行平滑转向
        Vec newDirection = currentDirection.mul(1.0f - homingStrength)  // 保持大部分当前方向
                .add(toTarget.mul(homingStrength))  // 添加目标方向
                .normalize();

        // 更新速度方向
        double currentSpeed = velocity.length();
        this.velocity = newDirection.mul(currentSpeed);

        onHomingUpdate();
    }

    /**
     * 追踪取消时执行
     */
    protected void onHomingCanceled() {
        // 可以在这里添加特效，比如改变粒子颜色表示锁定完成
        var particlePacket = new ParticlePacket(
                Particle.HAPPY_VILLAGER, // 绿色星星表示锁定完成
                (float) position.x(), (float) position.y(), (float) position.z(),
                0.3f, 0.3f, 0.3f,
                0f, 5
        );
        instance.sendGroupedPacket(particlePacket);
    }

    /**
     * 追踪更新时执行
     */
    protected void onHomingUpdate() {
        // 可以在这里添加追踪时的特效，比如不同的粒子颜色
        var particlePacket = new ParticlePacket(
                Particle.HEART,
                (float) position.x(), (float) position.y(), (float) position.z(),
                0.2f, 0.2f, 0.2f,
                0f, 1
        );
        instance.sendGroupedPacket(particlePacket);
    }

    /**
     * 每tick执行
     */
    protected abstract void onTick(long tick);

    /**
     * 结束时执行
     */
    protected abstract void onEnd();

    /**
     * 开始时执行
     */
    protected abstract void onStart();

    /**
     * 命中方块
     * @param hitBlock
     */
    protected abstract void onHitBlock(Block hitBlock);

    /**
     * 命中实体
     * @param hitEntity
     */
    protected abstract void onHitEntity(LivingEntity hitEntity);


    /**
     * 删除抛射物
     */
    @Override
    public void remove(){
        onEnd();
        super.remove();
    }





    /**
     * 实体碰撞检测
     */
    private void callEntityCollision() {
        Collection<LivingEntity> entities = getNearbyEntities();
        Long time = System.currentTimeMillis();
        for (LivingEntity entity : entities) {
            if (boundingBox.intersectEntity(position, entity)) {
                //跳过自己
                if(entity == this) continue;

                if(entity instanceof AbstractEntity || entity instanceof Player){

                    //跳过施法者
                    if(!inShooter && entity == shooter) continue;

                    //如果没开命中玩家 并且实体是玩家类型 那就跳过
                    if(!inPlayer && entity instanceof Player) continue;

                    //如果没开命中实体 并且是非玩家类型 那就跳过
                    if(!inEntity && !(entity instanceof Player)) continue;

                    //如果上一次命中小于间隔那就跳过
                    if(hitEntityCache.containsKey(entity) && (time - hitEntityCache.get(entity)) < hitDelay) continue;
                    hitEntityCache.put(entity, time);
                    onHitEntity(entity);
                    //启用实体碰撞停止的话则碰到第一个实体就结束
                    if(stopAtEntity){
                        remove();
                    }
                }
            }
        }
    }


    /**
     * 检查的区块范围
     */
    private final int chunkRadius = 2;
    /**
     * 获取附近指定区块范围内的所有实体
     */
    private Collection<LivingEntity> getNearbyEntities() {
        Chunk centerChunk = currentChunk;
        List<LivingEntity> nearbyEntities = new ArrayList<>();
        // 获取中心区块坐标
        int centerX = centerChunk.getChunkX();
        int centerZ = centerChunk.getChunkZ();
        // 遍历附近区块
        for (int dx = -chunkRadius; dx <= chunkRadius; dx++) {
            for (int dz = -chunkRadius; dz <= chunkRadius; dz++) {
                int chunkX = centerX + dx;
                int chunkZ = centerZ + dz;
                Chunk chunk = getInstance().getChunk(chunkX, chunkZ);
                if (ChunkUtils.isLoaded(chunk)) {
                    // 获取该区块的所有实体
                    Collection<Entity> chunkEntities = getInstance().getChunkEntities(chunk);
                    // 过滤生物实体 是活体并且不是盔甲架
                    chunkEntities.stream()
                            .filter(entity -> entity instanceof LivingEntity && entity.getEntityType() != EntityType.ARMOR_STAND)
                            .map(entity -> (LivingEntity) entity)
                            .forEach(nearbyEntities::add);
                }
            }
        }
        return nearbyEntities;
    }




    /**
     * 方块碰撞检测
     */
    private void callBlockCollision() {
        Block block = instance.getBlock(getPosition());
        if(block.isSolid()){
            Long time = System.currentTimeMillis();
            //如果上一次命中小于间隔那就跳过
            if(hitBlockCache.containsKey(block) && (time - hitBlockCache.get(block)) < hitDelay){
                return;
            }
            hitBlockCache.put(block, time);
            onHitBlock(block);
            //启用方块碰撞停止的话 在触碰到第一个方块时就结束
            if(stopAtBlock){
                remove();
            }
        }
    }


    /**
     * 获取当前追踪目标的位置
     */
    private Point getCurrentTargetPosition() {
        // 优先使用实体目标
        if (homingTarget != null && !homingTarget.isRemoved()) {
            return homingTarget.getPosition();
        }
        // 其次使用固定目标点
        if(homingTargetPoint != null) {
            return homingTargetPoint;
        }
        return null;
    }

    /**
     * 根据方向向量计算yaw（水平旋转角度）
     */
    private float calculateYaw(Vec direction) {
        return (float) Math.toDegrees(Math.atan2(-direction.x(), direction.z()));
    }

    /**
     * 根据方向向量计算pitch（垂直旋转角度）
     */
    private float calculatePitch(Vec direction) {
        double horizontalDistance = Math.sqrt(direction.x() * direction.x() + direction.z() * direction.z());
        return (float) Math.toDegrees(Math.atan2(-direction.y(), horizontalDistance));
    }







    /**
     * 渲染碰撞箱轮廓
     */
    private void renderBoundingBoxOutline() {
        Pos entityPos = previousPosition;

        // 计算碰撞箱在世界坐标中的位置
        double centerX = entityPos.x();
        double centerY = entityPos.y() + hitBox.height() / 2; // 因为BoundingBox底部在y=0
        double centerZ = entityPos.z();

        double halfWidth = hitBox.width() / 2;
        double halfHeight = hitBox.height() / 2;
        double halfDepth = hitBox.depth() / 2;

        // 计算最小和最大点
        Point min = new Vec(centerX - halfWidth, centerY - halfHeight, centerZ - halfDepth);
        Point max = new Vec(centerX + halfWidth, centerY + halfHeight, centerZ + halfDepth);

        // 渲染整个碰撞箱轮廓
        renderBoundingBox(min, max);
    }

    /**
     * 渲染边界框
     */
    private void renderBoundingBox(Point min, Point max) {
        // 底部矩形（红色粒子）
        renderRectangle(
                new Vec(min.x(), min.y(), min.z()),
                new Vec(max.x(), min.y(), min.z()),
                new Vec(max.x(), min.y(), max.z()),
                new Vec(min.x(), min.y(), max.z()),
                Particle.ELECTRIC_SPARK  // 红色火焰粒子
        );

        // 顶部矩形（绿色粒子）
        renderRectangle(
                new Vec(min.x(), max.y(), min.z()),
                new Vec(max.x(), max.y(), min.z()),
                new Vec(max.x(), max.y(), max.z()),
                new Vec(min.x(), max.y(), max.z()),
                Particle.ELECTRIC_SPARK  // 绿色星星粒子
        );

        // 4条垂直线（蓝色粒子）
        renderLine(new Vec(min.x(), min.y(), min.z()), new Vec(min.x(), max.y(), min.z()), Particle.ELECTRIC_SPARK);
        renderLine(new Vec(max.x(), min.y(), min.z()), new Vec(max.x(), max.y(), min.z()), Particle.ELECTRIC_SPARK);
        renderLine(new Vec(min.x(), min.y(), max.z()), new Vec(min.x(), max.y(), max.z()), Particle.ELECTRIC_SPARK);
        renderLine(new Vec(max.x(), min.y(), max.z()), new Vec(max.x(), max.y(), max.z()), Particle.ELECTRIC_SPARK);
    }

    /**
     * 渲染矩形边框
     */
    private void renderRectangle(Point p1, Point p2, Point p3, Point p4, Particle particle) {
        renderLine(p1, p2, particle);
        renderLine(p2, p3, particle);
        renderLine(p3, p4, particle);
        renderLine(p4, p1, particle);
    }

    /**
     * 渲染两点之间的线段
     */
    private void renderLine(Point start, Point end, Particle particle) {
        double distance = start.distance(end);
        int particles = Math.max(1, (int) (distance * 4)); // 每格8个粒子，至少1个

        Vec direction = end.sub(start).asVec().normalize();
        double step = distance / (particles - 1);
        for (int i = 0; i < particles; i++) {
            Point particlePos = start.add(direction.mul(step * i));
            spawnBoundingBoxParticle(particlePos, particle);
        }
    }

    /**
     * 在指定位置生成粒子
     */
    private void spawnBoundingBoxParticle(Point position, Particle particle) {
        var particlePacket = new ParticlePacket(
                particle,
                (float) position.x(), (float) position.y(), (float) position.z(),
                0f, 0f, 0f, // 无偏移
                0f,          // particleData
                1            // particleCount
        );
        getViewers().forEach(player -> {
            player.sendPackets(particlePacket);
        });
    }


}
