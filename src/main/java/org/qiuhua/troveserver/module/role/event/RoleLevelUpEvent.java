package org.qiuhua.troveserver.module.role.event;

import lombok.Getter;
import lombok.Setter;
import net.minestom.server.event.trait.CancellableEvent;
import net.minestom.server.event.trait.PlayerEvent;
import org.qiuhua.troveserver.module.role.RoleData;
import org.qiuhua.troveserver.player.RPGPlayer;
import org.qiuhua.troveserver.module.playermode.PlayerMode;

public class RoleLevelUpEvent implements PlayerEvent, CancellableEvent {

    @Getter
    private final RPGPlayer player;

    @Setter
    private boolean cancelled = false;


    @Override
    public boolean isCancelled() {
        return cancelled;
    }

    @Setter
    @Getter
    private PlayerMode playerMode;

    @Getter
    private final RoleData roleData;

    @Getter
    private final Integer oldLevel;

    @Getter
    private final Integer newLevel;

    public RoleLevelUpEvent(RPGPlayer rpgPlayer, RoleData roleData, int oldLevel, int newLevel){
        this.player = rpgPlayer;
        this.roleData = roleData;
        this.oldLevel = oldLevel;
        this.newLevel = newLevel;
    }

}
