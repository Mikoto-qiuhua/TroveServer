package org.qiuhua.troveserver.module.role.listener;


import org.qiuhua.troveserver.arcartx.event.client.ClientSimpleKeyPressEvent;
import org.qiuhua.troveserver.module.role.RoleData;
import org.qiuhua.troveserver.module.role.RoleManager;
import org.qiuhua.troveserver.player.RPGPlayer;

public class SkillReleaseListener {


    public SkillReleaseListener(){
        RoleManager.roleNode.addListener(ClientSimpleKeyPressEvent.class, this::onClientSimpleKeyPressEvent);
    }


    /**
     * 战斗模式下才处理指定的技能
     * 并且尝试触发对应触发器的技能
     * @param event
     */
    public void onClientSimpleKeyPressEvent(ClientSimpleKeyPressEvent event){
        if(event.getPlayer() instanceof RPGPlayer rpgPlayer){
            String keyName = event.getKeyName();
            RoleData roleData = rpgPlayer.getUseRoleData();
            if(roleData == null) return;
            roleData.castSkill(keyName);
        }
    }

}
