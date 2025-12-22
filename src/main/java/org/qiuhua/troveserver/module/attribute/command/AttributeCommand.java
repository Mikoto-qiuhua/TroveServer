package org.qiuhua.troveserver.module.attribute.command;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.minestom.server.component.DataComponents;
import net.minestom.server.entity.LivingEntity;
import net.minestom.server.entity.Player;
import org.qiuhua.troveserver.api.command.AbstractCommand;
import org.qiuhua.troveserver.api.command.SubCommand;
import org.qiuhua.troveserver.player.RPGPlayer;
import org.qiuhua.troveserver.module.attribute.AttributeManager;

import java.util.Map;

public class AttributeCommand extends AbstractCommand {

    private static final Map<String, String> cmdInfoMap = Map.of(
            "info", "查看自身属性"
    );

    public AttributeCommand() {
        super("AttributeSystem", "attribute");
        addSubcommand(new SubCommand("info", ((commandSender, commandContext) -> {
            if(commandSender instanceof Player player){
                player.sendMessage(attributeInfo(player));
            }
        })));
    }



    /**
     * 生成属性信息
     */
    public Component attributeInfo(LivingEntity livingEntity) {
        return Component.text()
                .append(createHeader(livingEntity))
                .append(Component.newline())
                .append(createAttributeList(livingEntity))
                .append(Component.newline())
                .build();
    }

    /**
     * 创建标题部分
     */
    private Component createHeader(LivingEntity livingEntity) {
        String name;
        if(livingEntity instanceof Player player){
            name = player.getUsername();
        }else {
            if(livingEntity.get(DataComponents.CUSTOM_NAME) == null){
                name = livingEntity.getEntityType().name();
            }else {
                name = livingEntity.get(DataComponents.CUSTOM_NAME).toString();
            }
        }
        return Component.text()
                .append(Component.text("❖          ").color(TextColor.fromHexString("#00fff6")))
                .append(Component.text(name).color(TextColor.fromHexString("#ffa200"))
                        .decorate(TextDecoration.BOLD))
                .append(Component.text("          ❖").color(TextColor.fromHexString("#00fff6")))
                .append(Component.newline())
                .build();
    }

    /**
     * 创建命令列表
     */
    private Component createAttributeList(LivingEntity livingEntity) {
        TextComponent.Builder builder = Component.text();
        boolean first = true;
        for(String attributeKey : AttributeManager.getAttributesDataConfigMap().keySet()){
            if (!first) {
                builder.append(Component.newline());
            }
            first = false;
            builder.append(createAttributeLine(livingEntity, attributeKey));
        }
        return builder.build();
    }



    /**
     * 创建单行命令信息
     */
    private Component createAttributeLine(LivingEntity livingEntity, String attributeKey) {
        Double total = 0.0;
        Double amount = 0.0;
        Double percent = 0.0;
        String strMax = "null";
        String strMin = "null";
        if(livingEntity instanceof RPGPlayer rpgPlayer){
            total = rpgPlayer.getAttributeTotal(attributeKey);
            amount = rpgPlayer.getAttributeAmount(attributeKey);
            percent = rpgPlayer.getAttributePercent(attributeKey);
            Double min = rpgPlayer.getAttributeMin(attributeKey);
            Double max = rpgPlayer.getAttributeMax(attributeKey);
            if(min != null) strMin = min.toString();
            if(max != null) strMax = max.toString();
        }


        return Component.text()
                .append(Component.text("▶ ").color(TextColor.fromHexString("#00fff6")))
                .append(Component.text(attributeKey).color(TextColor.fromHexString("#55ff55"))
                .append(Component.text(" -> ").color(TextColor.fromHexString("#555555")))
                .append(Component.text(total).color(TextColor.fromHexString("#ffaa00"))))
                .hoverEvent(HoverEvent.showText(Component.text("amount: " + amount)
                        .append(Component.newline())
                        .append(Component.text("percent: " + percent))
                        .append(Component.newline())
                        .append(Component.text("max: " + strMax))
                        .append(Component.newline())
                        .append(Component.text("min: " + strMin))
                        .color(TextColor.fromHexString("#ffffff"))))
                .build();
    }





    @Override
    public Map<String, String> getCmdInfoMap() {
        return cmdInfoMap;
    }
}
