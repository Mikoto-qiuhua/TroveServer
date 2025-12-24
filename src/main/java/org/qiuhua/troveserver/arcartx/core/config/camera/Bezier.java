package org.qiuhua.troveserver.arcartx.core.config.camera;

import com.google.gson.annotations.SerializedName;
import lombok.Getter;
import lombok.Setter;
import org.qiuhua.troveserver.utils.yaml.ConfigurationSection;

public class Bezier {


    @SerializedName(value="x1")
    @Getter
    @Setter
    private double x1 = 0.1;

    @SerializedName(value="y1")
    @Getter @Setter
    private double y1 = 0.25;

    @SerializedName(value="x2")
    @Getter @Setter
    private double x2 = 0.1;

    @SerializedName(value="y2")
    @Getter @Setter
    private double y2 = 0.25;

    public Bezier(){

    }

    public Bezier(double x1, double y1, double x2, double y2){
        this.x1 = x1;
        this.y1 = y1;
        this.x2 = x2;
        this.y2 = y2;
    }

    public Bezier(ConfigurationSection config){
        this.x1 = config.getDouble("x1", 0.1);
        this.y1 = config.getDouble("y1", 0.25);
        this.x2 = config.getDouble("x2", 0.1);
        this.y2 = config.getDouble("y2", 0.25);

    }


}
