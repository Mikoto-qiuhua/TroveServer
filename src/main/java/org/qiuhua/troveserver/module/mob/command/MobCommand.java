package org.qiuhua.troveserver.module.mob.command;

import net.minestom.server.command.builder.Command;
import net.minestom.server.command.builder.arguments.ArgumentString;
import net.minestom.server.command.builder.arguments.ArgumentType;
import net.minestom.server.command.builder.suggestion.SuggestionEntry;
import net.minestom.server.item.ItemStack;
import org.qiuhua.troveserver.Main;
import org.qiuhua.troveserver.api.command.AbstractCommand;
import org.qiuhua.troveserver.module.item.ItemManager;
import org.qiuhua.troveserver.module.mob.MobManager;
import org.qiuhua.troveserver.player.RPGPlayer;

import java.util.Map;

public class MobCommand extends AbstractCommand {

    private static final Map<String, String> cmdInfoMap = Map.of(
            "spawn [MobId] <SettingsId>", "生成一个指定实体"
    );

    public MobCommand() {
        super("MobSystem", "mob");
        addSubcommand(new Spawn());
    }




    private static class Spawn extends Command{

        public Spawn() {
            super("spawn");
            ArgumentString mobId = ArgumentType.String("mobId");
            ArgumentString settingsId = ArgumentType.String("settingsId");
            settingsId.setDefaultValue("default");
            mobId.setSuggestionCallback((commandSender, commandContext, suggestion) -> {
                MobManager.allMobConfig.keySet().forEach(key->{
                    suggestion.addEntry(new SuggestionEntry(key));
                });
            });
            addSyntax((sender, context) -> {
                if (sender instanceof RPGPlayer rpgPlayer){
                    Main.getLogger().info("{} 执行指令 /{}", rpgPlayer.getUsername(), context.getInput());
                    String a = context.get(mobId);
                    String b = context.get(settingsId);
                    MobManager.spawnEntity(a, b, rpgPlayer.getInstance(), rpgPlayer.getPosition());
                }
            },mobId, settingsId);


        }
    }

    /**
     * @return
     */
    @Override
    public Map<String, String> getCmdInfoMap() {
        return cmdInfoMap;
    }
}
