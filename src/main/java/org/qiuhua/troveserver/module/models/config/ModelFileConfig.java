package org.qiuhua.troveserver.module.models.config;

import net.worldseed.multipart.ModelEngine;
import net.worldseed.resourcepack.PackBuilder;
import org.qiuhua.troveserver.Main;
import org.qiuhua.troveserver.api.config.IConfig;
import org.qiuhua.troveserver.utils.FileUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

public class ModelFileConfig implements IConfig {

    private final static File models = new File(FileUtils.getDataFolder(), "models/models");

    private final static File resourcepack = new File(FileUtils.getDataFolder(), "models/resourcepack");

    private final static File model_data = new File(FileUtils.getDataFolder(), "models/model_data");

    public final static List<String> modelNameList = new ArrayList<>();

    @Override
    public void reload() {
        load();
    }

    @Override
    public void load() {
        modelNameList.clear();
        try {
            PackBuilder.ConfigJson packBuilder = PackBuilder.generate(models.toPath(), resourcepack.toPath(), model_data.toPath());
            // 生成模型映射表
            FileUtils.writeStringToFile(
                    new File(FileUtils.getDataFolder(), "models/model_mappings.json"),
                    packBuilder.modelMappings(),
                    Charset.defaultCharset()
            );
            Reader mappingsData = new InputStreamReader(
                    new FileInputStream(
                            new File(FileUtils.getDataFolder(), "models/model_mappings.json")
                    )
            );
            ModelEngine.loadMappings(mappingsData, model_data.toPath());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        modelNameList.addAll(FileUtils.loadFilesName(models));
        Main.getLogger().info("加载模型文件 {} 个",modelNameList.size());
    }
}
