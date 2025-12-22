package org.qiuhua.troveserver.api.entity;

import net.minestom.server.coordinate.Pos;
import net.minestom.server.instance.Instance;
import org.qiuhua.troveserver.module.mob.MobConfig;

public interface IMobCreature {

    void spawnEntity(MobConfig mobConfig, String settingsId, Instance instance, Pos pos);


}
