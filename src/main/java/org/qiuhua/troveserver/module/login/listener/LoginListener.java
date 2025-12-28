package org.qiuhua.troveserver.module.login.listener;

import net.minestom.server.MinecraftServer;
import net.minestom.server.event.GlobalEventHandler;
import net.minestom.server.event.player.PlayerBlockBreakEvent;
import net.minestom.server.event.player.PlayerBlockPlaceEvent;
import net.minestom.server.event.player.PlayerMoveEvent;
import net.minestom.server.event.player.PlayerSpawnEvent;
import org.qiuhua.troveserver.player.RPGPlayer;
import org.qiuhua.troveserver.module.login.event.LoginSuccessfulEvent;
import org.qiuhua.troveserver.module.login.LoginManager;
import org.qiuhua.troveserver.module.playermode.PlayerMode;

public class LoginListener{

    public LoginListener(){
        MinecraftServer.getGlobalEventHandler().addListener(PlayerSpawnEvent.class, this::onPlayerSpawnEvent);
        MinecraftServer.getGlobalEventHandler().addListener(PlayerMoveEvent.class, this::onPlayerMoveEvent);
        MinecraftServer.getGlobalEventHandler().addListener(PlayerBlockPlaceEvent.class, this::onPlayerBlockPlaceEvent);
        MinecraftServer.getGlobalEventHandler().addListener(PlayerBlockBreakEvent.class, this::onPlayerBlockBreakEvent);
    }

    /**
     * 玩家第一次生成时要唤起登入功能
     * 这里暂时直接call登入成功
     * @param event
     */
    public void onPlayerSpawnEvent(PlayerSpawnEvent event){
        RPGPlayer rpgPlayer = (RPGPlayer) event.getPlayer();
        if(event.isFirstSpawn()){
//            rpgPlayer.setIsLogin(true);
//            MinecraftServer.getGlobalEventHandler().call(new LoginSuccessfulEvent(rpgPlayer));
        }
    }

    /**
     * 没登入不能移动
     * @param event
     */
    public void onPlayerMoveEvent(PlayerMoveEvent event){
        RPGPlayer rpgPlayer = (RPGPlayer) event.getPlayer();
        if(!rpgPlayer.getIsLogin()){
            event.setCancelled(true);
        }
    }

    /**
     * 没登入不能放置方块
     * @param event
     */
    public void onPlayerBlockPlaceEvent(PlayerBlockPlaceEvent event){
        RPGPlayer rpgPlayer = (RPGPlayer) event.getPlayer();
        if(!rpgPlayer.getIsLogin()){
            event.setCancelled(true);
        }
    }

    /**
     * 没登入不能破坏方块
     * @param event
     */
    public void onPlayerBlockBreakEvent(PlayerBlockBreakEvent event){
        RPGPlayer rpgPlayer = (RPGPlayer) event.getPlayer();
        if(!rpgPlayer.getIsLogin()){
            event.setCancelled(true);
        }
    }

}
