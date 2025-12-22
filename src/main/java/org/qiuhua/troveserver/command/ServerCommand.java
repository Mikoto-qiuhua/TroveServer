package org.qiuhua.troveserver.command;

import net.minestom.server.command.builder.Command;
import net.minestom.server.command.builder.arguments.ArgumentEnum;
import net.minestom.server.command.builder.arguments.ArgumentString;
import net.minestom.server.command.builder.arguments.ArgumentType;
import net.minestom.server.command.builder.suggestion.SuggestionEntry;
import net.minestom.server.entity.GameMode;
import net.minestom.server.entity.Player;
import org.qiuhua.troveserver.Main;
import org.qiuhua.troveserver.api.command.AbstractCommand;
import org.qiuhua.troveserver.api.command.SubCommand;
import org.qiuhua.troveserver.config.ConfigManager;
import org.qiuhua.troveserver.player.RPGPlayer;

import java.util.Map;

public class ServerCommand extends AbstractCommand {


    private static final Map<String, String> cmdInfoMap = Map.of(
            "stop", "关闭服务器",
            "reload <配置命名空间:ID>", "重载全部配置/指定配置",
            "gamemode [游戏模式]", "切换当前游戏模式",
            "test", "临时测试代码"
    );




    public ServerCommand() {
        super("TroveServer", "server");
        addSubcommand(new SubCommand("stop", ((commandSender, commandContext) -> {
            Main.getInstance().stop();
        })));

        addSubcommand(new SubCommand("test", ((commandSender, commandContext) -> {
            if(commandSender instanceof RPGPlayer rpgPlayer){

            }
        })));
        addSubcommand(new reload());
        addSubcommand(new GameModeCommand());

    }



        private static class GameModeCommand extends Command {
            public GameModeCommand() {
                super("gamemode");
                ArgumentEnum<GameMode> gamemode = ArgumentType.Enum("mode", GameMode.class).setFormat(ArgumentEnum.Format.LOWER_CASED);
                addSyntax((sender, context) -> {
                    if(sender instanceof RPGPlayer rpgPlayer){
                        Main.getLogger().info("{} 执行指令 /{}", rpgPlayer.getUsername(), context.getInput());
                        GameMode mode = context.get(gamemode);
                        rpgPlayer.setGameMode(mode);
                    }
                },gamemode);
            }
        }


    private static class reload extends Command {
        public reload() {
            super("reload");
            setDefaultExecutor((sender, context) -> {
                if(sender instanceof RPGPlayer rpgPlayer){
                    Main.getLogger().info("{} 执行指令 /{}",rpgPlayer.getUsername(), context.getInput());
                }else {
                    Main.getLogger().info("执行指令 /{}", context.getInput());
                }
                ConfigManager.getAllConfig().values().forEach(config -> {
                    config.reload();
                });
                Main.getLogger().info("重载 {} 个配置完成", ConfigManager.getAllConfig().size());
            });

            //这里创建子命令用于更新单个配置文件
            ArgumentString namespaceId = ArgumentType.String("namespaceId");
            namespaceId.setSuggestionCallback((commandSender, commandContext, suggestion) -> {
                ConfigManager.getAllConfig().keySet().forEach(key -> {
                    suggestion.addEntry(new SuggestionEntry(key));
                });
            });

            addSyntax((sender, context) -> {
                String string = context.get(namespaceId);
                if (sender instanceof Player player){
                    Main.getLogger().info("{} 执行指令 /{}", player.getUsername(), context.getInput());
                }else {
                    Main.getLogger().info("执行指令 /{}", context.getInput());
                }
                if(string.contains(":")){
                    ConfigManager.reloadConfig(string);
                }else {
                    ConfigManager.reloadAllConfig(string);
                }
            },namespaceId);
        }
    }




    @Override
    public Map<String, String> getCmdInfoMap() {
        return cmdInfoMap;
    }

}
