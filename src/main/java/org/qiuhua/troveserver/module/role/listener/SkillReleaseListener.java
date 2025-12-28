package org.qiuhua.troveserver.module.role.listener;


import org.qiuhua.troveserver.Main;
import org.qiuhua.troveserver.arcartx.event.client.ClientMouseClickEvent;
import org.qiuhua.troveserver.arcartx.event.client.ClientSimpleKeyPressEvent;
import org.qiuhua.troveserver.module.role.RoleData;
import org.qiuhua.troveserver.module.role.RoleManager;
import org.qiuhua.troveserver.player.RPGPlayer;

public class SkillReleaseListener {


    public SkillReleaseListener(){
        RoleManager.roleNode.addListener(ClientSimpleKeyPressEvent.class, this::onClientSimpleKeyPressEvent);
        RoleManager.roleNode.addListener(ClientMouseClickEvent.class, this::onClientMouseClickEvent);
    }


    /**
     * 尝试触发对应触发器的技能
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

    /**
     * 鼠标点击 尝试触发左键技能
     * @param event
     */
    public void onClientMouseClickEvent(ClientMouseClickEvent event){
        if(event.getPlayer() instanceof RPGPlayer rpgPlayer){
            RoleData roleData = rpgPlayer.getUseRoleData();
            if(roleData == null) return;
            if(event.getButton() == 0 ){
                if(event.getAction() == 0){
                    roleData.castSkill("左键按下");
                }else {
                    roleData.castSkill("左键弹起");
                }
            }else {
                if(event.getAction() == 0){
                    roleData.castSkill("右键按下");
                }else {
                    roleData.castSkill("右键弹起");
                }

            }

        }

    }



}
