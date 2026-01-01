package org.qiuhua.troveserver.arcartx.core.config.font;

import com.google.gson.annotations.SerializedName;
import lombok.Getter;

public class FontIcon {

    /**
     * "图标id的取值范围是0 ~ 999999"
     */
    @SerializedName(value="id")
    @Getter
    private final int id;

    /**
     * 和文字大小的比例，默认0.8
     */
    @SerializedName(value="proportion")
    @Getter
    private final double proportion;

    /**
     * 材质路径
     */
    @SerializedName(value="path")
    @Getter
    private final String path;


    public FontIcon(int id, double proportion, String path){
        this.id = id;
        this.proportion = proportion;
        this.path = path;
    }

    public FontIcon(int id, String path){
        this.id = id;
        this.proportion = 0.8;
        this.path = path;
    }


}
