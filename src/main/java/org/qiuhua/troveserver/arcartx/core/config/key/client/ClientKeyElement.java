package org.qiuhua.troveserver.arcartx.core.config.key.client;

import com.google.gson.annotations.SerializedName;
import lombok.Getter;
import lombok.Setter;
import net.minestom.server.entity.Player;
import org.qiuhua.troveserver.arcartx.util.collections.KeyCallBack;

public class ClientKeyElement {

    /**
     * 按键ID
     */
    @SerializedName(value="id")
    @Getter @Setter
    private String id;

    /**
     * 类别
     */
    @SerializedName(value="category")
    @Getter @Setter
    private String category;

    /**
     * 默认按键
     */
    @SerializedName(value="default")
    @Getter @Setter
    private String defaultKey;

    /**
     * 回调
     */
    @Getter @Setter
    private transient KeyCallBack callBack;


    public ClientKeyElement(String id, String category, String defaultKey) {
        this.id = id;
        this.category = category;
        this.defaultKey = defaultKey;
    }

}
