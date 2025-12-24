package org.qiuhua.troveserver.arcartx.core.config.camera;

import com.google.gson.annotations.SerializedName;
import lombok.Getter;
import org.qiuhua.troveserver.utils.yaml.ConfigurationSection;

public class SceneElement {

    @SerializedName(value="x")
    @Getter
    private final double x;

    @SerializedName(value="y")
    @Getter
    private final double y;

    @SerializedName(value="z")
    @Getter
    private final double z;

    @SerializedName(value="yaw")
    @Getter
    private final float yaw;

    @SerializedName(value="pitch")
    @Getter
    private final float pitch;

    @SerializedName(value="bezier")
    @Getter
    private final Bezier bezier;

    @SerializedName(value="transition")
    @Getter
    private final int transition;

    @SerializedName(value="keep")
    @Getter
    private final int keep;

    public SceneElement(ConfigurationSection config) {
        x = config.getInt("x", 0);
        y = config.getInt("y", 0);
        z = config.getInt("z", 0);
        yaw = (float) config.getDouble("yaw", 0);
        pitch = (float) config.getDouble("pitch", 0);
        ConfigurationSection section = config.getConfigurationSection("bezier");
        if(section != null){
            bezier = new Bezier(section);
        }else {
            bezier = new Bezier();
        }
        transition = config.getInt("transition", 800);
        keep = config.getInt("keep", 800);
    }




}
