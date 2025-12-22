package org.qiuhua.troveserver.api.command;

import net.minestom.server.command.CommandSender;
import net.minestom.server.command.builder.Command;
import net.minestom.server.command.builder.CommandContext;
import net.minestom.server.entity.Player;
import org.qiuhua.troveserver.Main;

import java.util.function.BiConsumer;

public class SubCommand extends Command {


    public SubCommand(String name, BiConsumer<CommandSender, CommandContext> biConsumer) {
        super(name);
        setDefaultExecutor((sender, context) -> {
            if(sender instanceof Player player){
                Main.getLogger().info("{} 执行指令 /{}", player.getUsername(), context.getInput());
            }else {
                Main.getLogger().info("执行指令 /{}", context.getInput());
            }
            biConsumer.accept(sender, context);
        });

    }


}
