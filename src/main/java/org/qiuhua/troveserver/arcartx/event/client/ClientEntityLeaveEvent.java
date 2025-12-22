package org.qiuhua.troveserver.arcartx.event.client;

import lombok.Getter;
import net.minestom.server.entity.Player;
import net.minestom.server.event.trait.PlayerEvent;

import java.util.UUID;

public class ClientEntityLeaveEvent implements PlayerEvent {

    @Getter
    private final Player player;

    @Getter
    private final UUID entityUUID;

    public ClientEntityLeaveEvent(Player player, UUID entityUUID) {
        this.player = player;
        this.entityUUID = entityUUID;
    }



}