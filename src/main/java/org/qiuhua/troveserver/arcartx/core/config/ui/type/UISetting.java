package org.qiuhua.troveserver.arcartx.core.config.ui.type;

import com.google.gson.annotations.SerializedName;
import lombok.Getter;
import lombok.Setter;
import org.jetbrains.annotations.Nullable;
import org.qiuhua.troveserver.Main;
import org.qiuhua.troveserver.utils.yaml.ConfigurationSection;
import org.qiuhua.troveserver.utils.yaml.file.YamlConfiguration;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UISetting {

    /**
     * 匹配条件
     */
    @Getter
    @Setter
    @SerializedName("match")
    private List<String> match = new ArrayList<>();

    /**
     * 隐藏条件
     */
    @Getter @Setter
    @SerializedName("hide")
    private List<String> hide = new ArrayList<>();

    /**
     * 物品大小
     */
    @Getter @Setter
    @SerializedName("itemSize")
    private String itemSize = "16";

    /**
     * 是否穿透（鼠标是否可穿透UI）
     */
    @Getter @Setter
    @SerializedName("through")
    private String through = "false";

    /**
     * 按ESC键是否关闭UI
     */
    @Getter @Setter
    @SerializedName("escClose")
    private String escClose = "true";

    /**
     * 是否显示背景
     */
    @Getter @Setter
    @SerializedName("background")
    private String background = "true";

    /**
     * 玩家死亡时是否关闭UI
     */
    @Getter @Setter
    @SerializedName("closeDied")
    private String closeDied = "true";

    /**
     * 是否显示UI
     */
    @Getter @Setter
    @SerializedName("show")
    private String show = "true";

    /**
     * JEI兼容设置
     */
    @Getter @Setter
    @SerializedName("jei")
    private String jei = "false";

    /**
     * UI层级（用于控制多个UI的显示顺序）
     */
    @Getter @Setter
    @SerializedName("level")
    private String level = "0";

    /**
     * UI动作配置
     */
    @Getter @Setter
    @SerializedName("action")
    private Map<String, String> actions = new HashMap<>();

    /**
     * 数据包处理器配置
     */
    @Getter @Setter
    @SerializedName("packetHandler")
    private Map<String, String> packetHandler = new HashMap<>();

    /**
     * 是否为HUD（抬头显示器）类型
     */
    @Getter @Setter
    @SerializedName("isHud")
    private String isHud = "false";

    /**
     * 是否默认打开
     */
    @Getter @Setter
    @SerializedName("defaultOpen")
    private String defaultOpen = "true";


    /**
     * 这里要给ui的配置节点
     * @param config
     */
    public UISetting(@Nullable ConfigurationSection config){
        if(config == null) return;
        match = config.getStringList("match");
        hide = config.getStringList("hide");
        itemSize = config.getString("itemSize", "16");
        through = config.getString("through", "false");
        escClose = config.getString("escClose", "true");
        background = config.getString("background", "true");
        closeDied = config.getString("closeDied", "true");
        show = config.getString("show", "true");
        jei = config.getString("jei", "false");
        level = config.getString("level", "0");
        ConfigurationSection actionSection = config.getConfigurationSection("action");
        if(actionSection != null){
            for(String key : actionSection.getKeys(false)){
                actions.put(key, actionSection.getString(key));
            }
        }
        ConfigurationSection packetHandlerSection = config.getConfigurationSection("packetHandler");
        if(packetHandlerSection != null){
            for(String key : packetHandlerSection.getKeys(false)){
                packetHandler.put(key, packetHandlerSection.getString(key));
            }
        }
        isHud = config.getString("isHud", "false");
        defaultOpen = config.getString("defaultOpen", "false");
    }

}
