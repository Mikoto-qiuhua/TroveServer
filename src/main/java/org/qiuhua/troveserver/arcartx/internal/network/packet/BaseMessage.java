package org.qiuhua.troveserver.arcartx.internal.network.packet;

import com.google.gson.annotations.SerializedName;
import lombok.Getter;
import lombok.Setter;
import org.jetbrains.annotations.NotNull;

/**
 * 基础消息类
 * 用于在网络通信中传输数据包的载体
 * 支持消息分割、序列化等功能
 */
public class BaseMessage {

    /**
     * 消息ID
     * 用于唯一标识一个消息
     */
    @SerializedName("id")
    @Setter
    @Getter
    private int id;

    /**
     * 消息代码/类型标识
     * 用于区分不同的消息类型或操作码
     */
    @SerializedName("code")
    @Setter
    @Getter
    private int code;

    /**
     * 消息类型
     * 通常用于标识解码类型（如Base64、AES等）
     */
    @SerializedName("type")
    @Setter
    @Getter
    private int type;

    /**
     * 消息分割总数
     * 当消息过长时会被分割，此字段表示总片段数
     * 默认值为1（表示未分割）
     */
    @SerializedName("size")
    @Setter
    @Getter
    private int size = 1;

    /**
     * 当前消息片段索引
     * 当消息被分割时，表示当前是第几个片段（从0开始）
     */
    @SerializedName("part")
    @Setter
    @Getter
    private int part;

    /**
     * 消息内容
     * 实际的消息数据，可能是加密或编码后的字符串
     */
    @SerializedName("message")
    @Setter
    @Getter
    private String message = "";

    public BaseMessage(){

    }

}