package org.qiuhua.troveserver.arcartx.core.entity.data;

import lombok.Getter;
import net.minestom.server.coordinate.Point;
import net.minestom.server.entity.Entity;
import net.minestom.server.entity.Player;
import org.apache.commons.math3.util.Pair;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class ArcartXEntity {

    /**
     * 实体对象
     */
    @Getter
    private final Entity entity;

    /**
     * uuid
     */
    @Getter
    private final UUID uuid;

    /**
     * 套用的模型
     */
    @Getter
    @Nullable
    private String model;


    /**
     * 模型的缩放大小
     */
    @Getter
    private double scale = 1;

    /**
     * 实体尺寸（宽度，高度）
     */
    @Getter
    private Pair<Double, Double> size = new Pair<>(-1.0, -1.0);;

    /**
     * 隐藏的骨骼集合
     */
    @Getter
    private final Set<String> hideBone = new HashSet<>();

    /**
     * 默认状态映射（状态名 -> 状态值）
     */
    @Getter
    private final Map<String, String> defaultState = new HashMap<>();

    /**
     * 是否显示名称
     */
    @Getter
    private boolean displayName = true;

    /**
     * 发光颜色 [R, G, B]
     */
    @Getter
    private int[] glowColor;

    /**
     * 是否启用发光效果
     */
    @Getter
    private boolean glowEnable = false;

    /**
     * 是否隐藏碰撞箱
     */
    @Getter
    private boolean hideHitBox = false;


    /**
     * 构造函数
     * @param entity 原始实体对象
     */
    public ArcartXEntity(Entity entity) {
        this.entity = entity;
        this.uuid = this.entity.getUuid();
    }

    /**
     * 设置实体模型
     * @param modelID 模型ID
     * @param scale 缩放比例
     */
    public void setModel(String modelID, double scale) {
        //参数检查
        this.model = modelID;
        this.scale = scale;
        // TODO: 通知所有观察者模型已更新
    }

    /**
     * 移除实体模型
     */
    public void removeModel() {
        this.model = null;
        this.scale = 1.0;
        // TODO: 通知所有观察者模型已移除
    }

    /**
     * 设置实体尺寸
     * @param width 宽度
     * @param height 高度
     */
    public final void setSize(double width, double height) {
        this.size = new Pair<>(width, height);
        // TODO: 设置实体物理尺寸
        // TODO: 通知所有观察者尺寸已更新
    }

    /**
     * 设置骨骼隐藏状态
     * @param bone 骨骼名称
     * @param hide 是否隐藏
     */
    public final void setHideBone(String bone, boolean hide) {
        if (hide) {
            this.hideBone.add(bone);
        } else {
            this.hideBone.remove(bone);
        }
        // TODO: 通知所有观察者骨骼隐藏状态已更新
    }

    /**
     * 设置默认状态
     * @param state 状态名称
     * @param name 状态值
     */
    public final void setDefaultState(String state, String name) {
        this.defaultState.put(state, name);
        // TODO: 通知所有观察者默认状态已更新
    }

    /**
     * 设置是否显示名称
     * @param displayName 是否显示名称
     */
    public final void setDisplayName(boolean displayName) {
        this.displayName = displayName;
        // TODO: 通知所有观察者名称显示状态已更新
    }

    /**
     * 设置发光颜色
     * @param r 红色分量 (0-255)
     * @param g 绿色分量 (0-255)
     * @param b 蓝色分量 (0-255)
     */
    public final void setGlowColor(int r, int g, int b) {
        this.glowColor = new int[]{r, g, b};
        // TODO: 通知所有观察者发光颜色已更新
    }

    /**
     * 启用发光效果
     */
    public final void enableGlow() {
        this.glowEnable = true;
        // TODO: 通知所有观察者发光效果已启用
    }

    /**
     * 禁用发光效果
     */
    public final void disableGlow() {
        this.glowEnable = false;
        // TODO: 通知所有观察者发光效果已禁用
    }

    /**
     * 播放动画
     * @param animation 动画名称
     * @param speed 播放速度
     * @param transitionTime 过渡时间
     * @param keepTime 保持时间
     */
    public final void playAnimation(String animation, double speed, int transitionTime, long keepTime) {
        // TODO: 通知所有观察者播放动画
    }

    /**
     * 播放音效
     * @param resourcePath 资源路径
     * @param soundCategory 音效类别
     * @param distOrRoll 距离或衰减
     * @param pitch 音调
     * @param keepTime 保持时间
     */
    public final void playSound(String resourcePath, String soundCategory, int distOrRoll, double pitch, int keepTime) {
        // TODO: 通知所有观察者播放音效
    }

    /**
     * 设置是否隐藏碰撞箱
     * @param hide 是否隐藏碰撞箱
     */
    public final void setHideHitBox(boolean hide) {
        this.hideHitBox = hide;
        // TODO: 通知所有观察者碰撞箱隐藏状态已更新
    }

    /**
     * 广播伤害显示给所有观察者
     * @param damageDisplayConfigId 伤害显示配置ID
     * @param damage 伤害值
     */
    public final void broadcastDamageDisplay(String damageDisplayConfigId, double damage, Point startPoint) {
        double x = startPoint.x();
        double y = startPoint.y();
        double z = startPoint.z();
        // TODO: 向所有观察者发送伤害显示
    }

    /**
     * 发送伤害显示给指定玩家列表
     * @param damageDisplayConfigId 伤害显示配置ID
     * @param damage 伤害值
     * @param players 玩家列表
     */
    public final void sendDamageDisplayToPlayers(String damageDisplayConfigId, double damage, Point startPoint, List<? extends Player> players) {
        double x = startPoint.x();
        double y = startPoint.y();
        double z = startPoint.z();
        // TODO: 向指定玩家发送伤害显示
    }

    /**
     * 开始被指定玩家观察
     * 当玩家开始观察此实体时调用，同步所有状态
     * @param target 观察者玩家
     */
    public void startSeenBy(Player target) {
        // TODO: 同步模型、尺寸、骨骼隐藏状态、默认状态、发光效果、名称显示、碰撞箱等所有状态给观察者
    }

    /**
     * 同步实体尺寸到物理引擎
     */
    public final void syncSize() {
        double width = this.size.getFirst();
        double height = this.size.getSecond();
        // TODO: 设置实体物理尺寸

    }




}
