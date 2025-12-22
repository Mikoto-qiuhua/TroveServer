package org.qiuhua.troveserver.module.role;

import lombok.Getter;
import org.qiuhua.troveserver.player.RPGPlayer;
import org.qiuhua.troveserver.utils.script.JavaScript;
import org.qiuhua.troveserver.utils.yaml.ConfigurationSection;

public class RoleLevelExpData {

    /**
     * 这个等级数据的等级
     */
    @Getter
    private final Integer level;

    /**
     * 对应的配置
     */
    @Getter
    private final ConfigurationSection config;

    /**
     * 每级需要的经验
     */
    @Getter
    private final Integer exp;

    /**
     * js条件
     */
    @Getter
    private final String condition;

    private final RoleData roleData;


    public RoleLevelExpData(ConfigurationSection config, Integer level, RoleData roleData){
        this.level = level;
        this.config = config;
        this.exp = config.getInt("exp", 10);
        this.roleData = roleData;
        this.condition = config.getString("condition");
        //进行js条件预编译
        JavaScript.precompile(condition);
    }

    /**
     * 检查升级条件是否满足
     * @param rpgPlayer
     * @return
     */
    public Boolean checkCondition(RPGPlayer rpgPlayer) {
        return (Boolean) JavaScript.evaluate(condition, rpgPlayer);
    }

    /**
     * 执行升级动作
     * @param rpgPlayer
     */
    public void runActions(RPGPlayer rpgPlayer){

    }




}
