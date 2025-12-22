package org.qiuhua.troveserver.arcartx.internal.network.packet.server;

import com.google.gson.annotations.SerializedName;

/**
 * 客户端标题数据包
 * 服务器发送给客户端，显示自定义标题
 */
public class SPackClientTitle implements ServerPacketBase {

    /**
     * 标题文本内容
     */
    @SerializedName("text")
    private final String text;

    /**
     * 构造函数
     *
     * @param text 要显示的标题文本
     */
    public SPackClientTitle(String text) {
        this.text = text;
    }

    /**
     * 获取标题文本
     *
     * @return 标题文本内容
     */
    public String getText() {
        return this.text;
    }
}
