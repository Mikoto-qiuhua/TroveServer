package org.qiuhua.troveserver.arcartx.core.config.key.client;

import org.qiuhua.troveserver.Main;
import org.qiuhua.troveserver.api.config.IConfig;
import org.qiuhua.troveserver.utils.FileUtils;
import org.qiuhua.troveserver.utils.yaml.file.YamlConfiguration;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class ClientKeyFolder implements IConfig {


    public static final Map<String, ClientKeyElement> clientKeys = new HashMap<>();



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
        clientKeys.clear();
        if (!(new File(FileUtils.getDataFolder() , "arcartx/key_bind/client_key").exists())){
            FileUtils.saveResource("arcartx/key_bind/client_key/客户端按键示例.yml", false);
        }
        Map<String, YamlConfiguration> map = FileUtils.loadFiles(new File(FileUtils.getDataFolder(), "arcartx/key_bind/client_key"));
        map.values().forEach(config -> {
            for(String key : config.getKeys(false)){
                String category = config.getString(key + ".category", "ArcartX 自定义按键");
                String defaultKey = config.getString(key + ".default", "M");
                clientKeys.put(key, new ClientKeyElement(key, category, defaultKey));
            }
        });

        Main.getLogger().info("ArcartX -> 加载ClientKey {} 个", clientKeys.size());
    }


    /**
     * 注册一个按键组
     * @param id 配置id
     * @param category 类别
     * @param defaultKey 默认按键
     */
    public static ClientKeyElement register(String id, String category, String defaultKey){
        ClientKeyElement clientKeyElement = new ClientKeyElement(id, category, defaultKey);
        clientKeys.put(id, clientKeyElement);
        return clientKeyElement;
    }

}
