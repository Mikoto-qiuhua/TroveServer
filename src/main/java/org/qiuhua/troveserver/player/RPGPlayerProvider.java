package org.qiuhua.troveserver.player;

import net.minestom.server.entity.Player;
import net.minestom.server.network.PlayerProvider;
import net.minestom.server.network.player.GameProfile;
import net.minestom.server.network.player.PlayerConnection;

public class RPGPlayerProvider implements PlayerProvider {

    @Override
    public Player createPlayer(PlayerConnection playerConnection, GameProfile gameProfile) {
        return new RPGPlayer(playerConnection, gameProfile);
    }
}
