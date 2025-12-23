package org.qiuhua.troveserver.module.mob;

import net.minestom.server.MinecraftServer;
import net.minestom.server.coordinate.Pos;
import net.minestom.server.event.EventFilter;
import net.minestom.server.event.EventNode;
import net.minestom.server.event.trait.EntityEvent;
import net.minestom.server.instance.Instance;
import org.qiuhua.troveserver.Main;
import org.qiuhua.troveserver.api.entity.IMobCreature;
import org.qiuhua.troveserver.config.ConfigManager;
import org.qiuhua.troveserver.entity.DamageDummyEntity;
import org.qiuhua.troveserver.module.mob.command.MobCommand;
import org.qiuhua.troveserver.module.mob.config.MobFileConfig;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

public class MobManager {

    /**
     * 全部实体的配置
     */
    public final static Map<String, MobConfig> allMobConfig = new HashMap<>();

    /**
     * 注册的实体对象
     */
    public static final Map<String, Supplier<IMobCreature>> MOB_CLASS = Map.of(
            "DamageDummyEntity", DamageDummyEntity::new
    );

    /**
     * mob事件所使用的节点
     */
    public static final EventNode<EntityEvent> mobNode = EventNode.type("Mob_Node", EventFilter.ENTITY);


    public static void init(){
        //将节点添加进全局事件内
        MinecraftServer.getGlobalEventHandler().addChild(mobNode.setPriority(1));
        ConfigManager.loadConfig("mob", "mobs", new MobFileConfig());
        new MobCommand();
    }

    /**
     * 生成一个mob
     * @param mobId
     * @param settingsId
     * @param instance
     * @param pos
     */
    public static void spawnEntity(String mobId, String settingsId, Instance instance, Pos pos){
        MobConfig mobConfig = allMobConfig.get(mobId);
        if(mobConfig == null){
            Main.getLogger().warn("无效的Mob名称 {}", mobId);
            return;
        }
        Supplier<IMobCreature> supplier = MOB_CLASS.get(mobConfig.getMobClassId());
        IMobCreature iMobCreature = supplier.get();
        iMobCreature.spawnEntity(mobConfig, settingsId, instance, pos);
        Main.getLogger().debug("生成Mob {}", mobId);

    }



}
