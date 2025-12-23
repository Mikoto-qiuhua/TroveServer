package org.qiuhua.troveserver.arcartx.core.config.key.simple;

import com.google.gson.annotations.SerializedName;
import lombok.Getter;
import lombok.Setter;
import org.qiuhua.troveserver.arcartx.util.collections.KeyCallBack;

import java.util.ArrayList;
import java.util.List;

public class SimpleKeyElement {

    /**
     * 按键ID
     */
    @SerializedName(value="id")
    @Getter
    private final String id;

    /**
     * 按键列表
     */
    @SerializedName("keys")
    @Getter
    private final List<String> keys = new ArrayList<>();


    /**
     * 回调
     */
    @Getter @Setter
    private transient KeyCallBack callBack;



    public SimpleKeyElement(String id, List<String> keys) {
        this.id = id;
        this.keys.addAll(keys);
    }

}
