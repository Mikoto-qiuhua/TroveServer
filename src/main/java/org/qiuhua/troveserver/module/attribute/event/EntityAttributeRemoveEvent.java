package org.qiuhua.troveserver.module.attribute.event;

import lombok.Getter;
import lombok.Setter;
import net.minestom.server.entity.Entity;
import net.minestom.server.event.trait.CancellableEvent;
import net.minestom.server.event.trait.EntityEvent;

public class EntityAttributeRemoveEvent implements EntityEvent, CancellableEvent {


    @Getter
    private final Entity entity;

    @Setter
    private boolean cancelled = false;

    @Setter
    @Getter
    private String source;

    public EntityAttributeRemoveEvent(Entity entity, String source){
        this.entity = entity;
        this.source = source;
    }


    @Override
    public boolean isCancelled() {
        return cancelled;
    }
}
