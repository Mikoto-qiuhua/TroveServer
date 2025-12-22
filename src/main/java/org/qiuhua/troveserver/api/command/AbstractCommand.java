package org.qiuhua.troveserver.api.command;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.minestom.server.MinecraftServer;
import net.minestom.server.command.CommandSender;
import net.minestom.server.command.builder.Command;
import net.minestom.server.command.builder.condition.CommandCondition;
import org.jetbrains.annotations.Nullable;
import org.qiuhua.troveserver.Main;
import org.qiuhua.troveserver.player.RPGPlayer;

import java.util.Map;

public abstract class AbstractCommand extends Command {


    private final String cmdHeader;

    private final String cmd;

    public AbstractCommand(String cmdHeader, String cmd) {
        super(cmd);
        this.cmdHeader = cmdHeader;
        this.cmd = cmd;
        setCondition((commandSender, s) -> {
            if(commandSender instanceof RPGPlayer rpgPlayer){
                return rpgPlayer.isAdmin();
            }
            return true;
        });
        setDefaultExecutor((sender, context) -> {
            sender.sendMessage(generateCommandHelp());
        });
        MinecraftServer.getCommandManager().register(this);
    }

    public abstract Map<String, String> getCmdInfoMap();

    /**
     * 生成格式化的命令帮助信息
     */
    public Component generateCommandHelp() {
        return Component.text()
                .append(createHeader())
                .append(Component.newline())
                .append(createCommandList())
                .append(Component.newline())
                .append(createFooter())
                .build();
    }




    /**
     * 创建标题部分
     */
    private Component createHeader() {
        return Component.text()
                .append(Component.text("❖          ").color(TextColor.fromHexString("#00fff6")))
                .append(Component.text(cmdHeader).color(TextColor.fromHexString("#ffa200"))
                        .decorate(TextDecoration.BOLD))
                .append(Component.text("          ❖").color(TextColor.fromHexString("#00fff6")))
                .append(Component.newline())
                .append(Component.text("<>为可选 []为必填").color(TextColor.fromHexString("#aaaaaa"))).build();
    }

    /**
     * 创建命令列表
     */
    private Component createCommandList() {
        TextComponent.Builder builder = Component.text();
        boolean first = true;
        for (Map.Entry<String, String> entry : getCmdInfoMap().entrySet()) {
            if (!first) {
                builder.append(Component.newline());
            }
            first = false;
            builder.append(createCommandLine(entry.getKey(), entry.getValue()));
        }
        return builder.build();
    }

    /**
     * 创建单行命令信息
     */
    private Component createCommandLine(String command, String description) {
        return Component.text()
                .append(Component.text("▶ ").color(TextColor.fromHexString("#00fff6")))
                .append(Component.text("/" + cmd + " " + command).color(TextColor.fromHexString("#55ff55"))
                        .clickEvent(ClickEvent.suggestCommand("/" + cmd + " " + command))
                        .hoverEvent(HoverEvent.showText(Component.text("点击输入命令: /" + cmd + " " + command)
                                .color(TextColor.fromHexString("#ffffff")))))
                .append(Component.text(" - ").color(TextColor.fromHexString("#555555")))
                .append(Component.text(description).color(TextColor.fromHexString("#ffaa00")))
                .build();
    }

    /**
     * 创建底部信息
     */
    private Component createFooter() {
        return Component.text()
                .append(Component.text("共 " + getCmdInfoMap().size() + " 个命令可用").color(TextColor.fromHexString("#aaaaaa")))
                .build();
    }





}
