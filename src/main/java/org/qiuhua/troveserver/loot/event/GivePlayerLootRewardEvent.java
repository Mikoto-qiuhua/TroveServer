package org.qiuhua.troveserver.loot.event;

import lombok.Getter;
import lombok.Setter;
import net.minestom.server.event.trait.CancellableEvent;
import net.minestom.server.event.trait.PlayerEvent;
import org.qiuhua.troveserver.player.RPGPlayer;

public class GivePlayerLootRewardEvent implements PlayerEvent, CancellableEvent {


    @Getter
    private final RPGPlayer player;

    @Setter
    private boolean cancelled = false;

    public boolean isCancelled() { return cancelled; }

    /**
     * 是否跳过检查
     */
    @Getter
    @Setter
    private boolean skipCheck = false;

    public GivePlayerLootRewardEvent(RPGPlayer rpgPlayer){
        this.player = rpgPlayer;
    }
}
