package org.qiuhua.troveserver.arcartx;

import net.minestom.server.MinecraftServer;
import net.minestom.server.entity.Player;
import net.minestom.server.event.player.*;
import org.qiuhua.troveserver.Main;
import org.qiuhua.troveserver.arcartx.core.config.Setting;
import org.qiuhua.troveserver.arcartx.core.config.camera.CameraPresetFolder;
import org.qiuhua.troveserver.arcartx.core.config.camera.CameraSetting;
import org.qiuhua.troveserver.arcartx.core.config.camera.SceneCameraFolder;
import org.qiuhua.troveserver.arcartx.core.config.font.FontIconFolder;
import org.qiuhua.troveserver.arcartx.core.config.key.client.ClientKeyFolder;
import org.qiuhua.troveserver.arcartx.core.config.key.group.KeyGroupFolder;
import org.qiuhua.troveserver.arcartx.core.config.key.simple.SimpleKeyFolder;
import org.qiuhua.troveserver.arcartx.core.config.ui.folder.TipFolder;
import org.qiuhua.troveserver.arcartx.core.config.ui.folder.UIFolder;
import org.qiuhua.troveserver.arcartx.core.entity.ArcartXEntityManager;
import org.qiuhua.troveserver.arcartx.core.ui.ArcartXUIRegistry;
import org.qiuhua.troveserver.arcartx.core.ui.adapter.ArcartXUI;
import org.qiuhua.troveserver.arcartx.core.ui.adapter.CallBackType;
import org.qiuhua.troveserver.arcartx.event.client.ClientChannelEvent;
import org.qiuhua.troveserver.arcartx.event.client.ClientInitializedEvent;
import org.qiuhua.troveserver.arcartx.internal.network.packet.NetWorkManager;
import org.qiuhua.troveserver.arcartx.internal.network.packet.NetworkMessageSender;
import org.qiuhua.troveserver.config.ConfigManager;
import org.qiuhua.troveserver.module.role.ui.RoleMainUi;
import org.qiuhua.troveserver.player.RPGPlayer;

import java.util.List;

public class ArcartX {


    public static NetWorkManager netWorkManager;



    public static void init(){
        //加载配置文件
        ConfigManager.loadConfig("arcartx", "setting", new Setting());
        ConfigManager.loadConfig("arcartx", "tip", new TipFolder());
        ConfigManager.loadConfig("arcartx", "ui", new UIFolder());
        ConfigManager.loadConfig("arcartx", "client_key", new ClientKeyFolder());
        ConfigManager.loadConfig("arcartx", "key_group", new KeyGroupFolder());
        ConfigManager.loadConfig("arcartx", "simple_key", new SimpleKeyFolder());
        ConfigManager.loadConfig("arcartx", "font_icon", new FontIconFolder());
        ConfigManager.loadConfig("arcartx", "camera_setting", new CameraSetting());
        ConfigManager.loadConfig("arcartx", "camera_preset", new CameraPresetFolder());
        ConfigManager.loadConfig("arcartx", "camera_scene", new SceneCameraFolder());



        //临时加载需要的监听器
        //向连接的玩家客户端发送秘钥?
        MinecraftServer.getGlobalEventHandler().addListener(AsyncPlayerConfigurationEvent.class, event -> {
            Player player = event.getPlayer();
            MinecraftServer.getGlobalEventHandler().call(new ClientChannelEvent(player));
            NetworkMessageSender.sendPlayerJoinPacket(player);
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

        MainMenuCallBack();

        Main.getLogger().info("ArcartX 已启用");
    }


    /**
     * 主菜单的界面回调注册
     */
    private static void MainMenuCallBack(){
        ArcartXUI arcartXUI = ArcartXUIRegistry.get("MainMenu");
        if(arcartXUI == null) return;
        //回调函数 包处理
        arcartXUI.registerCallBack(CallBackType.PACKET, callData -> {
            //包头
            String identifier = callData.getIdentifier();
            //数据合集
            List<String> data = callData.getData();
            Player player = callData.getPlayer();
            if (data.isEmpty()) return;
            Main.getLogger().debug("收到包 {} | {}", identifier, data);
            if (player instanceof RPGPlayer rpgPlayer) {
                switch (identifier) {
                    //
                    case "openRole":
                        RoleMainUi.open(rpgPlayer);
                        break;

                }
            }

        });
    }

}
