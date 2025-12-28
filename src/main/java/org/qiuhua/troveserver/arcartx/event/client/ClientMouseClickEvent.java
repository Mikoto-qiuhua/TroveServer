package org.qiuhua.troveserver.arcartx.event.client;

import lombok.Getter;
import lombok.Setter;
import net.minestom.server.entity.Player;
import net.minestom.server.event.trait.CancellableEvent;
import net.minestom.server.event.trait.PlayerEvent;

public class ClientMouseClickEvent implements PlayerEvent, CancellableEvent {

    @Getter
    private final Player player;

    @Setter
    private boolean cancelled = false;

    @Override
    public boolean isCancelled() {
        return cancelled;
    }

    @Getter
    private final int button;

    @Getter
    private final int action;

    /**
     *
     * @param player
     * @param button 0=左键 1=右键
     * @param action 0=按下 1=弹起
     */
    public ClientMouseClickEvent( Player player, int button, int action) {
        this.player = player;
        this.button = button;
        this.action = action;
    }

}
