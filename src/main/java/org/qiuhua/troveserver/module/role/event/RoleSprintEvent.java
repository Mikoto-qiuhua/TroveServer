package org.qiuhua.troveserver.module.role.event;

import lombok.Getter;
import lombok.Setter;
import net.minestom.server.event.trait.CancellableEvent;
import net.minestom.server.event.trait.PlayerEvent;
import org.qiuhua.troveserver.player.RPGPlayer;

public class RoleSprintEvent implements PlayerEvent, CancellableEvent {

    @Getter
    private final RPGPlayer player;

    @Setter
    private boolean cancelled = false;


    @Override
    public boolean isCancelled() {
        return cancelled;
    }

    public RoleSprintEvent(RPGPlayer rpgPlayer){
        this.player = rpgPlayer;

    }



}
