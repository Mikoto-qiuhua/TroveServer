package org.qiuhua.troveserver.arcartx.core.config.key.group;

import com.google.gson.annotations.SerializedName;
import lombok.Getter;
import lombok.Setter;
import org.qiuhua.troveserver.Main;
import org.qiuhua.troveserver.arcartx.util.collections.KeyCallBack;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class KeyGroupElement {

    /**
     * 按键组合ID
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
     * 按键间隔时间（毫秒）
     */
    @SerializedName("interval")
    @Getter
    private final int interval;

    /**
     * 回调
     */
    @Getter @Setter
    private transient KeyCallBack callBack;


    /**
     * 创建一个按键组元素
     * @param id 配置id
     * @param keys 按键组列表
     * @param interval 按键间隔
     */
    public KeyGroupElement(String id, List<String> keys, int interval) {
        this.id = id;
        this.keys.addAll(keys);
        this.interval = interval;
    }

}
