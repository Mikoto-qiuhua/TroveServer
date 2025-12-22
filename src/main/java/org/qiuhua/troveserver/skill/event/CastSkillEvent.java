package org.qiuhua.troveserver.skill.event;

import lombok.Getter;
import lombok.Setter;
import net.minestom.server.entity.Entity;
import net.minestom.server.event.trait.CancellableEvent;
import net.minestom.server.event.trait.EntityEvent;
import org.qiuhua.troveserver.api.skill.AbstractSkillMechanic;
import org.qiuhua.troveserver.skill.SkillMetadata;

public class CastSkillEvent implements EntityEvent, CancellableEvent {

    @Getter
    private final Entity entity;

    @Setter
    private boolean cancelled = false;

    @Override
    public boolean isCancelled() {
        return cancelled;
    }

    @Getter
    public AbstractSkillMechanic abstractSkillMechanic;

    @Setter
    @Getter
    public SkillMetadata skillMetadata;

    public CastSkillEvent(AbstractSkillMechanic abstractSkillMechanic, Entity casterEntity, SkillMetadata skillMetadata){
        this.entity = casterEntity;
        this.abstractSkillMechanic = abstractSkillMechanic;
        this.skillMetadata = skillMetadata;
    }


}
