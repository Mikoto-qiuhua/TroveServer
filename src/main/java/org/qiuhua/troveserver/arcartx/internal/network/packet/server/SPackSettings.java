package org.qiuhua.troveserver.arcartx.internal.network.packet.server;

import com.google.gson.annotations.SerializedName;
import org.qiuhua.troveserver.arcartx.core.config.Setting;
import org.qiuhua.troveserver.arcartx.core.config.camera.CameraElement;
import org.qiuhua.troveserver.arcartx.core.config.camera.CameraPresetFolder;
import org.qiuhua.troveserver.arcartx.core.config.camera.CameraSetting;
import org.qiuhua.troveserver.arcartx.core.config.font.FontIconFolder;
import org.qiuhua.troveserver.arcartx.core.config.key.client.ClientKeyFolder;
import org.qiuhua.troveserver.arcartx.core.config.key.group.KeyGroupFolder;
import org.qiuhua.troveserver.arcartx.core.config.key.simple.SimpleKeyFolder;
import org.qiuhua.troveserver.arcartx.core.config.ui.folder.TipFolder;
import org.qiuhua.troveserver.arcartx.core.config.ui.type.Tip;
import org.qiuhua.troveserver.arcartx.core.config.ui.type.UI;
import org.qiuhua.troveserver.arcartx.core.ui.ArcartXUIRegistry;
import org.qiuhua.troveserver.config.ConfigManager;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

public class SPackSettings implements ServerPacketBase{

    /**
     * 客户端标题
     */
    @SerializedName(value="title")
    private final String title = Setting.clientTitle;

    /**
     * 字体图标
     */
    @SerializedName(value="fontIconSetting")
    private final Map<Integer, String> fontIconSetting = new HashMap<>(FontIconFolder.fontIcons);

    /**
     * 客户端按键
     */
    @SerializedName(value="clientKeyElements")
    private final Map<String, Object> clientKeyElements = new HashMap<>(ClientKeyFolder.clientKeys);

    /**
     * 按键组
     */
    @SerializedName(value="keyGroupElements")
    private final Map<String, Object> keyGroupElements = new HashMap<>(KeyGroupFolder.keyGroups);

    /**
     * 简单按键
     */
    @SerializedName(value="simpleKeyElements")
    private final Map<String, Object> simpleKeyElements = new HashMap<>(SimpleKeyFolder.simpleKeys);

    /**
     * UI配置
     */
    @SerializedName(value="ui")
    private final Collection<UI> uiData = ArcartXUIRegistry.getRegisteredUI().values();

    /**
     * Tip配置
     */
    @SerializedName(value="tip")
    private final Collection<Tip> tipData = TipFolder.tips.values();

    /**
     * 聊天卡片
     * 未实现
     */
    @SerializedName(value="chat_card")
    private final Collection<Object> chatCardData = new HashSet<>();

    /**
     * BOSS血条
     * 未实现
     */
    @SerializedName(value="boss_bar")
    private final Collection<Object> bossBarData = new HashSet<>();

    /**
     * 物品背景渲染
     * 未实现
     */
    @SerializedName(value="item_effect")
    private final Map<String, Object> itemEffectData = new HashMap<>();

    /**
     * 全息数据
     * 未实现
     */
    @SerializedName(value="hologram_data")
    private final Map<String, Object> hologramData = new HashMap<>();

    /**
     * 坐标全息
     * 未实现
     */
    @SerializedName(value="hologram_location")
    private final Map<String, Object> hologramLocation = new HashMap<>();

    /**
     * 实体全息 血条？
     * 未实现
     */
    @SerializedName(value="hologram_entity")
    private final Map<String, Object> hologramEntity = new HashMap<>();

    /**
     * 实体模型
     * 未实现
     */
    @SerializedName(value="entity_model")
    private final Map<String, Object> entityModelData = new HashMap<>();

    /**
     * 动作控制器
     * 未实现
     */
    @SerializedName(value="action_controller")
    private final Map<String, Object> actionControllerData = new HashMap<>();


    /**
     * 相机
     * 未实现
     */
    @SerializedName(value="camera_preset")
    private final Map<String, CameraElement> cameraData = new HashMap<>(CameraPresetFolder.cameraPresets);

    /**
     * 相机设置
     * 未实现
     */
    @SerializedName(value="camera_setting")
    private final CameraSetting cameraSetting = (CameraSetting) ConfigManager.getAllConfig().get("arcartx:camera_setting");

    /**
     * 导航点
     * 未实现
     */
    @SerializedName(value="waypoint")
    private final Map<String, Object> waypoint = new HashMap<>();

    /**
     * 全息伤害
     */
    @SerializedName(value="damage_display")
    private final Map<String, Object> damageDisplay = new HashMap<>();

    /**
     * 是否重载客户端资源
     */
    @SerializedName(value="isReload")
    private final boolean isReload;

    public SPackSettings() {
        this.isReload = false;
    }

    public SPackSettings(boolean isReload) {
        this.isReload = isReload;
    }
}
