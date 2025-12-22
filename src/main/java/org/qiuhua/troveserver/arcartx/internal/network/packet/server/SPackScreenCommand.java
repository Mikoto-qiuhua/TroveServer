package org.qiuhua.troveserver.arcartx.internal.network.packet.server;

import com.google.gson.annotations.SerializedName;
import lombok.Getter;

public class SPackScreenCommand implements ServerPacketBase{

    @SerializedName(value="cmd")
    @Getter
    private final String cmd;

    @SerializedName(value="id")
    @Getter
    private final String id;

    @SerializedName(value="code")
    @Getter
    private final String code;


    /**
     * 主构造函数
     * @param cmd 命令类型
     * @param id UI标识符
     * @param code 要执行的代码
     */
    public SPackScreenCommand(String cmd, String id, String code) {
        this.cmd = cmd;
        this.id = id;
        this.code = code;
    }

    /**
     * 处理默认参数和位掩码
     * @param cmd 命令类型
     * @param id UI标识符
     * @param code 要执行的代码（可以为null）
     * @param mask 位掩码，表示哪些参数使用了默认值
     * @param marker Kotlin默认构造器标记
     */
    public SPackScreenCommand(String cmd, String id, String code,
                              int mask, Object marker) {
        this.cmd = cmd;
        this.id = id;
        // 检查code参数是否使用了默认值（位掩码4表示第三个参数使用默认值）
        if ((mask & 4) != 0) {
            this.code = "";
        } else {
            this.code = code;
        }
    }

}
