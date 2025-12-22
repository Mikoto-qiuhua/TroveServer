package org.qiuhua.troveserver.arcartx.core.config.ui.folder;

import org.qiuhua.troveserver.Main;
import org.qiuhua.troveserver.api.config.IConfig;
import org.qiuhua.troveserver.arcartx.core.config.ui.type.Tip;
import org.qiuhua.troveserver.utils.FileUtils;
import org.qiuhua.troveserver.utils.yaml.file.YamlConfiguration;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class TipFolder implements IConfig {


    /**
     * 全部的tip配置
     */
    public static final Map<String, Tip> configs = new HashMap<>();

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
        configs.clear();
        if (!(new File(FileUtils.getDataFolder() , "arcartx/tooltip").exists())){
            FileUtils.saveResource("arcartx/tooltip/示例.yml", false);
        }
        Map<String, YamlConfiguration> map = FileUtils.loadFiles(new File(FileUtils.getDataFolder(), "arcartx/tooltip"));
        map.forEach((key, config) -> {
            if(configs.containsKey(key)){
                Main.getLogger().warn("出现重名Tip配置 {} 请注意修改,本次将越过该配置读取", key);
                return;
            }
            Tip tip = new Tip(key, config);
            configs.put(key, tip);
        });
        Main.getLogger().info("ArcartX 加载Tip {} 个", configs.size());


    }
}
