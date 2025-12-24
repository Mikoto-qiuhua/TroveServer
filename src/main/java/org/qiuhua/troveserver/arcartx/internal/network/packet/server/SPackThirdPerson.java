package org.qiuhua.troveserver.arcartx.internal.network.packet.server;

import com.google.gson.annotations.SerializedName;
import lombok.Getter;

public class SPackThirdPerson implements ServerPacketBase {

    @SerializedName(value="thirdPerson")
    @Getter
    private final boolean thirdPerson;

    public SPackThirdPerson(boolean thirdPerson) {
        this.thirdPerson = thirdPerson;
    }
}
