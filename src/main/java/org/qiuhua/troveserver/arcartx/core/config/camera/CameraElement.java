package org.qiuhua.troveserver.arcartx.core.config.camera;

import com.google.gson.annotations.SerializedName;
import lombok.Getter;
import lombok.Setter;
import org.qiuhua.troveserver.utils.yaml.ConfigurationSection;

public class CameraElement {

    @SerializedName(value="offsetX")
    @Getter @Setter
    private double offsetX;

    @SerializedName(value="offsetY")
    @Getter @Setter
    private double offsetY;

    @SerializedName(value="offsetZ")
    @Getter @Setter
    private double offsetZ;

    @SerializedName(value="enableFree")
    @Getter @Setter
    private boolean enableFree;

    @SerializedName(value="hideHead")
    @Getter @Setter
    private boolean hideHead;

    @SerializedName(value="bezier")
    @Getter @Setter
    private Bezier bezier = new Bezier();

    @SerializedName(value="transition")
    @Getter @Setter
    private int transition;

    public CameraElement(ConfigurationSection config){
        offsetX = config.getDouble("offsetX", -3.0);
        offsetY = config.getDouble("offsetY", 0.5);
        offsetZ = config.getDouble("offsetZ", 3.0);
        enableFree = config.getBoolean("enableFree", true);
        transition = config.getInt("transition", 1000);
        ConfigurationSection section = config.getConfigurationSection("bezier");
        if(section != null){
            bezier = new Bezier(section);
        }

    }


}
