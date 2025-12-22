package org.qiuhua.troveserver.module.attribute.event;

import lombok.Getter;
import lombok.Setter;
import net.minestom.server.entity.Entity;
import net.minestom.server.event.trait.CancellableEvent;
import net.minestom.server.event.trait.EntityEvent;
import org.qiuhua.troveserver.module.attribute.AttributeCompileGroup;

public class EntityAttributeAddEvent implements EntityEvent, CancellableEvent {


    @Getter
    private final Entity entity;

    @Setter
    private boolean cancelled = false;

    @Getter
    @Setter
    private AttributeCompileGroup attributeCompileGroup;

    @Setter
    @Getter
    private String source;

    public EntityAttributeAddEvent(Entity entity, AttributeCompileGroup attributeCompileGroup, String source){
        this.entity = entity;
        this.attributeCompileGroup = attributeCompileGroup;
        this.source = source;
    }


    @Override
    public boolean isCancelled() {
        return cancelled;
    }
}
