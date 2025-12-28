package org.qiuhua.troveserver.arcartx.core.config.ui.folder;

import org.qiuhua.troveserver.Main;
import org.qiuhua.troveserver.api.config.IConfig;
import org.qiuhua.troveserver.arcartx.core.ui.ArcartXUIRegistry;
import org.qiuhua.troveserver.utils.FileUtils;
import org.qiuhua.troveserver.utils.yaml.file.YamlConfiguration;

import java.io.File;
import java.util.Map;

public class UIFolder implements IConfig {


    /**
     *
     */
    @Override
    public void reload() {
        load();
    }

    /**
     *
     */
    @Override
    public void load() {
        if (!(new File(FileUtils.getDataFolder() , "arcartx/ui").exists())){
            FileUtils.saveResource("arcartx/ui/使用该功能请一定先看文档.yml", false);
        }
        Map<String, YamlConfiguration> map = FileUtils.loadFiles(new File(FileUtils.getDataFolder(), "arcartx/ui"));
        map.forEach((key, config) -> {
            if(!ArcartXUIRegistry.registeredUI.containsKey(key)){
                ArcartXUIRegistry.register(key, config);
            }else {
                ArcartXUIRegistry.reload(key, config);
            }
        });
        Main.getLogger().info("ArcartX -> 加载UI {} 个", ArcartXUIRegistry.registeredUI.size());
    }
}
