package org.qiuhua.troveserver.module.login.listener;

import net.minestom.server.MinecraftServer;
import net.minestom.server.event.GlobalEventHandler;
import net.minestom.server.event.player.PlayerSpawnEvent;
import org.qiuhua.troveserver.player.RPGPlayer;
import org.qiuhua.troveserver.module.login.event.LoginSuccessfulEvent;
import org.qiuhua.troveserver.module.login.LoginManager;
import org.qiuhua.troveserver.module.playermode.PlayerMode;

public class LoginListener{

    public LoginListener(){
        GlobalEventHandler globalEventHandler = MinecraftServer.getGlobalEventHandler();
        globalEventHandler.addListener(PlayerSpawnEvent.class, this::onPlayerSpawnEvent);

    }

    /**
     * 玩家第一次生成时要唤起登入功能
     * 这里暂时直接call登入成功
     * @param event
     */
    public void onPlayerSpawnEvent(PlayerSpawnEvent event){
        RPGPlayer rpgPlayer = (RPGPlayer) event.getPlayer();
        if(event.isFirstSpawn()){
            LoginManager.loginNode.call(new LoginSuccessfulEvent(rpgPlayer));
            rpgPlayer.setIsLogin(true);
        }
    }


    /**
     * 登入成功的事件
     * @param event
     */
    public void onLoginSuccessfulEvent(LoginSuccessfulEvent event){


    }



}
