package org.qiuhua.troveserver.arcartx.core.config;

import com.google.gson.annotations.SerializedName;
import lombok.Getter;
import lombok.Setter;
import org.qiuhua.troveserver.api.config.IConfig;
import org.qiuhua.troveserver.utils.FileUtils;
import org.qiuhua.troveserver.utils.yaml.ConfigurationSection;
import org.qiuhua.troveserver.utils.yaml.file.YamlConfiguration;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Setting implements IConfig {


    /**
     * 加密的资源包
     */
    public static final Map<String, AxResource> resource = new HashMap<>();

    /**
     * 客户端标题
     */
    public static String clientTitle;

    /**
     * 是否启用crc64检测
     */
    public static boolean crc64_enable;

    /**
     *  是否是全匹配模式
     *  如果是 则需要完全匹配 如果不是 则允许缺少一部分
     */
    public static boolean crc64_allowPartial;

    /**
     * crc64列表
     */
    public static List<Long> crc64_list = new ArrayList<>();


    /**
     *
     */
    @Override
    public void reload() {
        load();
    }

    /**
     *
     */
    @Override
    public void load() {
        if (!(new File(FileUtils.getDataFolder() , "arcartx/setting.yml").exists())){
            FileUtils.saveResource("arcartx/setting.yml", false);
        }
        YamlConfiguration config = FileUtils.loadFile("arcartx/setting.yml");
        //加载客户端标题
        clientTitle = config.getString("clientTitle","欢迎使用Minestom-ArcartX,该标题可在setting.yml修改");

        //加载加密资源列表
        resource.clear();
        ConfigurationSection resourceSection = config.getConfigurationSection("encryptedResourceFiles");
        if(resourceSection != null){
            for (String key : resourceSection.getKeys(false)){
                String name = resourceSection.getString(key + ".fileName");
                String password = resourceSection.getString(key + ".password");
                resource.put(key, new AxResource(name, password));
            }
        }

        //加载crc64配置
        crc64_list.clear();
        crc64_list.addAll(config.getLongList("crc64.list"));
        crc64_enable = config.getBoolean("crc64.enable", false);
        crc64_allowPartial = config.getBoolean("crc64.allowPartial", false);

    }










    /**
     * 资源包
     */
    public class AxResource{

        @SerializedName(value="fileName")
        @Setter
        @Getter
        private String name = "Example.zip";

        @SerializedName(value="password")
        @Setter
        @Getter
        private String password = "123456789";

        public AxResource(){}

        public AxResource(String name, String password){

        }




    }





}
