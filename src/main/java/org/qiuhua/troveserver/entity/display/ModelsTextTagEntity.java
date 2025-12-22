package org.qiuhua.troveserver.entity.display;

import net.kyori.adventure.text.Component;
import net.minestom.server.component.DataComponents;
import net.minestom.server.coordinate.Pos;
import net.minestom.server.entity.EntityType;
import net.minestom.server.entity.Player;
import net.minestom.server.entity.metadata.other.ArmorStandMeta;
import net.minestom.server.instance.Instance;
import net.minestom.server.network.packet.server.LazyPacket;
import net.minestom.server.network.packet.server.play.SpawnEntityPacket;
import net.minestom.server.tag.Tag;
import net.worldseed.multipart.GenericModel;
import net.worldseed.multipart.model_bones.ModelBone;
import net.worldseed.multipart.model_bones.misc.ModelBoneNametag;
import org.jetbrains.annotations.NotNull;
import org.qiuhua.troveserver.Main;
import org.qiuhua.troveserver.api.entity.AbstractEntity;
import org.qiuhua.troveserver.module.models.ModelsData;

import java.util.Set;

public class ModelsTextTagEntity extends AbstractEntity {

    private final ModelsData modelsData;

    private final String boneName;

    private ModelBone modelBone;

    public ModelsTextTagEntity(ModelsData model, String boneName, Component text) {
        super(EntityType.ARMOR_STAND);
        this.setAutoViewable(false);
        this.setTag(Tag.String("WSEE"), "textTag");
        this.modelsData = model;
        this.boneName = boneName;
        this.setNoGravity(true);
        this.setCustomNameVisible(true);
        this.set(DataComponents.CUSTOM_NAME, text);
        this.setNoGravity(true);
        this.setInvisible(true);
        this.setBoundingBox(0,0,0);
        ArmorStandMeta meta = (ArmorStandMeta) getEntityMeta();
        meta.setMarker(true);
        this.setSynchronizationTicks(Integer.MAX_VALUE);
    }


    /**
     * 重新设置显示文本
     * @param text
     * @return
     */
    public ModelsTextTagEntity setText(Component text){
        this.set(DataComponents.CUSTOM_NAME, text);
        return this;
    }

    @Override
    public @NotNull Set<Player> getViewers() {
        return modelsData.getViewers();
    }



    @Override
    public void spawnEntity(Instance instance, Pos pos){
        modelBone = modelsData.getPart(boneName);
        if(modelBone == null){
            Main.getLogger().debug("{} 骨骼不存在,移除所绑定的实体");
            modelsData.getTextTagEntityMap().remove(boneName);
            remove();
            return;
        }
        if(modelBone instanceof ModelBoneNametag modelBoneNametag){
            modelBoneNametag.bind(this);
        }
        this.setInstance(instance, modelBone.calculatePosition()).join();
        teleport(modelBone.calculatePosition());
    }

    /**
     * 这里重写了tick让其能够同步到实体上
     * @param time
     */
    @Override
    public void tick(long time) {
        if(modelBone == null) remove();
        Pos pos = modelBone.calculatePosition();
        if(!getPosition().samePoint(pos) && getInstance() != null) {
            //Main.getLogger().debug("更新位置");
            teleport(pos);
        }
    }

    public GenericModel getModel() {
        return modelsData;
    }

    public String getName() {
        return boneName;
    }

    @Override
    public void updateNewViewer(@NotNull Player player) {
        SpawnEntityPacket spawnPacket = new SpawnEntityPacket(this.getEntityId(), this.getUuid(), this.getEntityType().id(), modelBone.calculatePosition(), 0, 0, (short)0, (short)0, (short)0);
        player.sendPacket(spawnPacket);
        player.sendPacket(new LazyPacket(this::getMetadataPacket));

    }

}
