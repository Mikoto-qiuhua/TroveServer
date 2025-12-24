package org.qiuhua.troveserver.arcartx.internal.network.packet.server;

import com.google.gson.annotations.SerializedName;
import lombok.Getter;

public class SPackCamera implements ServerPacketBase{

    @SerializedName(value="x")
    @Getter
    private final double x;

    @SerializedName(value="y")
    @Getter
    private final double y;

    @SerializedName(value="z")
    @Getter
    private final double z;

    @SerializedName(value="freeView")
    @Getter
    private final boolean freeView;

    public SPackCamera(double x, double y, double z, boolean freeView) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.freeView = freeView;
    }


}
