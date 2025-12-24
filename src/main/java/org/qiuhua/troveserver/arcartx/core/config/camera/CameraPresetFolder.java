package org.qiuhua.troveserver.arcartx.core.config.camera;

import org.qiuhua.troveserver.Main;
import org.qiuhua.troveserver.api.config.IConfig;
import org.qiuhua.troveserver.arcartx.core.config.key.group.KeyGroupElement;
import org.qiuhua.troveserver.utils.FileUtils;
import org.qiuhua.troveserver.utils.yaml.file.YamlConfiguration;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CameraPresetFolder implements IConfig {


    public static final HashMap<String, CameraElement> cameraPresets = new HashMap<>();

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
        cameraPresets.clear();
        if (!(new File(FileUtils.getDataFolder() , "arcartx/camera/preset").exists())){
            FileUtils.saveResource("arcartx/camera/preset/相机预设示例.yml", false);
        }
        Map<String, YamlConfiguration> map = FileUtils.loadFiles(new File(FileUtils.getDataFolder(), "arcartx/camera/preset"));
        map.values().forEach(config -> {
            for(String key : config.getKeys(false)){
                cameraPresets.put(key, new CameraElement(config.getConfigurationSection(key)));
            }
        });
        Main.getLogger().info("ArcartX -> 加载CameraPreset {} 个", cameraPresets.size());
    }



}
