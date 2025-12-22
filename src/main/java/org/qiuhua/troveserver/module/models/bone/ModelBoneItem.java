package org.qiuhua.troveserver.module.models.bone;

import lombok.Getter;
import net.minestom.server.coordinate.Point;
import net.minestom.server.coordinate.Pos;
import net.minestom.server.coordinate.Vec;
import net.minestom.server.entity.EntityType;
import net.minestom.server.entity.metadata.display.ItemDisplayMeta;
import net.worldseed.multipart.GenericModel;
import net.worldseed.multipart.model_bones.BoneEntity;
import net.worldseed.multipart.model_bones.ModelBoneViewable;
import net.worldseed.multipart.model_bones.display_entity.ModelBonePartDisplay;


public class ModelBoneItem extends ModelBonePartDisplay implements ModelBoneViewable {


    @Getter
    private ItemDisplayMeta itemMeta;

    public ModelBoneItem(Point pivot, String name, Point rotation, GenericModel model, float scale) {
        super(pivot, name, rotation, model, scale);
        if (this.offset != null) {
            this.stand = new BoneEntity(EntityType.ITEM_DISPLAY, model, name);
            itemMeta = (ItemDisplayMeta)this.stand.getEntityMeta();
            itemMeta.setScale(new Vec(scale, scale, scale));
            itemMeta.setDisplayContext(ItemDisplayMeta.DisplayContext.NONE);
            itemMeta.setTransformationInterpolationDuration(2);
            itemMeta.setPosRotInterpolationDuration(2);
            itemMeta.setViewRange(1000.0F);
        }

    }




    @Override
    public Pos calculatePosition() {
        if (this.offset == null) {
            return Pos.ZERO;
        } else {
            Point p = this.offset;
            p = this.applyTransform(p);
            p = this.calculateGlobalRotation(p);
            Pos endPos = new Pos(p);
            return endPos.div(4.0F, 4.0F, 4.0F).mul(this.scale).add(this.model.getPosition());
        }
    }





}
