package org.qiuhua.troveserver.arcartx.internal.network.packet.server;

import com.google.gson.annotations.SerializedName;
import lombok.Getter;
import org.qiuhua.troveserver.arcartx.core.config.ui.type.UI;

public class SPackScreen implements ServerPacketBase {

    /**
     * UI配置对象
     */
    @SerializedName("ui")
    @Getter
    private final UI ui;

    public SPackScreen(UI ui) {
        this.ui = ui;
    }
}
