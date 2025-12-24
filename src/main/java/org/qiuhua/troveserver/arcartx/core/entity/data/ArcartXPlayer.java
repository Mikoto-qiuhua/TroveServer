package org.qiuhua.troveserver.arcartx.core.entity.data;

import lombok.Getter;
import lombok.Setter;
import net.minestom.server.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.qiuhua.troveserver.arcartx.internal.network.encryptor.AESEncryptor;
import org.qiuhua.troveserver.arcartx.internal.network.packet.NetworkMessageSender;
import org.qiuhua.troveserver.arcartx.util.collections.CallBack;
import org.qiuhua.troveserver.player.RPGPlayer;

import java.util.*;

public class ArcartXPlayer extends ArcartXEntity{

    @Getter
    private final Player player;


    @Setter
    @Getter
    @Nullable
    private AESEncryptor encryptor;


    @Setter
    @Getter
    @Nullable
    private String key;

    /**
     * 回调
     */
    @Getter
    private final Map<String, CallBack> callbacks = new HashMap<>();

    /**
     * crc64验证
     */
    @Getter
    private final Set<Long> crc64 = new HashSet<>();

    /**
     * 控制器id
     */
    @Getter
    @Nullable
    private String controller;

    /**
     * 模型id
     */
    @Getter
    @Nullable
    private String playerModel;

    /**
     * 玩家模型缩放
     */
    @Getter
    private double playerModelScale = 1;

    /**
     * 额外模型
     */
    @Getter
    private final HashMap<String, String> extraModels = new HashMap<>();

    /**
     * 代替模型 不知道干嘛的
     */
    @Getter
    @Nullable
    private String substitutionModel;

    /**
     * 替换模式 不知道干嘛的
     */
    @Getter
    @Nullable
    private Boolean substitutionMode;

    /**
     * 发送资源
     */
    @Getter
    @Nullable
    private Boolean sentResource;

    /**
     * 发送资源重新加载
     */
    @Getter
    @Nullable
    private Boolean sentResourceReload;


    public ArcartXPlayer(Player player){
        super(player);
        this.player = player;
    }


    /**
     * 设置玩家模型
     * @param modelID 模型ID
     * @param scale 缩放比例
     */
    @Override
    public void setModel(String modelID, double scale) {
        //调用父类方法
        super.setModel(modelID, scale);

        //更新本地变量
        this.playerModel = modelID;
        this.playerModelScale = scale;
    }

    /**
     * 移除玩家模型
     */
    @Override
    public void removeModel() {
        //调用父类方法
        super.removeModel();

        //重置本地变量
        this.playerModel = null;
        this.playerModelScale = 1.0;
    }

    /**
     * 添加额外模型
     * @param locator 定位器
     * @param modelID 模型ID
     */
    public final void addExtraModel(String locator, String modelID) {
        //添加到本地映射
        this.extraModels.put(locator, modelID);

        // TODO: 发送给所有可见玩家
    }

    /**
     * 移除额外模型
     * @param locator 定位器
     */
    public final void removeExtraModel(String locator) {
        //从本地映射移除
        this.extraModels.remove(locator);

        // TODO: 发送给所有可见玩家
    }

    /**
     * 清空所有额外模型
     */
    public final void clearExtraModels() {
        //清空本地映射
        this.extraModels.clear();

        // TODO: 发送给所有可见玩家
    }

    /**
     * 设置替换模型
     * @param modelID 模型ID
     * @param mode 替换模式
     */
    public final void setSubstitutionModel(String modelID, boolean mode) {
        // 更新本地变量
        this.substitutionModel = modelID;
        this.substitutionMode = mode;

        // TODO: 发送给所有可见玩家

    }

    /**
     * 开始被其他玩家观察
     * @param target 观察者
     */
    @Override
    public void startSeenBy(Player target) {
        // 调用父类方法
        super.startSeenBy(target);

        // TODO: 发送控制器信息、发送所有额外模型、发送替换模型

    }

    /**
     * 设置控制器
     * @param controller 控制器名称
     */
    public final void setController(String controller) {
        // 更新本地变量
        this.controller = controller;

        // TODO: 发送给所有可见玩家
    }

    /**
     * 尝试使用模型（临时）
     * @param modelID 模型ID
     * @param scale 缩放比例
     * @param time 持续时间（毫秒）
     */
    public final void tryModel(String modelID, double scale, long time) {
        // 设置临时模型
        super.setModel(modelID, scale);

        // TODO: 设置模型并创建延迟任务还原模型
    }

    /**
     * 设置状态
     * @param controller 控制器
     * @param state 状态
     */
    public final void setState(String controller, String state) {
        // TODO: 发送给所有可见玩家

    }

    /**
     * 设置状态（带速度）
     * @param controller 控制器
     * @param state 状态
     * @param speed 速度
     */
    public final void setState(String controller, String state, double speed) {
        // TODO: 发送给所有可见玩家
    }

    /**
     * 添加路径点
     * @param id 路径点ID
     * @param title 标题
     * @param waypointConfigId 路径点配置ID
     * @param x X坐标
     * @param y Y坐标
     * @param z Z坐标
     */
    public final void addWayPoint(String id, String title, String waypointConfigId, double x, double y, double z) {
        // TODO: 发送路径点给玩家
    }

    /**
     * 删除路径点
     * @param id 路径点ID
     * @param regex 是否使用正则表达式匹配
     */
    public final void deleteWayPoint(String id, boolean regex) {
        // TODO: 给玩家删除路径点
    }

    /**
     * 清空所有路径点
     */
    public final void clearWayPoint() {
        // TODO: 给玩家删除路径点
    }



