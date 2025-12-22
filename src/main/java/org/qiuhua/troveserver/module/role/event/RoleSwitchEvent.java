package org.qiuhua.troveserver.module.role.event;

import lombok.Getter;
import lombok.Setter;
import net.minestom.server.event.trait.PlayerEvent;
import org.qiuhua.troveserver.player.RPGPlayer;
import org.qiuhua.troveserver.module.role.RoleData;

public class RoleSwitchEvent implements PlayerEvent{

    @Getter
    private final RPGPlayer player;


    @Getter
    private RoleData roleData;


    public RoleSwitchEvent(RPGPlayer rpgPlayer, RoleData roleData){
        this.player = rpgPlayer;
        this.roleData = roleData;
    }
}
