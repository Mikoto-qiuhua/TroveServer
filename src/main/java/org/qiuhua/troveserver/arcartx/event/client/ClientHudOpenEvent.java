package org.qiuhua.troveserver.arcartx.event.client;

import lombok.Getter;
import net.minestom.server.entity.Player;
import net.minestom.server.event.trait.PlayerEvent;
import org.qiuhua.troveserver.arcartx.core.ui.adapter.ArcartXUI;

public class ClientHudOpenEvent implements PlayerEvent {


    @Getter
    private final Player player;


    @Getter
    private final ArcartXUI hud;

    public ClientHudOpenEvent(Player player, ArcartXUI hud){
        this.hud = hud;
        this.player = player;
    }


}
