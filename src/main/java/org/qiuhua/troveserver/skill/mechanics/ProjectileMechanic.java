package org.qiuhua.troveserver.skill.mechanics;

import net.minestom.server.entity.LivingEntity;
import net.minestom.server.instance.block.Block;
import org.qiuhua.troveserver.api.skill.projectile.AbstractProjectile;

public class ProjectileMechanic extends AbstractProjectile {


    public interface HitEntityCallback {
        void onHit(LivingEntity livingEntity, ProjectileMechanic projectile);
    }

    public interface HitBlockCallback {
        void onHit(Block block, ProjectileMechanic projectile);
    }

    public interface StartCallback {
        void onStart(ProjectileMechanic projectile);
    }

    public interface EndCallback {
        void onEnd(ProjectileMechanic projectile);
    }

    public interface TickCallback {
        void onTick(long tick, ProjectileMechanic projectile);
    }

    private HitEntityCallback hitEntityCallback;
    private HitBlockCallback hitBlockCallback;
    private StartCallback startCallback;
    private EndCallback endCallback;
    private TickCallback tickCallback;

    public ProjectileMechanic() {
        super();
    }

    public ProjectileMechanic setOnHitEntity(HitEntityCallback callback) {
        this.hitEntityCallback = callback;
        return this;
    }

    public ProjectileMechanic setOnHitBlock(HitBlockCallback callback) {
        this.hitBlockCallback = callback;
        return this;
    }

    public ProjectileMechanic setOnStart(StartCallback callback) {
        this.startCallback = callback;
        return this;
    }

    public ProjectileMechanic setOnEnd(EndCallback callback) {
        this.endCallback = callback;
        return this;
    }

    public ProjectileMechanic setOnTick(TickCallback callback) {
        this.tickCallback = callback;
        return this;
    }

    @Override
    protected void onTick(long tick) {
        if (tickCallback != null) {
            tickCallback.onTick(tick, this);
        }
    }

    @Override
    protected void onEnd() {
        if (endCallback != null) {
            endCallback.onEnd(this);
        }
    }

    @Override
    protected void onStart() {
        if (startCallback != null) {
            startCallback.onStart(this);
        }
    }

    @Override
    protected void onHitBlock(Block hitBlock) {
        if (hitBlockCallback != null) {
            hitBlockCallback.onHit(hitBlock, this);
        }
    }

    @Override
    protected void onHitEntity(LivingEntity hitEntity) {
        if (hitEntityCallback != null) {
            hitEntityCallback.onHit(hitEntity, this);
        }
    }

    // 获取当前实例的方法，方便在回调中操作
    public ProjectileMechanic getProjectile() {
        return this;
    }
}