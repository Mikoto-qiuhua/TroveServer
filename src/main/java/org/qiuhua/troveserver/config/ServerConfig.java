package org.qiuhua.troveserver.config;

import net.minestom.server.coordinate.Pos;
import net.minestom.server.entity.GameMode;
import net.minestom.server.world.Difficulty;
import org.qiuhua.troveserver.Main;
import org.qiuhua.troveserver.api.config.IConfig;
import org.qiuhua.troveserver.utils.FileUtils;
import org.qiuhua.troveserver.utils.yaml.ConfigurationSection;
import org.qiuhua.troveserver.utils.yaml.file.YamlConfiguration;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class ServerConfig implements IConfig {

    public static YamlConfiguration config;
    public static String server_serverName;
    public static Boolean server_onlineMode;
    public static Integer server_compressionThreshold;
    public static String server_ip;
    public static Integer server_port;
    public static String logLevel;
    public static List<String> adminList = new ArrayList<>();
    public static String world_default;
    public static Pos world_respawnPoint;
    public static ConfigurationSection world_autoLoad_section;


    @Override
    public void reload() {
        Main.getLogger().warn("注意 此配置不允许重新加载,请重启服务器！");
    }
    @Override
    public void load(){
        if (!(new File(FileUtils.getDataFolder() , "config.yml").exists())){
            FileUtils.saveResource("config.yml", false);
        }
        config = FileUtils.loadFile("config.yml");
        server_serverName = config.getString("Server.serverName", "Server");
        server_onlineMode = config.getBoolean("Server.onlineMode", true);
        server_compressionThreshold = config.getInt("Server.compressionThreshold", 256);
        server_ip = config.getString("Server.ip", "0.0.0.0");
        server_port = config.getInt("Server.port", 25565);
        logLevel = config.getString("Server.logLevel");
        world_default = config.getString("World.default", "");
        String respawnPoint = config.getString("World.respawnPoint", null);
        adminList.addAll(config.getStringList("Server.adminList"));
        if(respawnPoint != null){
            respawnPoint = respawnPoint.trim();
            String[] parts = respawnPoint.split(",");
            if(parts.length == 5){
                world_respawnPoint = new Pos(Double.parseDouble(parts[0]), Double.parseDouble(parts[1]), Double.parseDouble(parts[2]), Float.parseFloat(parts[3]), Float.parseFloat(parts[4]));
            }
        }
        world_autoLoad_section = config.getConfigurationSection("World.autoLoad");

    }


}
