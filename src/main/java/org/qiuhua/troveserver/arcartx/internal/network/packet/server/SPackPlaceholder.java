package org.qiuhua.troveserver.arcartx.internal.network.packet.server;

import com.google.gson.annotations.SerializedName;
import lombok.Getter;
import lombok.ToString;

/**
 * 服务器发送自定义的数据包
 * 用于向客户端发送占位符键值对
 */
@ToString
public class SPackPlaceholder implements ServerPacketBase {
    /**
     * 包名
     */
    @SerializedName("key")
    @Getter
    private final String key;

    /**
     * 内容
     */
    @SerializedName("value")
    @Getter
    private final Object value;

    /**
     * 是否为更新操作
     */
    @SerializedName("update")
    @Getter
    private final boolean update;

    /**
     * 更新ID
     */
    @SerializedName("updateID")
    @Getter
    private final String updateID;

    /**
     * 构造函数
     * @param key 占位符键
     * @param value 占位符值
     * @param update 是否为更新操作
     * @param updateID 更新ID
     */
    public SPackPlaceholder(String key, Object value, boolean update, String updateID) {
        this.key = key;
        this.value = value;
        this.update = update;
        this.updateID = updateID;
    }


}
