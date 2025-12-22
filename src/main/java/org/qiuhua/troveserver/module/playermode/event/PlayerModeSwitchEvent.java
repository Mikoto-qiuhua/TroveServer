package org.qiuhua.troveserver.module.playermode.event;

import lombok.Getter;
import lombok.Setter;
import net.minestom.server.event.trait.CancellableEvent;
import net.minestom.server.event.trait.PlayerEvent;
import org.qiuhua.troveserver.player.RPGPlayer;
import org.qiuhua.troveserver.module.playermode.PlayerMode;

public class PlayerModeSwitchEvent implements PlayerEvent, CancellableEvent {


    @Getter
    private final RPGPlayer player;

    @Setter
    private boolean cancelled = false;

    @Setter
    @Getter
    private PlayerMode playerMode;

    @Override
    public boolean isCancelled() {
        return cancelled;
    }

    /**
     * 玩家模式切换事件
     * @param player
     */
    public PlayerModeSwitchEvent(RPGPlayer player, PlayerMode playerMode){
        this.player = player;
        this.playerMode = playerMode;
    }

}
