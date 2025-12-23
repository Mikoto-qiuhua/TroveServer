package org.qiuhua.troveserver.module.item.command;

import net.minestom.server.command.builder.Command;
import net.minestom.server.command.builder.arguments.ArgumentString;
import net.minestom.server.command.builder.arguments.ArgumentType;
import net.minestom.server.command.builder.arguments.ArgumentWord;
import net.minestom.server.command.builder.arguments.minecraft.ArgumentResourceLocation;
import net.minestom.server.command.builder.arguments.number.ArgumentInteger;
import net.minestom.server.command.builder.suggestion.SuggestionEntry;
import net.minestom.server.item.ItemStack;
import org.qiuhua.troveserver.Main;
import org.qiuhua.troveserver.api.command.AbstractCommand;
import org.qiuhua.troveserver.player.RPGPlayer;
import org.qiuhua.troveserver.module.item.ItemManager;

import java.util.Map;

public class ItemCommand extends AbstractCommand {

    private static final Map<String, String> cmdInfoMap = Map.of(
            "list", "显示物品列表",
            "give [物品ID] <数量>", "获取物品"
    );

    public ItemCommand() {
        super("ItemSystem", "item");
        addSubcommand(new Give());
    }


    private static class Give extends Command {
        public Give() {
            super("give");
            ArgumentWord itemId = ArgumentType.Word("itemId");
            ArgumentInteger amount = ArgumentType.Integer("amount");
            amount.setDefaultValue(1);
            itemId.setSuggestionCallback((commandSender, commandContext, suggestion) -> {
                ItemManager.allItem.keySet().forEach(key -> {
                    suggestion.addEntry(new SuggestionEntry(key));
                });
            });
            addSyntax((sender, context) -> {
                if (sender instanceof RPGPlayer rpgPlayer){
                    Main.getLogger().info("{} 执行指令 /{}", rpgPlayer.getUsername(), context.getInput());
                    String string = context.get(itemId);
                    Integer integer = context.get(amount);
                    ItemStack itemStack = ItemManager.giveItem(string, integer);
                    if(itemStack != null){
                        rpgPlayer.getInventory().addItemStack(itemStack);
                    }
                }
            },itemId, amount);
        }
    }




    @Override
    public Map<String, String> getCmdInfoMap() {
        return cmdInfoMap;
    }
}
