package org.qiuhua.troveserver.module.mob.config;

import org.qiuhua.troveserver.Main;
import org.qiuhua.troveserver.api.config.IConfig;
import org.qiuhua.troveserver.module.mob.MobConfig;
import org.qiuhua.troveserver.module.mob.MobManager;
import org.qiuhua.troveserver.utils.FileUtils;
import org.qiuhua.troveserver.utils.yaml.ConfigurationSection;
import org.qiuhua.troveserver.utils.yaml.file.YamlConfiguration;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class MobFileConfig implements IConfig {


    public final static Map<String, YamlConfiguration> allMobConfig = new HashMap<>();


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
        allMobConfig.clear();
        //如果文件夹不在 才生成
        if (!(new File(FileUtils.getDataFolder() , "mob/mobs").exists())){
            FileUtils.saveResource("mob/mobs/训练假人.yml", false);
        }
        Map<String, YamlConfiguration> map = FileUtils.loadFiles(new File(FileUtils.getDataFolder(), "mob/mobs"));
        allMobConfig.putAll(map);
        loadMob();
    }



    //加载配置文件中的实体
    private void loadMob(){
        MobManager.allMobConfig.clear();
        allMobConfig.forEach((key, value)->{
            MobConfig mobConfig = new MobConfig(key, value);
            MobManager.allMobConfig.put(key, mobConfig);

        });
        Main.getLogger().info("加载实体配置 {} 个",  MobManager.allMobConfig.size());
    }

}
