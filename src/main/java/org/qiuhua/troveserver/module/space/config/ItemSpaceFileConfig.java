package org.qiuhua.troveserver.module.space.config;

import org.qiuhua.troveserver.Main;
import org.qiuhua.troveserver.api.config.IConfig;
import org.qiuhua.troveserver.utils.FileUtils;
import org.qiuhua.troveserver.utils.yaml.file.YamlConfiguration;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class ItemSpaceFileConfig implements IConfig {

    public final static Map<String, ItemSpaceConfigData> allItemSpaceConfig = new HashMap<>();

    @Override
    public void reload() {
        load();
    }

    @Override
    public void load(){
        allItemSpaceConfig.clear();
        //如果文件夹不在 才生成
        if (!(new File(FileUtils.getDataFolder() , "itemspace/spaces").exists())){
            FileUtils.saveResource("itemspace/spaces/示例仓库.yml", false);
        }
        Map<String, YamlConfiguration> map = FileUtils.loadFiles(new File(FileUtils.getDataFolder(), "itemspace/spaces"));
        map.forEach((key, config) -> {
            ItemSpaceConfigData itemSpaceConfigData = new ItemSpaceConfigData(key, config);
            allItemSpaceConfig.put(key, itemSpaceConfigData);
        });

        Main.getLogger().info("加载ItemSpace配置文件 {} 个", map.size());
    }



}
