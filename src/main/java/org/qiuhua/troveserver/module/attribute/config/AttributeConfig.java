package org.qiuhua.troveserver.module.attribute.config;

import com.ezylang.evalex.EvaluationException;
import com.ezylang.evalex.Expression;
import com.ezylang.evalex.parser.ParseException;
import lombok.Getter;
import net.minestom.server.MinecraftServer;
import org.qiuhua.troveserver.Main;
import org.qiuhua.troveserver.api.config.IConfig;
import org.qiuhua.troveserver.player.RPGPlayer;
import org.qiuhua.troveserver.module.attribute.AttributeCompileGroup;
import org.qiuhua.troveserver.module.attribute.AttributeManager;
import org.qiuhua.troveserver.utils.FileUtils;
import org.qiuhua.troveserver.utils.yaml.ConfigurationSection;
import org.qiuhua.troveserver.utils.yaml.file.YamlConfiguration;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AttributeConfig implements IConfig {

    public static List<String> base_attribute_player = new ArrayList<>();

    public static Map<String, VanillaAttributeConfig> vanillaAttributeConfigMap = new HashMap<>();


    @Override
    public void reload() {
        base_attribute_player.clear();
        load();
        MinecraftServer.getConnectionManager().getOnlinePlayers().forEach(player -> {
            player.scheduler().scheduleNextTick(()->{
                AttributeCompileGroup attributeCompileGroup = AttributeManager.getStringAttributeGroup(base_attribute_player);
                RPGPlayer rpgPlayer = (RPGPlayer) player;
                rpgPlayer.removeAttribute("base_attribute_player");
                rpgPlayer.addAttribute(attributeCompileGroup, "base_attribute_player");
                rpgPlayer.updateAttribute();
            });
        });
    }

    @Override
    public void load() {
        if (!(new File(FileUtils.getDataFolder() , "attribute/config.yml").exists())){
            FileUtils.saveResource("attribute/config.yml", false);
        }
        YamlConfiguration config = FileUtils.loadFile("attribute/config.yml");
        base_attribute_player = config.getStringList("base_attribute_player");

        ConfigurationSection vanillaSection = config.getConfigurationSection("vanilla");
        for (String key : vanillaSection.getKeys(false)){
            vanillaAttributeConfigMap.put(key, new VanillaAttributeConfig(vanillaSection.getConfigurationSection(key), key));
        }

    }



    public static class VanillaAttributeConfig{

        /**
         * 属性值预编译公式
         */
        @Getter
        private final Expression expression;

        /**
         * 是否关闭原版属性
         * true=开启 false=关闭
         */
        @Getter
        private final Boolean vanilla;

        /**
         * 原版的属性id
         */
        @Getter
        private final String attributeId;


        public VanillaAttributeConfig(ConfigurationSection config, String attributeId){
            this.expression = new Expression(config.getString("value"));
            this.vanilla = config.getBoolean("vanilla",false);
            this.attributeId = attributeId;
        }

        /**
         * 计算结果
         * @param map
         * @return
         */
        public Double total(Map<String, Double> map){
            try {
                return this.expression.withValues(map).evaluate().getNumberValue().doubleValue();
            } catch (EvaluationException | ParseException e) {
                Main.getLogger().error("计算错误 {} | " + e, expression);
                return 0.0;
            }

        }
    }

}
