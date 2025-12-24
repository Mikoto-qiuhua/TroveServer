package org.qiuhua.troveserver.arcartx.internal.network.packet.server;

import com.google.gson.annotations.SerializedName;
import lombok.Getter;
import org.qiuhua.troveserver.arcartx.core.config.camera.CameraElement;

public class SPackCameraElement implements ServerPacketBase{

    @SerializedName(value="element")
    @Getter
    private final CameraElement element;

    public SPackCameraElement(CameraElement element) {
        this.element = element;
    }

}
