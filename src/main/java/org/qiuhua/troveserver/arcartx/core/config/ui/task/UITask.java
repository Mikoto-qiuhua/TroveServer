package org.qiuhua.troveserver.arcartx.core.config.ui.task;

import com.google.gson.annotations.SerializedName;
import lombok.Getter;
import org.qiuhua.troveserver.utils.yaml.ConfigurationSection;

public class UITask {


    @SerializedName(value="type")
    @Getter
    private final String type;

    @SerializedName(value="time")
    @Getter
    private final long time;

    @SerializedName(value="cycle")
    @Getter
    private final long cycle;

    @SerializedName(value="run")
    @Getter
    private final String run;


    public UITask(ConfigurationSection config){
        type = config.getString("type", "delay");
        time = config.getInt("time", 1000);
        run = config.getString("run", "");
        cycle = config.getInt("cycle", 1000);
    }


}
