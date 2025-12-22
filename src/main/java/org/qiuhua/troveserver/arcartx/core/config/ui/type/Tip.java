package org.qiuhua.troveserver.arcartx.core.config.ui.type;

import com.google.gson.annotations.SerializedName;
import lombok.Getter;
import lombok.Setter;
import org.qiuhua.troveserver.arcartx.core.config.ui.Control;
import org.qiuhua.troveserver.utils.yaml.ConfigurationSection;
import org.qiuhua.troveserver.utils.yaml.file.YamlConfiguration;

public class Tip {


    /**
     * 配置ID
     */
    @SerializedName("id")
    @Getter
    private final String id;

    /**
     * 提示框数据
     */
    @SerializedName("tip")
    @Getter
    private final TipData tipData;

    /**
     * 根控件
     */
    @SerializedName("root_control")
    @Getter
    private Control controls;


    public Tip(String id, YamlConfiguration config) {
        this.id = id;
        this.tipData = new TipData(config);
        ConfigurationSection controlSection = config.getConfigurationSection("root_control");
        if(controlSection != null){
            this.controls = Control.parseControl(controlSection);
        }
    }









}
