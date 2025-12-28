package org.qiuhua.troveserver.module.role.config;

import net.minestom.server.item.ItemStack;
import net.minestom.server.item.Material;
import org.qiuhua.troveserver.api.config.IConfig;
import org.qiuhua.troveserver.module.item.ItemManager;
import org.qiuhua.troveserver.utils.FileUtils;
import org.qiuhua.troveserver.utils.StringUtils;
import org.qiuhua.troveserver.utils.yaml.file.YamlConfiguration;

import java.io.File;
import java.util.List;

public class RoleConfig implements IConfig {

    public static Double switchHealthValue;

    public static Integer jumpTime;

    public static Integer sprintTime;

    public static Double sprintSpeed;

    public static Double jumpSpeed;


    @Override
    public void reload() {
        load();
    }

    @Override
    public void load() {
        if (!(new File(FileUtils.getDataFolder() , "role/config.yml").exists())){
            FileUtils.saveResource("role/config.yml", false);
        }
        YamlConfiguration config = FileUtils.loadFile("role/config.yml");
        switchHealthValue = config.getDouble("SwitchHealthValue", 0.1);
        jumpTime = config.getInt("JumpTime", 200);
        sprintTime = config.getInt("SprintTime", 5000);
        sprintSpeed = config.getDouble("SprintSpeed", 1);
        jumpSpeed = config.getDouble("JumpSpeed", 10);
    }
}
