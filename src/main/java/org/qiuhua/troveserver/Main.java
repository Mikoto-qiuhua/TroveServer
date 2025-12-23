package org.qiuhua.troveserver;


import lombok.Getter;

import net.minestom.server.Auth;
import net.minestom.server.MinecraftServer;
import org.qiuhua.troveserver.arcartx.ArcartX;
import org.qiuhua.troveserver.command.ServerCommand;
import org.qiuhua.troveserver.config.ConfigManager;
import org.qiuhua.troveserver.config.ServerConfig;
import org.qiuhua.troveserver.module.mob.MobManager;
import org.qiuhua.troveserver.player.RPGPlayerProvider;
import org.qiuhua.troveserver.listener.GlobalListener;
import org.qiuhua.troveserver.module.attribute.AttributeManager;
import org.qiuhua.troveserver.module.item.ItemManager;
import org.qiuhua.troveserver.module.login.LoginManager;
import org.qiuhua.troveserver.module.playermode.PlayerModeManager;
import org.qiuhua.troveserver.module.role.RoleManager;
import org.qiuhua.troveserver.module.world.WorldManager;
import org.qiuhua.troveserver.utils.task.SchedulerManager;
import org.qiuhua.troveserver.utils.ServerLogger;



public class Main {

    @Getter
    private static Main instance;
    @Getter
    private static ServerLogger logger;


    public static void main(String[] args) {
        // 初始化配置
        ConfigManager.loadConfig("server", "config", new ServerConfig());
        System.setProperty("minestom.chunk-view-distance", ServerConfig.config.getString("Flag.chunk-view-distance", "8"));
        System.setProperty("minestom.dispatcher-threads", ServerConfig.config.getString("Flag.dispatcher-threads", "1"));
        System.setProperty("minestom.entity-view-distance", ServerConfig.config.getString("Flag.entity-view-distance", "5"));
        logger = new ServerLogger();
        new Main().start();
    }

    public void start(){
        instance = this;
        logger.info("╔══════════════════════════════════╗");
        logger.info("║          Trove Server            ║");
        logger.info("║            Minestom              ║");
        logger.info("╚══════════════════════════════════╝");
        MinecraftServer server;
        if(ServerConfig.server_onlineMode){
            server = MinecraftServer.init(new Auth.Online());
            Main.getLogger().warn("正版验证启用中");
        }else {
            server = MinecraftServer.init();
            Main.getLogger().warn("正版验证未启用");
        }
        MinecraftServer.getConnectionManager().setPlayerProvider(new RPGPlayerProvider()); //修改player对象
        MinecraftServer.setBrandName(ServerConfig.server_serverName);
        MinecraftServer.setCompressionThreshold(ServerConfig.server_compressionThreshold);
        new ServerCommand(); //创建服务器命令
        SchedulerManager.init();
        WorldManager.init();
        AttributeManager.init();
        ItemManager.init();
        LoginManager.init();
        PlayerModeManager.init();
        RoleManager.init();
        MobManager.init();
        ArcartX.init();
        new GlobalListener(); //创建基础的监听器

        server.start(ServerConfig.server_ip, ServerConfig.server_port);
        logger.info("服务器启动完成！ {}:{}", ServerConfig.server_ip, ServerConfig.server_port);
        logger.info("当前管理员列表 {}", ServerConfig.adminList);
    }

    public void stop(){
        logger.info("服务器关闭中......");
        SchedulerManager.shutdown();
        MinecraftServer.stopCleanly();
        System.exit(0);
    }





}