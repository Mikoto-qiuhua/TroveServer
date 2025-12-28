package org.qiuhua.troveserver.module.login.event;

import lombok.Getter;
import lombok.Setter;
import net.minestom.server.event.trait.CancellableEvent;
import net.minestom.server.event.trait.PlayerEvent;
import org.qiuhua.troveserver.player.RPGPlayer;

public class LoginSuccessfulEvent implements PlayerEvent {

    @Getter
    private final RPGPlayer player;

    /**
     * 登入成功事件
     * @param rpgPlayer
     */
    public LoginSuccessfulEvent(RPGPlayer rpgPlayer){
        this.player = rpgPlayer;
    }



}
