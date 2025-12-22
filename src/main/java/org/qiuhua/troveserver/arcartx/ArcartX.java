package org.qiuhua.troveserver.arcartx;

import net.minestom.server.MinecraftServer;
import net.minestom.server.event.player.*;
import org.qiuhua.troveserver.Main;
import org.qiuhua.troveserver.arcartx.core.config.Setting;
import org.qiuhua.troveserver.arcartx.core.config.ui.folder.TipFolder;
import org.qiuhua.troveserver.arcartx.core.config.ui.folder.UIFolder;
import org.qiuhua.troveserver.arcartx.core.entity.ArcartXEntityManager;
import org.qiuhua.troveserver.arcartx.event.client.ClientInitializedEvent;
import org.qiuhua.troveserver.arcartx.internal.network.packet.NetWorkManager;
import org.qiuhua.troveserver.arcartx.internal.network.packet.NetworkMessageSender;
import org.qiuhua.troveserver.config.ConfigManager;

public class ArcartX {


    public static NetWorkManager netWorkManager;



    public static void init(){
        //加载配置文件
        ConfigManager.loadConfig("arcartx", "setting", new Setting());
        ConfigManager.loadConfig("arcartx", "tip", new TipFolder());
        ConfigManager.loadConfig("arcartx", "ui", new UIFolder());

        //临时加载需要的监听器
        //向连接的玩家客户端发送秘钥?
        MinecraftServer.getGlobalEventHandler().addListener(AsyncPlayerConfigurationEvent.class, event -> {
            NetworkMessageSender.sendPlayerJoinPacket(event.getPlayer());
        });


        //玩家客户端加载完成
        //给玩家发送基础的配置
        MinecraftServer.getGlobalEventHandler().addListener(ClientInitializedEvent.Start.class, event -> {
            NetworkMessageSender.sendSetting(event.getPlayer());
        });


        //玩家离开服务器 卸载玩家数据
        MinecraftServer.getGlobalEventHandler().addListener(PlayerDisconnectEvent.class, event -> {
            ArcartXEntityManager.removePlayer(event.getPlayer());

        });
        netWorkManager = new NetWorkManager();

        Main.getLogger().info("ArcartX 已启用");
    }





}
