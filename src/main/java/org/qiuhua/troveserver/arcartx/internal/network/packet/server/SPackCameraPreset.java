package org.qiuhua.troveserver.arcartx.internal.network.packet.server;

import com.google.gson.annotations.SerializedName;
import lombok.Getter;

public class SPackCameraPreset implements ServerPacketBase{

    @SerializedName(value="name")
    @Getter
    private final String name;

    public SPackCameraPreset(String name) {
        this.name = name;
    }
}
