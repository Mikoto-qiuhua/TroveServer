package org.qiuhua.troveserver.arcartx.core.config.camera;

import com.google.gson.annotations.SerializedName;
import lombok.Getter;
import org.qiuhua.troveserver.api.config.IConfig;
import org.qiuhua.troveserver.utils.FileUtils;
import org.qiuhua.troveserver.utils.yaml.file.YamlConfiguration;

import java.io.File;

public class CameraSetting implements IConfig {




    /**
     * 是否开启相机功能
     */
    @SerializedName(value="enable")
    @Getter
    private boolean enable;

    /**
     * 视角锁定模式，0-关闭，1-强制锁定第一人称，2-强制锁定第三人称
     */
    @SerializedName(value="forceMode")
    @Getter
    private int force;

    /**
     * 默认预设
     */
    @SerializedName(value="default")
    @Getter
    private String defaultCamera;



    /**
     * 重载配置
     */
    @Override
    public void reload() {
        load();
    }

    /**
     * 加载配置
     */
    @Override
    public void load() {
        if (!(new File(FileUtils.getDataFolder() , "arcartx/camera/setting.yml").exists())){
            FileUtils.saveResource("arcartx/camera/setting.yml", false);
        }
        YamlConfiguration config = FileUtils.loadFile("arcartx/camera/setting.yml");
        enable = config.getBoolean("enable", true);
        force = config.getInt("forceMode", 0);
        defaultCamera = config.getString("default", "idle");
    }

}
