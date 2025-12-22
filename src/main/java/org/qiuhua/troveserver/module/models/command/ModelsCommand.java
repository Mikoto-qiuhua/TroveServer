package org.qiuhua.troveserver.module.models.command;

import net.minestom.server.command.builder.Command;
import net.minestom.server.command.builder.arguments.ArgumentString;
import net.minestom.server.command.builder.arguments.ArgumentType;
import net.minestom.server.command.builder.suggestion.SuggestionEntry;
import org.qiuhua.troveserver.Main;
import org.qiuhua.troveserver.api.command.AbstractCommand;
import org.qiuhua.troveserver.module.models.ModelsManager;
import org.qiuhua.troveserver.module.models.config.ModelFileConfig;
import org.qiuhua.troveserver.player.RPGPlayer;

import java.util.Map;

public class ModelsCommand extends AbstractCommand {

    private static final Map<String, String> cmdInfoMap = Map.of(
            "spawn [模型ID]", "在自身位置生成一个模型"
    );


    public ModelsCommand(){
        super("ModelsSystem", "models");
        addSubcommand(new Spawn());
    }


    private static class Spawn extends Command{
        public Spawn(){
            super("spawn");
            ArgumentString modelsId = ArgumentType.String("modelsId");
            modelsId.setSuggestionCallback((commandSender, commandContext, suggestion) -> {
                ModelFileConfig.modelNameList.forEach(key -> {
                    suggestion.addEntry(new SuggestionEntry(key));
                });
            });

            addSyntax((sender, context) -> {
                if(sender instanceof RPGPlayer rpgPlayer){
                    Main.getLogger().info("{} 执行指令 /{}", rpgPlayer.getUsername(), context.getInput());
                    String string = context.get(modelsId);
                    ModelsManager.spawnModel(string, rpgPlayer.getInstance(), rpgPlayer.getPosition());
                }
            }, modelsId);
        }
    }





    @Override
    public Map<String, String> getCmdInfoMap() {
        return cmdInfoMap;
    }
}
