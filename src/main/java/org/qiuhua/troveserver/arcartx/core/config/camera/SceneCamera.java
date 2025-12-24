package org.qiuhua.troveserver.arcartx.core.config.camera;

import com.google.gson.annotations.SerializedName;
import lombok.Getter;
import org.qiuhua.troveserver.utils.yaml.ConfigurationSection;
import org.qiuhua.troveserver.utils.yaml.file.YamlConfiguration;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

public class SceneCamera {

    @SerializedName(value="mode")
    @Getter
    private final int mode;

    @SerializedName(value="backTime")
    @Getter
    private final int backTime;

    @SerializedName(value="step")
    @Getter
    private final LinkedHashMap<String, SceneElement> elements = new LinkedHashMap<>();

    public SceneCamera(YamlConfiguration config){
        mode = config.getInt("mode", 1);
        backTime = config.getInt("backTime", 1000);
        ConfigurationSection stepSection = config.getConfigurationSection("step");
        if(stepSection != null){
            for (String key : stepSection.getKeys(false)){
                elements.put(key, new SceneElement(stepSection.getConfigurationSection(key)));
            }
        }
    }

}
