package org.qiuhua.troveserver.module.item.config;

import org.qiuhua.troveserver.Main;
import org.qiuhua.troveserver.api.config.IConfig;
import org.qiuhua.troveserver.module.item.AttributeRandomTemplate;
import org.qiuhua.troveserver.module.item.ItemManager;
import org.qiuhua.troveserver.utils.FileUtils;
import org.qiuhua.troveserver.utils.StringUtils;
import org.qiuhua.troveserver.utils.yaml.ConfigurationSection;
import org.qiuhua.troveserver.utils.yaml.file.YamlConfiguration;

import java.io.File;

public class ItemConfig implements IConfig {


    public static String attributeFormat_format;
    public static String attributeFormat_strengthenIcon;

    @Override
    public void reload() {
        load();
    }

    @Override
    public void load() {
        if (!(new File(FileUtils.getDataFolder() , "item/config.yml").exists())){
            FileUtils.saveResource("item/config.yml", false);
        }
        YamlConfiguration config = FileUtils.loadFile("item/config.yml");
        attributeFormat_format = StringUtils.colorCodeConversion(config.getString("AttributeFormat.format"));
        attributeFormat_strengthenIcon = config.getString("AttributeFormat.strengthenIcon", "+");
        ItemManager.allAttributeRandomTemplate.clear();
        ConfigurationSection attributeRandomTemplateSection = config.getConfigurationSection("AttributeRandomTemplate");
        if(attributeRandomTemplateSection != null){
            for (String key : attributeRandomTemplateSection.getKeys(false)){
                ConfigurationSection templateSection = attributeRandomTemplateSection.getConfigurationSection(key);
                if(templateSection != null){
                    ItemManager.allAttributeRandomTemplate.put(key, new AttributeRandomTemplate(templateSection, key));
                }
            }
        }
        Main.getLogger().info("加载属性模版 {} 个", ItemManager.allAttributeRandomTemplate.size());

    }


}
