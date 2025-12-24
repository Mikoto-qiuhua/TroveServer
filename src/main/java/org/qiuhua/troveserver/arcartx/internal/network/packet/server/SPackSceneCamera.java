package org.qiuhua.troveserver.arcartx.internal.network.packet.server;

import com.google.gson.annotations.SerializedName;
import lombok.Getter;
import org.qiuhua.troveserver.arcartx.core.config.camera.SceneCamera;

public class SPackSceneCamera implements ServerPacketBase {

    @SerializedName(value="scene")
    @Getter
    private final SceneCamera scene;

    public SPackSceneCamera(SceneCamera scene) {
        this.scene = scene;
    }
}
