package org.qiuhua.troveserver.module.role.config;

import org.qiuhua.troveserver.Main;
import org.qiuhua.troveserver.api.config.IConfig;
import org.qiuhua.troveserver.utils.FileUtils;
import org.qiuhua.troveserver.utils.yaml.file.YamlConfiguration;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class RoleFileConfig implements IConfig {

    public final static Map<String, YamlConfiguration> allRoleConfig = new HashMap<>();

    @Override
    public void reload() {
        load();
    }

    @Override
    public void load() {
        allRoleConfig.clear();
        //如果文件夹不在 才生成
        if (!(new File(FileUtils.getDataFolder() , "role/roles").exists())){
            FileUtils.saveResource("role/roles/warrior.yml", false);
        }
        Map<String, YamlConfiguration> map = FileUtils.loadFiles(new File(FileUtils.getDataFolder(), "role/roles"));
        allRoleConfig.putAll(map);
        Main.getLogger().info("加载Role配置文件 {} 个", map.size());
    }
}
