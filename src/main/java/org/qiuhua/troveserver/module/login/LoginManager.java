package org.qiuhua.troveserver.module.login;


import net.minestom.server.MinecraftServer;
import net.minestom.server.event.EventFilter;
import net.minestom.server.event.EventNode;
import net.minestom.server.event.trait.PlayerEvent;
import org.qiuhua.troveserver.module.playermode.PlayerMode;
import org.qiuhua.troveserver.module.role.RoleUnlockedState;
import org.qiuhua.troveserver.player.RPGPlayer;
import org.qiuhua.troveserver.module.attribute.AttributeCompileGroup;
import org.qiuhua.troveserver.module.attribute.AttributeManager;
import org.qiuhua.troveserver.module.attribute.config.AttributeConfig;
import org.qiuhua.troveserver.module.login.event.LoginSuccessfulEvent;
import org.qiuhua.troveserver.module.login.listener.LoginListener;
import org.qiuhua.troveserver.module.role.RoleData;
import org.qiuhua.troveserver.module.role.config.RoleFileConfig;
import org.qiuhua.troveserver.utils.task.SchedulerManager;

public class LoginManager {


    /**
     * 登入事件所使用的节点
     */
    public static final EventNode<PlayerEvent> loginNode = EventNode.type("Login_Node", EventFilter.PLAYER);


    public static void init(){
        //将节点添加进全局事件内
        MinecraftServer.getGlobalEventHandler().addChild(loginNode.setPriority(0));
        new LoginListener();
        //临时在这里实现登入成功后该干的事情
        loginNode.addListener(LoginSuccessfulEvent.class, event -> {
            RPGPlayer rpgPlayer = event.getPlayer();
            rpgPlayer.scheduler().scheduleNextTick(()->{
                AttributeCompileGroup attributeCompileGroup = AttributeManager.getStringAttributeGroup(AttributeConfig.base_attribute_player);
                rpgPlayer.addAttribute(attributeCompileGroup, "base_attribute_player");
                rpgPlayer.updateAttribute();
                rpgPlayer.sendMessage("登入成功");
                rpgPlayer.setFood(0);
                SchedulerManager.submitAsync(()->{
                    //这里加载角色
                    RoleFileConfig.allRoleConfig.forEach((roleId, config) -> {
                        rpgPlayer.getRoleDataMap().put(roleId, new RoleData(roleId, config, rpgPlayer));
                    });
                    rpgPlayer.getRoleDataMap().get("战士").setRoleUnlockedState(RoleUnlockedState.Unlocked);
                    rpgPlayer.setPlayerMode(PlayerMode.Battle);
                    rpgPlayer.switchRole("战士");
                });



            });
        });

    }



}
