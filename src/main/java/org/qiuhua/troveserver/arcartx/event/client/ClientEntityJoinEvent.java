package org.qiuhua.troveserver.arcartx.event.client;

import lombok.Getter;
import net.minestom.server.entity.Player;
import net.minestom.server.event.trait.PlayerEvent;
import org.qiuhua.troveserver.player.RPGPlayer;

import java.util.UUID;

public class ClientEntityJoinEvent implements PlayerEvent {

    @Getter
    private final Player player;

    private final UUID entityUUID;

    public ClientEntityJoinEvent(Player player, UUID entityUUID) {
        this.player = player;
        this.entityUUID = entityUUID;
    }



}
