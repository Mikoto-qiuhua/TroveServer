package org.qiuhua.troveserver.arcartx.core.config.ui.type;

import com.google.gson.annotations.SerializedName;
import lombok.Getter;
import org.qiuhua.troveserver.utils.yaml.file.YamlConfiguration;

import java.util.ArrayList;

public class TipData {

    /**
     * 匹配规则列表
     */
    @SerializedName("match")
    @Getter
    private ArrayList<String> match = new ArrayList<>();


    public TipData(YamlConfiguration config){
        match.addAll(config.getStringList("tip.match"));
    }



}
