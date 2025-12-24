package org.qiuhua.troveserver.arcartx.core.config.font;

import org.qiuhua.troveserver.Main;
import org.qiuhua.troveserver.api.config.IConfig;
import org.qiuhua.troveserver.utils.FileUtils;
import org.qiuhua.troveserver.utils.yaml.file.YamlConfiguration;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class FontIconFolder implements IConfig {


    public static final HashMap<Integer, String> fontIcons = new HashMap<>();

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
        fontIcons.clear();
        if (!(new File(FileUtils.getDataFolder() , "arcartx/font_icon").exists())){
            FileUtils.saveResource("arcartx/font_icon/文字图标示例.yml", false);
        }
        Map<String, YamlConfiguration> map = FileUtils.loadFiles(new File(FileUtils.getDataFolder(), "arcartx/font_icon"));
        map.values().forEach(config -> {
            for(String key : config.getKeys(false)){
                int id = config.getInt(key + ".id");
                String path = config.getString(key + ".path");
                fontIcons.put(id, path);
            }
        });
        Main.getLogger().info("ArcartX -> 加载FontIcons {} 个", fontIcons.size());
    }



}
