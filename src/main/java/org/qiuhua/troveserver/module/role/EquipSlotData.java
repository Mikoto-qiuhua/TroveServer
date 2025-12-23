package org.qiuhua.troveserver.module.role;

import lombok.Getter;
import net.kyori.adventure.text.Component;
import net.minestom.server.component.DataComponents;
import net.minestom.server.item.ItemStack;
import net.minestom.server.item.Material;
import net.minestom.server.item.component.TooltipDisplay;
import org.jetbrains.annotations.Nullable;
import org.qiuhua.troveserver.player.RPGPlayer;
import org.qiuhua.troveserver.module.item.ItemManager;
import org.qiuhua.troveserver.utils.StringUtils;
import org.qiuhua.troveserver.utils.script.JavaScript;
import org.qiuhua.troveserver.utils.yaml.ConfigurationSection;

import java.util.Set;

public class EquipSlotData {

    @Getter
    private final RoleData roleData;

    @Getter
    private final String condition;

    @Getter
    @Nullable
    private final String defaultItemId;

    @Getter
    private ItemStack itemStack = ItemStack.AIR;



    @Getter
    private final String equipId;

    public EquipSlotData(String equipId, ConfigurationSection config, RoleData roleData){
        this.equipId = equipId;
        this.roleData = roleData;
        condition = config.getString("condition");
        //进行js条件预编译
        JavaScript.precompile(condition);
        defaultItemId = config.getString("defaultItemId", null);
        //设置了默认物品 那就从物品库拿一个物品过来
        if(defaultItemId != null){
            this.itemStack = ItemManager.giveItem(defaultItemId);
        }
    }


    /**
     * 检查条件
     * @param itemStack
     * @param rpgPlayer
     * @return
     */
    public Boolean checkCondition(ItemStack itemStack, RPGPlayer rpgPlayer){
        return (Boolean) JavaScript.evaluate(condition, rpgPlayer, itemStack);
    }

    /**
     * 将物品从槽位内取出
     * @return
     */
    public ItemStack takeItemStack(){
        ItemStack itemStack = this.itemStack;
        this.itemStack = ItemStack.AIR;
        return itemStack;
    }

    /**
     * 将物品放置到槽位内
     * @param itemStack
     * @return
     */
    public void putItemStack(ItemStack itemStack){
        this.itemStack = itemStack;
    }





}
