package org.qiuhua.troveserver.arcartx.core.config.key.client;

import org.jetbrains.annotations.Nullable;
import org.qiuhua.troveserver.Main;
import org.qiuhua.troveserver.api.config.IConfig;
import org.qiuhua.troveserver.arcartx.core.config.key.group.KeyGroupElement;
import org.qiuhua.troveserver.arcartx.util.collections.KeyCallBack;
import org.qiuhua.troveserver.utils.FileUtils;
import org.qiuhua.troveserver.utils.yaml.file.YamlConfiguration;

import java.io.File;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class ClientKeyFolder implements IConfig {


    public static final Map<String, ClientKeyElement> clientKey = new HashMap<>();



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
        clientKey.clear();
        if (!(new File(FileUtils.getDataFolder() , "arcartx/key_bind/client_key").exists())){
            FileUtils.saveResource("arcartx/key_bind/client_key/客户端按键示例.yml", false);
        }
        Map<String, YamlConfiguration> map = FileUtils.loadFiles(new File(FileUtils.getDataFolder(), "arcartx/key_bind/client_key"));
        map.values().forEach(config -> {
            for(String key : config.getKeys(false)){
                String category = config.getString(key + ".category", "ArcartX 自定义按键");
                String defaultKey = config.getString(key + ".default", "M");
                clientKey.put(key, new ClientKeyElement(key, category, defaultKey));
            }
        });

        Main.getLogger().info("ArcartX -> 加载Client_Key {} 个", clientKey.size());
    }


    /**
     * 注册一个按键组
     * @param id 配置id
     * @param category 类别
     * @param defaultKey 默认按键
     */
    public static ClientKeyElement register(String id, String category, String defaultKey){
        ClientKeyElement clientKeyElement = new ClientKeyElement(id, category, defaultKey);
        clientKey.put(id, clientKeyElement);
        return clientKeyElement;
    }

}
