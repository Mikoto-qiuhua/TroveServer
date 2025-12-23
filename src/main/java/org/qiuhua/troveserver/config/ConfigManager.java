package org.qiuhua.troveserver.config;

import lombok.Getter;
import org.qiuhua.troveserver.Main;
import org.qiuhua.troveserver.api.config.IConfig;
import org.qiuhua.troveserver.utils.yaml.file.YamlConfiguration;

import java.io.File;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

public class ConfigManager {

    @Getter
    private final static LinkedHashMap<String, IConfig> allConfig = new LinkedHashMap<>();


    /**
     * 加载一个配置文件
     * @param namespace
     * @param id
     * @param config
     */
    public static void loadConfig(String namespace, String id , IConfig config){
        String key = namespace + ":" + id;
        if(!allConfig.containsKey(key)){
            if(Main.getLogger() != null){
                Main.getLogger().info("加载配置文件 {}", key);
            }
            config.load();
            allConfig.put(key, config);
        }
    }

    /**
     * 重新加载一个配置文件 需要完整命名空间:ID
     * @param namespaceId
     */
    public static void reloadConfig(String namespaceId){
        IConfig config = allConfig.get(namespaceId);
        if(config != null){
            Main.getLogger().info("重载载配置文件 {}", namespaceId);
            config.reload();
        }
    }

    /**
     * 使用命名空间重载该空间的全部配置
     * 注意不需要使用ID
     * @param namespace
     */
    public static void reloadAllConfig(String namespace){
        allConfig.forEach((key, config) -> {
            if(key.startsWith(namespace)){
                config.reload();
            }
        });
    }









}
