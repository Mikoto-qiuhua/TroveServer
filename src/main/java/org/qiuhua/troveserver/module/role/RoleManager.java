package org.qiuhua.troveserver.module.role;

import net.minestom.server.MinecraftServer;
import net.minestom.server.entity.Entity;
import net.minestom.server.event.Event;
import net.minestom.server.event.EventNode;
import org.qiuhua.troveserver.api.skill.AbstractSkillMechanic;
import org.qiuhua.troveserver.config.ConfigManager;
import org.qiuhua.troveserver.module.role.listener.RoleArmsListener;
import org.qiuhua.troveserver.module.role.ui.RoleMainUi;
import org.qiuhua.troveserver.skill.SkillMetadata;
import org.qiuhua.troveserver.skill.mechanics.warrior.Warrior_Skill_1;
import org.qiuhua.troveserver.skill.mechanics.warrior.Warrior_Skill_2;
import org.qiuhua.troveserver.skill.mechanics.warrior.Warrior_Skill_Main;
import org.qiuhua.troveserver.module.role.command.RoleCommand;
import org.qiuhua.troveserver.module.role.config.RoleConfig;
import org.qiuhua.troveserver.module.role.config.RoleFileConfig;
import org.qiuhua.troveserver.module.role.config.SkillFileConfig;
import org.qiuhua.troveserver.module.role.listener.JumpSprintListener;
import org.qiuhua.troveserver.skill.SkillConfigData;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

public class RoleManager {



    /**
     * 角色的节点
     */
    public static final EventNode<Event> roleNode = EventNode.all("Role_Node");


    /**
     * 技能合集 这里都是可以直接释放的技能
     */
    public static final Map<String, SkillConfigData> skillMechanicMap = new HashMap<>();

    /**
     * 注册的技能对象
     */
    public static final Map<String, Supplier<AbstractSkillMechanic>> SKILL_CLASS = Map.of(
            "warrior_skill_main", Warrior_Skill_Main::new,
            "warrior_skill_2", Warrior_Skill_2::new,
            "warrior_skill_1", Warrior_Skill_1::new

    );


    public static void init(){
        //将节点添加进全局事件内
        MinecraftServer.getGlobalEventHandler().addChild(roleNode.setPriority(1));
        ConfigManager.loadConfig("role", "config", new RoleConfig());
        ConfigManager.loadConfig("role", "roles", new RoleFileConfig());
        ConfigManager.loadConfig("role", "skills", new SkillFileConfig());
        //这个是界面的配置
        ConfigManager.loadConfig("role", "ui", new RoleMainUi());

        new JumpSprintListener();
        new RoleArmsListener();
        new RoleCommand();

    }


    /**
     * 指定实体释放一个技能
     * @param entity 施法者
     * @param skillName 技能名称
     */
    public static void castSkill(Entity entity, String skillName){
        SkillConfigData skillConfigData = skillMechanicMap.get(skillName);
        if(skillConfigData != null && skillConfigData.getSkillMechanic() != null){
            SkillMetadata skillMetadata = new SkillMetadata(entity, entity.getPosition());
            skillConfigData.getSkillMechanic().execute(skillMetadata, entity);
        }
    }

    /**
     * 指定实体释放一个技能
     * @param entity 施法者
     * @param skillName 技能名称
     * @param meta 元数据
     */
    public static void castSkill(Entity entity, String skillName, Map<String, Object> meta){
        SkillConfigData skillConfigData = skillMechanicMap.get(skillName);
        if(skillConfigData != null && skillConfigData.getSkillMechanic() != null){
            SkillMetadata skillMetadata = new SkillMetadata(entity, entity.getPosition());
            skillMetadata.getMeta().putAll(meta);
            skillConfigData.getSkillMechanic().execute(skillMetadata, entity);
        }
    }











}
