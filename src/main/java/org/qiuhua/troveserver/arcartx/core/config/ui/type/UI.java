package org.qiuhua.troveserver.arcartx.core.config.ui.type;

import com.google.gson.annotations.SerializedName;
import lombok.Getter;
import org.qiuhua.troveserver.arcartx.core.config.ui.Control;
import org.qiuhua.troveserver.arcartx.core.ui.adapter.ArcartXUI;
import org.qiuhua.troveserver.arcartx.core.ui.adapter.CallBackType;
import org.qiuhua.troveserver.arcartx.util.collections.UICallBack;
import org.qiuhua.troveserver.utils.yaml.ConfigurationSection;
import org.qiuhua.troveserver.utils.yaml.file.YamlConfiguration;

import java.util.*;
/**
 * 包含UI的设置、控件模板和控件定义
 */
public class UI implements ArcartXUI {
    //回调函数映射表，键为回调类型，值为对应的回调函数列表
    @Getter
    private final transient Map<CallBackType, List<UICallBack>> callbacks = new HashMap<>();

    // UI设置数据
    @SerializedName(value="ui")
    @Getter
    private final UISetting menuData;

    @SerializedName(value="controls")
    @Getter
    private final LinkedHashMap<String, Control> controls = new LinkedHashMap<>();

    @SerializedName(value="template")
    @Getter
    private final LinkedHashMap<String, Control> template = new LinkedHashMap<>();

    @Getter
    @SerializedName(value="id")
    private final String id;

    public UI(String id ,YamlConfiguration config){
        this.id = id;
        this.menuData = new UISetting(config.getConfigurationSection("ui"));
        //ui控件解析
        ConfigurationSection controlsSection = config.getConfigurationSection("controls");
        if(controlsSection != null){
            for(String controlKey : controlsSection.getKeys(false)){
                ConfigurationSection controlSection = controlsSection.getConfigurationSection(controlKey);
                if(controlSection != null){
                    Control control = Control.parseControl(controlSection);
                    controls.put(controlKey, control);
                }

            }
        }

        //ui模版解析 不知道有什么用 反正没看见wiki上有这个功能
        ConfigurationSection templatesSection = config.getConfigurationSection("template");
        if(templatesSection != null){
            for(String templateKey : templatesSection.getKeys(false)){
                ConfigurationSection templateSection = templatesSection.getConfigurationSection(templateKey);
                if(templateSection != null){
                    Control control = Control.parseControl(templateSection);
                    template.put(templateKey, control);
                }

            }
        }
    }



}
