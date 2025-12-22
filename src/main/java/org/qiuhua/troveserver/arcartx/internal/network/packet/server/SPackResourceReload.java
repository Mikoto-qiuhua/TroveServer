package org.qiuhua.troveserver.arcartx.internal.network.packet.server;

import com.google.gson.annotations.SerializedName;
import lombok.Getter;
import net.minestom.server.registry.RegistryData;
import org.jetbrains.annotations.NotNull;
import org.qiuhua.troveserver.Main;
import org.qiuhua.troveserver.arcartx.core.config.Setting;

import java.util.HashMap;
import java.util.Map;

public class SPackResourceReload implements ServerPacketBase {


    /**
     * 是否重新加载资源
     */
    @SerializedName("reload")
    @Getter
    private final boolean reload;

    /**
     * 资源密码映射表
     * Key: 资源名称
     * Value: 资源密码
     */
    @SerializedName("initCode")
    private final Map<String, String> passwords = new HashMap<>();

    /**
     * 构造函数
     *
     * @param reload 是否重新加载资源
     */
    public SPackResourceReload(boolean reload) {
        this.reload = reload;
        Setting.resource.values().forEach(axResource -> {
            passwords.put(axResource.getName(), axResource.getPassword());
        });
    }






}
