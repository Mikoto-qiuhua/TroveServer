package org.qiuhua.troveserver.module.role.event;

import lombok.Getter;
import lombok.Setter;
import net.minestom.server.event.trait.CancellableEvent;
import net.minestom.server.event.trait.PlayerEvent;
import net.minestom.server.item.ItemStack;
import org.qiuhua.troveserver.player.RPGPlayer;
import org.qiuhua.troveserver.module.role.EquipSlotData;
import org.qiuhua.troveserver.module.role.RoleData;

public class RolePutEquipEvent implements PlayerEvent, CancellableEvent {

    @Getter
    private final RPGPlayer player;

    @Setter
    private boolean cancelled = false;

    @Override
    public boolean isCancelled() {
        return cancelled;
    }

    @Getter
    private final RoleData roleData;

    @Getter
    private final EquipSlotData equipSlotData;

    @Getter
    @Setter
    private ItemStack itemStack;



    public RolePutEquipEvent(RPGPlayer rpgPlayer, RoleData roleData, EquipSlotData equipSlotData, ItemStack itemStack){
        this.player = rpgPlayer;
        this.roleData = roleData;
        this.equipSlotData = equipSlotData;
        this.itemStack = itemStack;
    }


}
