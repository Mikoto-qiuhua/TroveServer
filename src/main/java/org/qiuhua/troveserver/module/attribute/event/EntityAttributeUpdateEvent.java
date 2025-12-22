package org.qiuhua.troveserver.module.attribute.event;

import lombok.Getter;
import lombok.Setter;
import net.minestom.server.entity.Entity;
import net.minestom.server.event.trait.CancellableEvent;
import net.minestom.server.event.trait.EntityEvent;
import org.qiuhua.troveserver.module.attribute.EntityAttributesData;

public class EntityAttributeUpdateEvent implements EntityEvent, CancellableEvent {


    @Getter
    private final Entity entity;

    @Setter
    private boolean cancelled = false;

    @Getter
    private final EntityAttributesData entityAttributesData;

    public EntityAttributeUpdateEvent(Entity entity, EntityAttributesData entityAttributesData){
        this.entity = entity;
        this.entityAttributesData = entityAttributesData;
    }


    @Override
    public boolean isCancelled() {
        return cancelled;
    }
}
