package org.qiuhua.troveserver.module.role.config;

import org.qiuhua.troveserver.Main;
import org.qiuhua.troveserver.api.config.IConfig;
import org.qiuhua.troveserver.api.skill.AbstractSkillMechanic;
import org.qiuhua.troveserver.module.role.RoleManager;
import org.qiuhua.troveserver.module.role.RoleSkillConfigData;
import org.qiuhua.troveserver.utils.FileUtils;
import org.qiuhua.troveserver.utils.yaml.file.YamlConfiguration;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

public class SkillFileConfig implements IConfig {

    public final static Map<String, YamlConfiguration> allSkillConfig = new HashMap<>();

    @Override
    public void reload() {
        load();
    }

    @Override
    public void load() {
        allSkillConfig.clear();
        //如果文件夹不在 才生成
        if (!(new File(FileUtils.getDataFolder() , "role/skills").exists())){
            FileUtils.saveResource("role/skills/warrior/warrior_skill_main.yml", false);
        }
        Map<String, YamlConfiguration> map = FileUtils.loadFiles(new File(FileUtils.getDataFolder(), "role/skills"));
        allSkillConfig.putAll(map);
        Main.getLogger().info("加载Skill配置文件 {} 个", map.size());
        loadSkill();
    }


    private void loadSkill(){
        RoleManager.skillMechanicMap.clear();
        allSkillConfig.forEach((key, config) -> {
            if(RoleManager.skillMechanicMap.containsKey(key)){
                Main.getLogger().warn("出现重名技能配置 {} 请注意修改,本次将越过该配置读取", key);
                return;
            }
            String className = config.getString("SkillClassId", "");
            AbstractSkillMechanic abstractSkillMechanic = null;
            Supplier<AbstractSkillMechanic> supplier = RoleManager.SKILL_CLASS.get(className);
            if(supplier == null){
                Main.getLogger().warn("技能 {} 无效的SkillClassId {}", key, className);
            }else {
                abstractSkillMechanic = supplier.get();
            }
            RoleSkillConfigData roleSkillConfigData = new RoleSkillConfigData(key, config, abstractSkillMechanic);
            RoleManager.skillMechanicMap.put(key, roleSkillConfigData);
        });
        Main.getLogger().info("加载技能 {} 个",  RoleManager.skillMechanicMap.size());


    }

}
