package org.qiuhua.troveserver.module.models;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import net.kyori.adventure.text.Component;
import net.minestom.server.coordinate.Pos;
import net.minestom.server.entity.Entity;
import net.minestom.server.entity.LivingEntity;
import net.minestom.server.entity.Player;
import net.minestom.server.instance.Instance;
import net.minestom.server.item.ItemStack;
import net.worldseed.multipart.GenericModelImpl;
import net.worldseed.multipart.model_bones.ModelBone;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.qiuhua.troveserver.Main;
import org.qiuhua.troveserver.api.entity.AbstractEntity;
import org.qiuhua.troveserver.entity.ModelEntity;
import org.qiuhua.troveserver.entity.display.ModelsTextTagEntity;
import org.qiuhua.troveserver.module.models.bone.ModelBoneItem;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ModelsData extends GenericModelImpl {


    /**
     * 模型的id
     */
    @Getter
    private final String modelId;


    /**
     * 这个模型所属的实体对象
     */
    @Getter
    private final LivingEntity mainEntity;

    /**
     * 模型整体缩放大小
     */
    @Getter
    @Setter
    @Accessors(chain=true)
    private float scale = 1;

    /**
     * 动画管理器
     */
    @Getter
    @Setter
    private AnimationManager animationManager;

    /**
     * 模型所绑定的显示文本的实体
     */
    @Getter
    private final ConcurrentHashMap<String, ModelsTextTagEntity> textTagEntityMap = new ConcurrentHashMap<>();


    public ModelsData(String modelId, ModelEntity modelEntity){
        this.modelId = modelId;
        this.mainEntity = modelEntity;
    }


    @Override
    public void init(@Nullable Instance instance, @NotNull Pos position) {
        super.init(instance, position, scale);
        animationManager = new AnimationManager(this);
        textTagEntityMap.forEach((name, entity)->{
            entity.spawnEntity(instance, null);
        });
        mainEntity.getViewers().forEach(this::addViewer);
//        getParts().forEach(modelBone -> {
//            Main.getLogger().debug(modelBone.toString() + " -> " + modelBone.getName());
//        });
    }


    /**
     * 添加一个文本实体
     * @param boneName
     * @param text
     * @return
     */
    public ModelsData addTextTagEntity(String boneName, Component text){
        ModelsTextTagEntity modelsTextTagEntity = new ModelsTextTagEntity(this, boneName, text);
        textTagEntityMap.put(boneName, modelsTextTagEntity);
        return this;
    }


    /**
     * 给物品骨骼设置物品
     * @param boneName
     * @param itemStack
     * @return
     */
    public ModelsData setModelItem(String boneName, ItemStack itemStack){
        if(getPart(boneName) instanceof ModelBoneItem modelBoneItem){
            modelBoneItem.getItemMeta().setItemStack(itemStack);
        }
        return this;
    }

    /**
     * 将模型绑定在某个指定的实体上
     * 这里模型要基于0的位置Y轴往下偏移-32.3格
     * @param entity
     */
    public void binding(Entity entity){
        getParts().forEach(modelBone -> {
            Entity e = modelBone.getEntity();
            if(e != null){
                entity.addPassenger(e);
            }
        });
    }

    /**
     * 获取指定骨骼的坐标
     * @param boneName
     * @return
     */
    @Nullable
    public Pos getBonePos(String boneName){
        ModelBone bone = getPart(boneName);
        if(bone == null) return null;
        return bone.calculatePosition();
    }

    @Override
    public boolean addViewer(@NotNull Player player){
        boolean a = super.addViewer(player);
        if(a){
            textTagEntityMap.values().forEach(entity -> {
                if(entity.isActive()){
                    entity.addViewer(player);
                }
            });
        }
        return a;
    }

    @Override
    public boolean removeViewer(@NotNull Player player){
        boolean a = super.removeViewer(player);
        if(a){
            textTagEntityMap.values().forEach(entity -> {
                entity.removeViewer(player);
            });
        }
        return a;
    }



    /**
     * 这里重写多注册武器骨骼
     */
    @Override
    protected void registerBoneSuppliers() {
        boneSuppliers.put(name -> name.contains("item"),
                (info) -> new ModelBoneItem(
                        info.pivot(), info.name(), info.rotation(), info.model(), info.scale()));
        super.registerBoneSuppliers();
    }

    @Override
    public void destroy(){
        textTagEntityMap.values().forEach(AbstractEntity::remove);
        animationManager.getAnimationHandler().destroy();
        super.destroy();
    }

    @Override
    public String getId() {
        return this.modelId;
    }


}
