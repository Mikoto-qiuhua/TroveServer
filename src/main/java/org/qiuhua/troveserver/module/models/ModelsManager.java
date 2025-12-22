package org.qiuhua.troveserver.module.models;

import net.kyori.adventure.text.Component;
import net.minestom.server.coordinate.Pos;
import net.minestom.server.entity.LivingEntity;
import net.minestom.server.instance.Instance;
import net.minestom.server.tag.Tag;
import net.worldseed.multipart.model_bones.BoneEntity;
import org.jetbrains.annotations.Nullable;
import org.qiuhua.troveserver.entity.ModelEntity;
import org.qiuhua.troveserver.config.ConfigManager;
import org.qiuhua.troveserver.module.models.command.ModelsCommand;
import org.qiuhua.troveserver.module.models.config.ModelFileConfig;
import org.qiuhua.troveserver.utils.FileUtils;


public class ModelsManager {


    public static void init(){
        FileUtils.createFolder("models/resourcepack");
        FileUtils.createFolder("models/models");
        ConfigManager.loadConfig("model", "models", new ModelFileConfig());
        new ModelsCommand();
    }

    /**
     * 生成一个模型
     * @param modelId
     */
    public static void spawnModel(String modelId, Instance instance, Pos pos){
        ModelEntity modelEntity = new ModelEntity(modelId);
        modelEntity.getModelsData().addTextTagEntity("nametag", Component.text(modelId));
        modelEntity.spawnEntity(instance, pos);
    }

    /**
     * 获取实体的指定骨骼坐标
     * @param entity
     * @param boneName
     * @return
     */
    @Nullable
    public static Pos getModelEntityBonePos(ModelEntity entity, String boneName){
        return entity.getModelsData().getBonePos(boneName);
    }

    /**
     * 尝试获取这个实体的父系
     * 如果他是WSEE实体那就尝试返回主要的父系实体 如果不是hitbox则返回null
     * 如果不是 则返回本身
     * @param livingEntity
     * @return
     */
    @Nullable
    public static LivingEntity getParentEntity(LivingEntity livingEntity){
        String tag = livingEntity.getTag(Tag.String("WSEE"));
        if(tag != null && tag.equals("hitbox")){
            if(livingEntity instanceof BoneEntity bone) {
                return ((ModelsData) bone.getModel()).getMainEntity();
            }
        }else {
            return null;
        }
        return livingEntity;
    }






}
