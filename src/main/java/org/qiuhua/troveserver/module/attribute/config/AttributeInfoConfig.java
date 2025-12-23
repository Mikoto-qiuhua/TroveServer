package org.qiuhua.troveserver.module.attribute.config;

import net.minestom.server.MinecraftServer;
import org.qiuhua.troveserver.Main;
import org.qiuhua.troveserver.api.config.IConfig;
import org.qiuhua.troveserver.player.RPGPlayer;
import org.qiuhua.troveserver.module.attribute.AttributeCompileGroup;
import org.qiuhua.troveserver.module.attribute.AttributeDataConfig;
import org.qiuhua.troveserver.module.attribute.AttributeManager;
import org.qiuhua.troveserver.utils.FileUtils;
import org.qiuhua.troveserver.utils.yaml.file.YamlConfiguration;

import java.io.File;

public class AttributeInfoConfig implements IConfig {

    @Override
    public void reload() {
        load();
        MinecraftServer.getConnectionManager().getOnlinePlayers().forEach(player -> {
            player.scheduler().scheduleNextTick(()->{
                AttributeCompileGroup attributeCompileGroup = AttributeManager.getStringAttributeGroup(AttributeConfig.base_attribute_player);
                RPGPlayer rpgPlayer = (RPGPlayer) player;
                rpgPlayer.removeAttribute("base_attribute_player");
                rpgPlayer.addAttribute(attributeCompileGroup, "base_attribute_player");
                rpgPlayer.updateAttribute();
            });
        });
    }

    @Override
    public void load() {
        if (!(new File(FileUtils.getDataFolder() , "attribute/attributes.yml").exists())){
            FileUtils.saveResource("attribute/attributes.yml", false);
        }
        YamlConfiguration config = FileUtils.loadFile("attribute/attributes.yml");
        int i = 0;
        for (String key : config.getKeys(false)){
            AttributeDataConfig attributeDataConfig = new AttributeDataConfig(config.getConfigurationSection(key), key);
            AttributeManager.register(key, attributeDataConfig);
            i++;
        }
        Main.getLogger().info("加载属性 {} 个", i);
    }




}