    /**
     * 添加伤害显示（指定坐标）
     * @param damageDisplayConfigId 伤害显示配置ID
     * @param x X坐标
     * @param y Y坐标
     * @param z Z坐标
     * @param damage 伤害值
     */
    public final void addDamageDisplay(String damageDisplayConfigId, double x, double y, double z, double damage) {
        // TODO: 发送伤害显示
    }

    /**
     * 为自己播放音效
     * @param resourcePath 资源路径
     * @param soundCategory 音效类别
     * @param pitch 音调
     * @param keepTime 持续时间
     */
    public final void playSoundForSelf(String resourcePath, String soundCategory, float pitch, int keepTime) {
        String entityId = this.player.getUuid().toString();
        // TODO: 发送音效

    }


    /**
     * 发送自定义数据包
     * @param id 数据包ID
     * @param data 数据
     */
    public final void sendCustomPacket(String id, String... data) {
        NetworkMessageSender.sendCustomPacket(this.player, id, Arrays.copyOf(data, data.length));
    }

    /**
     * 设置客户端标题
     * @param text 标题文本
     */
    public final void setClientTitle(String text) {
        NetworkMessageSender.sendClientTitle(this.player, text);
    }

    /**
     * 发送客户端脚本
     * @param code 客户端脚本
     */
    public final void parseShimmer(@NotNull String code) {
        // TODO: 发送脚本解析
    }


    /**
     * 设置第三人称视角
     * @param enable 是否启用
     */
    public final void setThirdPerson(boolean enable) {
        NetworkMessageSender.setThirdPerson(this.player, enable);
    }

    /**
     * 设置视角锁定
     * @param enable 是否启用
     */
    public final void setViewLock(boolean enable) {
        NetworkMessageSender.setViewLock(this.player, enable ? 2 : 0);
    }

    /**
     * 设置视角锁定模式
     * @param mode 模式
     */
    public final void setViewLockMode(int mode) {
        NetworkMessageSender.setViewLock(this.player, mode);
    }

    /**
     * 从预设设置摄像机
     * @param id 预设ID
     */
    public final void setCameraFromPreset(String id) {
        NetworkMessageSender.setCameraFromPreset(this.player, id);
    }

    /**
     * 设置摄像机位置
     * @param offsetX X偏移
     * @param offsetY Y偏移
     * @param offsetZ Z偏移
     * @param freeView 是否自由视角
     */
    public final void setCameraLocation(double offsetX, double offsetY, double offsetZ, boolean freeView) {
        NetworkMessageSender.setCamera(this.player, offsetX, offsetY, offsetZ, freeView);
    }

    /**
     * 开始场景摄像机
     * @param sceneId 场景ID
     */
    public final void startSceneCamera(String sceneId) {
        NetworkMessageSender.sendSceneCamera(this.player, sceneId);
    }

    /**
     * 停止场景摄像机
     */
    public final void stopSceneCamera() {
        NetworkMessageSender.sendSceneCameraStop(this.player);
    }



    /**
     * 启用着色器
     * @param shader 着色器名称
     */
    public final void enableShader(String shader) {
        // TODO: 发送启用着色器
    }

    /**
     * 禁用着色器
     */
    public final void disableShader() {
        // TODO: 发送禁用着色器
    }

    /**
     * 设置天空盒纹理
     * @param texturePath 纹理路径
     * @param forceNoCloud 是否强制无云
     */
    public final void setSkyTexture(String texturePath, boolean forceNoCloud) {
        // TODO: 发送天空盒纹理
    }

    /**
     * 清空天空盒纹理
     */
    public final void clearSkyTexture() {
        // TODO: 发送清空天空盒纹理
    }


    /**
     * 生成基岩版粒子
     * @param id 粒子ID
     * @param x X坐标
     * @param y Y坐标
     * @param z Z坐标
     * @param yaw 偏航角
     * @param pitch 俯仰角
     */
    public final void spawnBedrockParticle(String id, double x, double y, double z, float yaw, float pitch) {
        String[] data = new String[]{
                id,
                String.valueOf(x),
                String.valueOf(y),
                String.valueOf(z),
                String.valueOf(yaw),
                String.valueOf(pitch)
        };
        // TODO: 发送基岩版粒子
    }

    /**
     * 播放方块动画
     * @param x X坐标
     * @param y Y坐标
     * @param z Z坐标
     * @param animation 动画名称
     * @param speed 速度
     * @param transitionTime 过渡时间
     * @param keepTime 持续时间
     */
    public final void playBlockAnimation(int x, int y, int z, @NotNull String animation, double speed, int transitionTime, long keepTime) {
        // TODO: 发送播放方块动画
    }

    /**
     * 生成锤子破裂效果
     * @param x X坐标
     * @param y Y坐标
     * @param z Z坐标
     * @param radius 半径
     * @param depth 深度
     * @param in 进入时间
     * @param keep 保持时间
     * @param out 退出时间
     * @param mode 模式
     */
    public final void spawnHammerCrackEffect(int x, int y, int z, float radius, float depth, int in, int keep, int out, int mode) {
        // TODO: 发送锤子破裂效果
    }

    /**
     * 发送聊天卡片
     * @param cardID 卡片ID
     * @param cardData 卡片数据
     */
    public final void sendChatCard(String cardID, Map<String, String> cardData) {
        // TODO: 发送聊天卡片
    }

//    /**
//     * 添加世界纹理
//     * @param id 纹理ID
//     * @param builder 纹理构建器
//     * @param effectPosition 效果位置
//     */
//    public final void addWorldTexture(String id, WorldTextureBuilder builder, EffectPosition effectPosition) {
//        // TODO: 发送世界纹理
//    }
//
//    /**
//     * 移除世界纹理
//     * @param id 纹理ID
//     */
//    public final void removeWorldTexture(String id) {
//        // TODO: 发送移除世界纹理
//    }



}
