package org.qiuhua.troveserver.arcartx.internal.network.packet.server;

import com.google.gson.annotations.SerializedName;
import lombok.Getter;

public class SPackLockView implements ServerPacketBase {

    @SerializedName(value="lock")
    @Getter
    private final int lock;

    public SPackLockView(int lock) {
        this.lock = lock;
    }

}