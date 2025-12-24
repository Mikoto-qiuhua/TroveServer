package org.qiuhua.troveserver.arcartx.core.config.camera;

import org.qiuhua.troveserver.Main;
import org.qiuhua.troveserver.api.config.IConfig;
import org.qiuhua.troveserver.utils.FileUtils;
import org.qiuhua.troveserver.utils.yaml.file.YamlConfiguration;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class SceneCameraFolder implements IConfig {


    public static final HashMap<String, SceneCamera> sceneCameras = new HashMap<>();

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
        sceneCameras.clear();
        if (!(new File(FileUtils.getDataFolder() , "arcartx/camera/scene").exists())){
            FileUtils.saveResource("arcartx/camera/scene/场景相机示例.yml", false);
        }
        Map<String, YamlConfiguration> map = FileUtils.loadFiles(new File(FileUtils.getDataFolder(), "arcartx/camera/scene"));
        map.forEach((key, value) -> {
            SceneCamera sceneCamera = new SceneCamera(value);
            sceneCameras.put(key, sceneCamera);
        });

        Main.getLogger().info("ArcartX -> 加载CameraScene {} 个", sceneCameras.size());
    }



}
