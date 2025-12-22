package org.qiuhua.troveserver.skill;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import net.minestom.server.coordinate.Pos;
import net.minestom.server.entity.Entity;
import org.qiuhua.troveserver.api.meta.IMeta;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@ToString
public class SkillMetadata implements Cloneable, IMeta {

    /**
     * 技能的施法者
     */
    @Getter
    private final Entity caster;

    /**
     * 技能的触发者
     */
    @Setter
    @Getter
    private Entity trigger;

    /**
     * 技能的目标列表
     */
    @Getter
    @Setter
    private Collection<Entity> targets;

    /**
     * 技能的释放原点
     */
    @Setter
    @Getter
    private Pos origin;

    /**
     * 技能的数据列表
     */
    @Getter
    private final Map<String, Object> meta = new HashMap<>();

    public SkillMetadata(Entity caster, Entity trigger, Collection<Entity> targets){
        this.caster = caster;
        this.targets = targets;
        this.trigger = trigger;
    }

    public SkillMetadata(Entity caster, Collection<Entity> targets){
        this.caster = caster;
        this.targets = targets;
        this.trigger = caster;
    }

    public SkillMetadata(Entity caster, Entity trigger, Pos origin) {
        this.caster = caster;
        this.origin = origin;
        this.trigger = trigger;
    }

    public SkillMetadata(Entity caster, Pos origin){
        this.caster = caster;
        this.origin = origin;
        this.trigger = caster;
    }


    public SkillMetadata clone() throws CloneNotSupportedException {
        return (SkillMetadata) super.clone();
    }



}
