package org.qiuhua.troveserver.arcartx.core.config.key.group;

import org.jetbrains.annotations.Nullable;
import org.qiuhua.troveserver.Main;
import org.qiuhua.troveserver.api.config.IConfig;
import org.qiuhua.troveserver.arcartx.util.collections.KeyCallBack;
import org.qiuhua.troveserver.utils.FileUtils;
import org.qiuhua.troveserver.utils.yaml.file.YamlConfiguration;

import java.io.File;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class KeyGroupFolder implements IConfig {


    public static final HashMap<String, KeyGroupElement> keyGroups = new HashMap<>();

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
        keyGroups.clear();
        if (!(new File(FileUtils.getDataFolder() , "arcartx/key_bind/key_group").exists())){
            FileUtils.saveResource("arcartx/key_bind/key_group/按键组示例.yml", false);
        }
        Map<String, YamlConfiguration> map = FileUtils.loadFiles(new File(FileUtils.getDataFolder(), "arcartx/key_bind/key_group"));
        map.values().forEach(config -> {
            for(String key : config.getKeys(false)){
                List<String> keys = config.getStringList(key + ".keys");
                int interval = config.getInt(key + ".interval", 100);
                keyGroups.put(key, new KeyGroupElement(key, keys, interval));
            }
        });
        Main.getLogger().info("ArcartX -> 加载KeyGroup {} 个", keyGroups.size());
    }


    /**
     * 注册一个按键组
     * @param id 配置id
     * @param keys 这里面记录的是客户端按键的配置id
     * @param interval 按键间隔
     */
    public static KeyGroupElement register(String id, List<String> keys, int interval){
        KeyGroupElement keyGroupElement = new KeyGroupElement(id, keys, interval);
        keyGroups.put(id, keyGroupElement);
        return keyGroupElement;
    }


}
