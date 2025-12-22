package org.qiuhua.troveserver.arcartx.internal.network.packet.server;

import com.google.gson.annotations.SerializedName;
import lombok.Getter;

import java.util.List;

public class SPackCustomPacket implements ServerPacketBase{

    @SerializedName(value="id")
    @Getter
    private final String id;
    @SerializedName(value="data")
    @Getter
    private final List<String> data;

    public SPackCustomPacket(String id, List<String> data) {
        this.id = id;
        this.data = data;
    }

}
