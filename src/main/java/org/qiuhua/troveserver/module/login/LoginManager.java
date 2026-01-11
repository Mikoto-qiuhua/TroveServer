package org.qiuhua.troveserver.module.login;


import net.minestom.server.MinecraftServer;
import org.qiuhua.troveserver.module.space.ItemSpaceManager;
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



    public static void init(){
        new LoginListener();
        //临时在这里实现登入成功后该干的事情
        MinecraftServer.getGlobalEventHandler().addListener(LoginSuccessfulEvent.class, event -> {
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
                    //给玩家一个测试仓库
                    ItemSpaceManager.createItemSpace(rpgPlayer, "示例仓库");
                });


            });
        });

    }



}
