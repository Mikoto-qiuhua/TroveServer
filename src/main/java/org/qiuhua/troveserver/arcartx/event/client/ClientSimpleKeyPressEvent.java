package org.qiuhua.troveserver.arcartx.event.client;

import lombok.Getter;
import lombok.Setter;
import net.minestom.server.entity.Player;
import net.minestom.server.event.trait.CancellableEvent;
import net.minestom.server.event.trait.PlayerEvent;

public class ClientSimpleKeyPressEvent implements PlayerEvent , CancellableEvent {

    @Getter
    private final Player player;

    @Setter
    private boolean cancelled = false;

    @Override
    public boolean isCancelled() {
        return cancelled;
    }

    @Getter
    private final String keyName;

    public ClientSimpleKeyPressEvent(Player player, String keyName){
        this.player = player;
        this.keyName = keyName;
    }
}
