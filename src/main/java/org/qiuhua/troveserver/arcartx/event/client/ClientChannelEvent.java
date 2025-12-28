package org.qiuhua.troveserver.arcartx.event.client;

import lombok.Getter;
import net.minestom.server.entity.Player;
import net.minestom.server.event.trait.PlayerEvent;

/**
 * 客户端通道事件
 */
public class ClientChannelEvent implements PlayerEvent {

    @Getter
    private final Player player;

    public ClientChannelEvent(Player player){
        this.player = player;
    }

}
