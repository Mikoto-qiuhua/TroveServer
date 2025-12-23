package org.qiuhua.troveserver.arcartx.core.config.key.simple;

import net.minestom.server.MinecraftServer;
import net.minestom.server.entity.Player;
import org.jetbrains.annotations.Nullable;
import org.qiuhua.troveserver.Main;
import org.qiuhua.troveserver.api.config.IConfig;
import org.qiuhua.troveserver.arcartx.core.config.key.group.KeyGroupElement;
import org.qiuhua.troveserver.arcartx.internal.network.packet.NetworkMessageSender;
import org.qiuhua.troveserver.arcartx.util.collections.KeyCallBack;
import org.qiuhua.troveserver.config.ConfigManager;
import org.qiuhua.troveserver.config.ServerConfig;
import org.qiuhua.troveserver.utils.FileUtils;
import org.qiuhua.troveserver.utils.yaml.file.YamlConfiguration;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SimpleKeyFolder implements IConfig {


    public static final HashMap<String, SimpleKeyElement> simpleKey = new HashMap<>();

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
        simpleKey.clear();
        if (!(new File(FileUtils.getDataFolder() , "arcartx/key_bind/simple_key").exists())){
            FileUtils.saveResource("arcartx/key_bind/simple_key/简单按键示例.yml", false);
        }
        Map<String, YamlConfiguration> map = FileUtils.loadFiles(new File(FileUtils.getDataFolder(), "arcartx/key_bind/simple_key"));
        map.values().forEach(config -> {
            for(String key : config.getKeys(false)){
                List<String> keys = config.getStringList(key + ".keys");
                simpleKey.put(key, new SimpleKeyElement(key, keys));
            }
        });

        //这里注册ali+r 去重载玩家的配置 只有管理员有效果 注册的简单按键
        SimpleKeyElement simpleKeyElement = SimpleKeyFolder.register("重载配置", List.of("LEFT_ALT", "R"));
        simpleKeyElement.setCallBack(new KeyCallBack() {
            @Override
            public void onPress(Player player) {
                if(ServerConfig.adminList.contains(player.getUsername())){
                    ConfigManager.reloadAllConfig("arcartx");
                    MinecraftServer.getConnectionManager().getOnlinePlayers().forEach(player1 -> {
                        NetworkMessageSender.sendResourceReload(player, true);
                    });
                }
            }

            @Override
            public void onRelease(Player player) {

            }
        });


        Main.getLogger().info("ArcartX -> 加载Simple_Key {} 个", simpleKey.size());
    }

    /**
     * 注册一个按键组
     * @param id 配置id
     * @param keys 这里面记录的是客户端按键的配置id
     */
    public static SimpleKeyElement register(String id, List<String> keys){
        SimpleKeyElement simpleKeyElement = new SimpleKeyElement(id, keys);
        simpleKey.put(id, simpleKeyElement);
        return simpleKeyElement;
    }



}
