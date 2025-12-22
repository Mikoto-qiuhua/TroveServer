package org.qiuhua.troveserver.arcartx.core.config.ui;

import com.google.gson.annotations.SerializedName;
import lombok.Getter;
import org.qiuhua.troveserver.utils.yaml.ConfigurationSection;
import org.qiuhua.troveserver.utils.yaml.file.YamlConfiguration;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

public class Control {

    /**
     * 控件类型
     */
    @Getter
    @SerializedName("type")
    private String type = "none";

    /**
     * 值名称
     */
    @SerializedName("val")
    @Getter
    private String valName = "";

    /**
     * 控件属性
     */
    @SerializedName("attribute")
    @Getter
    private Map<String, Object> attributes = new HashMap<>();

    /**
     * 控件动作
     */
    @SerializedName("action")
    @Getter
    private Map<String, String> action = new HashMap<>();

    /**
     * 子控件
     */
    @SerializedName("children")
    @Getter
    private LinkedHashMap<String, Control> children = new LinkedHashMap<>();




    public Control(){
    }




    public static Control parseControl(ConfigurationSection config) {
        Control control = new Control();
        //解析基本属性
        if(config.contains("type")) {
            control.type = config.getString("type", "");
        }
        if(config.contains("val")) {
            control.valName = config.getString("val", "");
        }
        //解析属性
        if(config.contains("attribute")) {
            ConfigurationSection attrConfig = config.getConfigurationSection("attribute");
            if (attrConfig != null) {
                for (String key : attrConfig.getKeys(false)) {
                    Object value = attrConfig.get(key);
                    control.attributes.put(key, value);
                }
            }
        }
        //解析动作
        if(config.contains("action")) {
            ConfigurationSection actionConfig = config.getConfigurationSection("action");
            if(actionConfig != null) {
                for(String key : actionConfig.getKeys(false)) {
                    String value = actionConfig.getString(key, "");
                    control.action.put(key, value);
                }
            }
        }
        //递归解析子控件
        if(config.contains("children")){
            ConfigurationSection childrenConfig = config.getConfigurationSection("children");
            if(childrenConfig != null){
                for(String childKey : childrenConfig.getKeys(false)){
                    ConfigurationSection childConfig = childrenConfig.getConfigurationSection(childKey);
                    if(childConfig != null){
                        Control childControl = parseControl(childConfig);
                        control.children.put(childKey, childControl);
                    }
                }
            }
        }
        return control;
    }














}
