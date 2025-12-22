package org.qiuhua.troveserver.module.role.config;

import net.minestom.server.item.ItemStack;
import org.qiuhua.troveserver.api.config.IConfig;
import org.qiuhua.troveserver.module.item.ItemManager;
import org.qiuhua.troveserver.utils.FileUtils;
import org.qiuhua.troveserver.utils.StringUtils;
import org.qiuhua.troveserver.utils.yaml.file.YamlConfiguration;

import java.io.File;
import java.util.List;

public class RoleConfig implements IConfig {

    public static YamlConfiguration config;

    public static ItemStack fillItem;

    public static Long clickInterval;

    public static String title0;

    public static String title1;

    public static List<Integer> roleListSlot;

    public static Integer page_previousPage_slot;

    public static ItemStack page_previousPage_item;

    public static Integer page_nextPage_slot;

    public static ItemStack page_nextPage_item;

    public static List<Integer> useButton_slot;

    public static ItemStack useButton_item;

    public static Integer titleX;

    public static Integer infoX;

    public static Double switchHealthValue;

    public static Integer jumpTime;

    public static Integer sprintTime;

    public static Double sprintSpeed;

    public static Double jumpSpeed;

    public static ItemStack sprintItem;

    @Override
    public void reload() {
        load();
    }

    @Override
    public void load() {
        if (!(new File(FileUtils.getDataFolder() , "role/config.yml").exists())){
            FileUtils.saveResource("role/config.yml", false);
        }
        config = FileUtils.loadFile("role/config.yml");
        fillItem = ItemManager.buildItem(config.getConfigurationSection("InventoryGui.fillItem"), "");
        clickInterval = config.getLong("InventoryGui.clickInterval");
        title0 = StringUtils.colorCodeConversion(config.getString("InventoryGui.title0"));
        title1 = StringUtils.colorCodeConversion(config.getString("InventoryGui.title1"));
        roleListSlot = config.getIntegerList("InventoryGui.roleListSlot");
        page_previousPage_slot = config.getInt("InventoryGui.page.previousPage.slot");
        page_nextPage_slot = config.getInt("InventoryGui.page.nextPage.slot");
        useButton_slot = config.getIntegerList("InventoryGui.useButton.slot");
        useButton_item = ItemManager.buildItem(config.getConfigurationSection("InventoryGui.useButton.item"), "");
        page_previousPage_item = ItemManager.buildItem(config.getConfigurationSection("InventoryGui.page.previousPage.item"), "");
        page_nextPage_item = ItemManager.buildItem(config.getConfigurationSection("InventoryGui.page.nextPage.item"), "");
        titleX = config.getInt("InventoryGui.titleX", 0);
        infoX = config.getInt("InventoryGui.infoX", 0);
        switchHealthValue = config.getDouble("Role.switchHealthValue", 0.1);
        jumpTime = config.getInt("Role.jumpTime", 200);
        sprintTime = config.getInt("Role.sprintTime", 5000);
        sprintSpeed = config.getDouble("Role.sprintSpeed", 1);
        jumpSpeed = config.getDouble("Role.jumpSpeed", 10);
        sprintItem = ItemManager.buildItem(config.getConfigurationSection("SprintItem"), "");
    }
}
