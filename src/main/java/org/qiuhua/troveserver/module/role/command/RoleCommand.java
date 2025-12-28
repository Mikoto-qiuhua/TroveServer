package org.qiuhua.troveserver.module.role.command;


import net.minestom.server.MinecraftServer;
import net.minestom.server.command.builder.Command;
import net.minestom.server.command.builder.arguments.ArgumentString;
import net.minestom.server.command.builder.arguments.ArgumentType;
import net.minestom.server.command.builder.arguments.number.ArgumentInteger;
import net.minestom.server.command.builder.suggestion.SuggestionEntry;
import org.qiuhua.troveserver.Main;
import org.qiuhua.troveserver.api.command.AbstractCommand;
import org.qiuhua.troveserver.module.role.ui.RoleMainUi;
import org.qiuhua.troveserver.player.RPGPlayer;
import org.qiuhua.troveserver.module.role.RoleData;

import java.util.Map;

public class RoleCommand extends AbstractCommand {


    private static final Map<String, String> cmdInfoMap = Map.of(
            "open <玩家名称>", "打开自身/指定玩家角色界面",
            "exp [角色名称] [经验值] <玩家名称>", "给指定玩家角色添加经验"
    );

    public RoleCommand() {
        super("RoleSystem", "role");
        addSubcommand(new Open());
        addSubcommand(new Exp());
    }


    private static class Exp extends Command{
        public Exp(){
            super("exp");
            ArgumentString roleId = ArgumentType.String("roleId");
            ArgumentInteger exp = ArgumentType.Integer("expAmount");
            ArgumentString playerName = ArgumentType.String("player");

            playerName.setSuggestionCallback((commandSender, commandContext, suggestion) -> {
                MinecraftServer.getConnectionManager().getOnlinePlayers().forEach(player -> {
                    suggestion.addEntry(new SuggestionEntry(player.getUsername()));
                });
            });


            addSyntax((sender, context) -> {
                if(sender instanceof RPGPlayer rpgPlayer){
                    Main.getLogger().info("{} 执行指令 /{}", rpgPlayer.getUsername(), context.getInput());
                    String a = context.get(roleId);
                    Integer b = context.get(exp);
                    String c = context.get(playerName);
                    RoleData roleData = null;
                    if(c.isEmpty()){
                        roleData = rpgPlayer.getRoleDataMap().get(a);
                    }else {
                        RPGPlayer rpgPlayer1 = (RPGPlayer) MinecraftServer.getConnectionManager().getOnlinePlayerByUsername(c);
                        if(rpgPlayer1 != null){
                            roleData = rpgPlayer1.getRoleDataMap().get(a);
                        }
                    }
                    if(roleData != null){
                        roleData.addExp(b);
                    }
                }
            }, roleId, exp, playerName);
        }


    }




    private static class Open extends Command {
        public Open() {
            super("open");
            setDefaultExecutor((sender, context) -> {
                if(sender instanceof RPGPlayer rpgPlayer){
                    RoleMainUi.open(rpgPlayer);
                    Main.getLogger().info("{} 执行指令 /{}", rpgPlayer.getUsername(), context.getInput());
                }
            });
            ArgumentString playerName = ArgumentType.String("player");
            playerName.setSuggestionCallback((commandSender1, commandContext1, suggestion) -> {
                MinecraftServer.getConnectionManager().getOnlinePlayers().forEach(player -> {
                    suggestion.addEntry(new SuggestionEntry(player.getUsername()));
                });
            });
            addSyntax((sender, context) -> {
                if(sender instanceof RPGPlayer rpgPlayer){
                    String string = context.get(playerName);
                    RPGPlayer rpgPlayer1 = (RPGPlayer) MinecraftServer.getConnectionManager().getOnlinePlayerByUsername(string);
                    if(rpgPlayer1 != null){
                        RoleMainUi.open(rpgPlayer, rpgPlayer1);
                        Main.getLogger().info("{} 执行指令 /{}", rpgPlayer.getUsername(), context.getInput());
                    }
                }
            },playerName);
        }
    }




    @Override
    public Map<String, String> getCmdInfoMap() {
        return cmdInfoMap;
    }
}
