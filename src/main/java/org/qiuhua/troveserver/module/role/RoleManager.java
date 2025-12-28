package org.qiuhua.troveserver.module.role;

import net.minestom.server.MinecraftServer;
import net.minestom.server.entity.Entity;
import net.minestom.server.entity.LivingEntity;
import net.minestom.server.event.Event;
import net.minestom.server.event.EventFilter;
import net.minestom.server.event.EventNode;
import net.minestom.server.event.trait.PlayerEvent;
import org.qiuhua.troveserver.api.skill.AbstractSkillMechanic;
import org.qiuhua.troveserver.config.ConfigManager;
import org.qiuhua.troveserver.module.playermode.PlayerMode;
import org.qiuhua.troveserver.module.playermode.PlayerModeManager;
import org.qiuhua.troveserver.module.role.listener.SkillReleaseListener;
import org.qiuhua.troveserver.module.role.ui.RoleMainUi;
import org.qiuhua.troveserver.player.RPGPlayer;
import org.qiuhua.troveserver.skill.SkillMetadata;
import org.qiuhua.troveserver.skill.mechanics.warrior.Warrior_Skill_1;
import org.qiuhua.troveserver.skill.mechanics.warrior.Warrior_Skill_2;
import org.qiuhua.troveserver.skill.mechanics.warrior.Warrior_Skill_Main;
import org.qiuhua.troveserver.module.role.command.RoleCommand;
import org.qiuhua.troveserver.module.role.config.RoleConfig;
import org.qiuhua.troveserver.module.role.config.RoleFileConfig;
import org.qiuhua.troveserver.module.role.config.SkillFileConfig;
import org.qiuhua.troveserver.module.role.listener.JumpSprintListener;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

public class RoleManager {



    /**
     * 角色的节点
     * 必须是战斗模式才生效
     */
    public static final EventNode<PlayerEvent> roleNode = EventNode.type("Role_Node", EventFilter.PLAYER, ((playerEvent, player) -> {
        RPGPlayer rpgPlayer = (RPGPlayer) player;
        return rpgPlayer.getPlayerMode() == PlayerMode.Battle;
    }));


    /**
     * 技能合集 这里都是可以直接释放的技能
     */
    public static final Map<String, RoleSkillConfigData> skillMechanicMap = new HashMap<>();

    /**
     * 注册的技能对象
     */
    public static final Map<String, Supplier<AbstractSkillMechanic>> SKILL_CLASS = Map.of(
            "warrior_skill_main", Warrior_Skill_Main::new,
            "warrior_skill_2", Warrior_Skill_2::new,
            "warrior_skill_1", Warrior_Skill_1::new

    );


    public static void init(){
        //将节点添加进全局节点
        MinecraftServer.getGlobalEventHandler().addChild(roleNode);
        ConfigManager.loadConfig("role", "config", new RoleConfig());
        ConfigManager.loadConfig("role", "roles", new RoleFileConfig());
        ConfigManager.loadConfig("role", "skills", new SkillFileConfig());
        //这个是界面的配置
        ConfigManager.loadConfig("role", "ui", new RoleMainUi());

        new JumpSprintListener();
        new SkillReleaseListener();
        new RoleCommand();

    }


    /**
     * 指定实体释放一个技能
     * @param entity 施法者
     * @param skillName 技能名称
     */
    public static void castSkill(LivingEntity entity, String skillName){
        RoleSkillConfigData roleSkillConfigData = skillMechanicMap.get(skillName);
        if(roleSkillConfigData != null && roleSkillConfigData.getSkillMechanic() != null){
            SkillMetadata skillMetadata = new SkillMetadata(entity, entity.getPosition());
            roleSkillConfigData.getSkillMechanic().execute(skillMetadata, entity);
        }
    }

    /**
     * 指定实体释放一个技能
     * @param entity 施法者
     * @param skillName 技能名称
     * @param meta 元数据
     */
    public static void castSkill(LivingEntity entity, String skillName, Map<String, Object> meta){
        RoleSkillConfigData roleSkillConfigData = skillMechanicMap.get(skillName);
        if(roleSkillConfigData != null && roleSkillConfigData.getSkillMechanic() != null){
            SkillMetadata skillMetadata = new SkillMetadata(entity, entity.getPosition());
            skillMetadata.getMeta().putAll(meta);
            roleSkillConfigData.getSkillMechanic().execute(skillMetadata, entity);
        }
    }











}
