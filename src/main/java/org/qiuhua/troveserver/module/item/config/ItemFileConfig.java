package org.qiuhua.troveserver.module.item.config;

import org.qiuhua.troveserver.Main;
import org.qiuhua.troveserver.config.ConfigManager;
import org.qiuhua.troveserver.api.config.IConfig;
import org.qiuhua.troveserver.module.item.ItemCompileData;
import org.qiuhua.troveserver.module.item.ItemManager;
import org.qiuhua.troveserver.utils.FileUtils;
import org.qiuhua.troveserver.utils.yaml.ConfigurationSection;
import org.qiuhua.troveserver.utils.yaml.file.YamlConfiguration;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class ItemFileConfig implements IConfig {

    public final static Map<String, YamlConfiguration> allItemConfig = new HashMap<>();

    @Override
    public void reload() {
        load();
    }

    @Override
    public void load(){
        allItemConfig.clear();
        //如果文件夹不在 才生成
        if (!(new File(FileUtils.getDataFolder() , "item/items").exists())){
            FileUtils.saveResource("item/items/烈焰宝石.yml", false);
            FileUtils.saveResource("item/items/潮汐宝石.yml", false);
            FileUtils.saveResource("item/items/苍穹宝石.yml", false);
        }
        Map<String, YamlConfiguration> map = FileUtils.loadFiles(new File(FileUtils.getDataFolder(), "item/items"));
        allItemConfig.putAll(map);
        Main.getLogger().info("加载Item配置文件 {} 个", map.size());
        loadItem();
    }

    //加载配置文件中的全部物品
    private void loadItem(){
        ItemManager.allItem.clear();
        allItemConfig.values().forEach(config -> {
            for (String key : config.getKeys(false)){
                ConfigurationSection section = config.getConfigurationSection(key);
                ItemCompileData itemCompileData = new ItemCompileData(section, key);
                if(!itemCompileData.getItemStack().isAir()){
                    if( ItemManager.allItem.containsKey(key)){
                        Main.getLogger().warn("出现重名物品 {} 请注意修改,本次将越过该物品读取", key);
                        continue;
                    }
                    ItemManager.allItem.put(key, itemCompileData);
                }
            }
        });
        Main.getLogger().info("加载物品 {} 个",  ItemManager.allItem.size());

//        JsonElement json = ItemStack.CODEC.encode(Transcoder.JSON, allItem.get("测试火焰宝石").getItemStack()).orElseThrow();
//        Main.getLogger().info(json.toString());

    }

}
