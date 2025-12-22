package org.qiuhua.troveserver.arcartx.event.client;

import lombok.Getter;
import net.minestom.server.entity.Player;
import net.minestom.server.event.trait.PlayerEvent;
import org.qiuhua.troveserver.arcartx.core.ui.adapter.ArcartXUI;

public class ClientMenuCloseEvent implements PlayerEvent {


    @Getter
    private final Player player;


    @Getter
    private final ArcartXUI menu;

    public ClientMenuCloseEvent(Player player, ArcartXUI menu){
        this.menu = menu;
        this.player = player;
    }


}
