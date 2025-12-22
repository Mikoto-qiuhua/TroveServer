package org.qiuhua.troveserver.module.world;

import com.dfsek.terra.minestom.world.TerraMinestomWorldBuilder;
import net.minestom.server.MinecraftServer;

import net.minestom.server.instance.Instance;
import net.minestom.server.instance.InstanceContainer;
import net.minestom.server.instance.InstanceManager;
import net.minestom.server.instance.LightingChunk;
import net.minestom.server.instance.anvil.AnvilLoader;
import org.jetbrains.annotations.Nullable;
import org.qiuhua.troveserver.Main;
import org.qiuhua.troveserver.config.ServerConfig;
import org.qiuhua.troveserver.utils.FileUtils;
import org.qiuhua.troveserver.utils.yaml.ConfigurationSection;

import java.util.concurrent.ConcurrentHashMap;


public class WorldManager {

    private static InstanceManager instanceManager;

    private final static ConcurrentHashMap<String, Instance> allInstance = new ConcurrentHashMap<>();

    public static void init(){
        FileUtils.createFolder("worlds");
        instanceManager = MinecraftServer.getInstanceManager();
        //这里开始要加载默认需要加载的世界
        ConfigurationSection world_autoLoad_section = ServerConfig.world_autoLoad_section;
        for(String key : world_autoLoad_section.getKeys(false)){
            String path = world_autoLoad_section.getString(key + ".path");
            loadWorld(key, path);
        }

    }

    public static InstanceContainer loadWorld(String worldName, String worldFolder){
        if(allInstance.containsKey(worldName)){
            Main.getLogger().warn("世界名称 {} 和现有世界名称重复", worldName);
            return null;
        }
        Long startTime = System.currentTimeMillis();
        InstanceContainer instanceContainer = instanceManager.createInstanceContainer();
        instanceContainer.setChunkSupplier(LightingChunk::new);
        instanceContainer.setChunkLoader(new AnvilLoader(FileUtils.getDataFolder() + "/worlds/" + worldFolder));
        TerraMinestomWorldBuilder.from(instanceContainer).packById("TEST").attach();
        allInstance.put(worldName, instanceContainer);
        Long endTime = System.currentTimeMillis();
        Main.getLogger().info("世界 {} 加载完成 {}ms", worldName, (endTime-startTime));
        return instanceContainer;
    }

    @Nullable
    public static Instance getInstance(String worldName){
        return allInstance.get(worldName);
    }






}
