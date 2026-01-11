package org.qiuhua.troveserver.module.space.command;


import net.minestom.server.MinecraftServer;
import net.minestom.server.command.builder.Command;
import net.minestom.server.command.builder.arguments.ArgumentString;
import net.minestom.server.command.builder.arguments.ArgumentType;
import net.minestom.server.command.builder.arguments.number.ArgumentInteger;
import net.minestom.server.command.builder.suggestion.SuggestionEntry;
import org.qiuhua.troveserver.Main;
import org.qiuhua.troveserver.api.command.AbstractCommand;
import org.qiuhua.troveserver.module.role.RoleData;
import org.qiuhua.troveserver.module.role.ui.RoleMainUi;
import org.qiuhua.troveserver.module.space.ui.ItemSpaceMainUi;
import org.qiuhua.troveserver.player.RPGPlayer;

import java.util.Map;

public class ItemSpaceCommand extends AbstractCommand {


    private static final Map<String, String> cmdInfoMap = Map.of(
            "open <玩家名称>", "打开自身/指定玩家空间界面"
    );

    public ItemSpaceCommand() {
        super("ItemSpaceSystem", "space");
        addSubcommand(new Open());
    }


    private static class Open extends Command {
        public Open() {
            super("open");
            setDefaultExecutor((sender, context) -> {
                if(sender instanceof RPGPlayer rpgPlayer){
                    ItemSpaceMainUi.open(rpgPlayer);
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
                        ItemSpaceMainUi.open(rpgPlayer, rpgPlayer1);
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
