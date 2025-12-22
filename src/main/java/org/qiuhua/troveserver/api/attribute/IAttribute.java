package org.qiuhua.troveserver.api.attribute;

import org.qiuhua.troveserver.module.attribute.AttributeCompileGroup;
import org.qiuhua.troveserver.module.attribute.EntityAttributesData;

public interface IAttribute {



    void addAttribute(AttributeCompileGroup attributeCompileGroup, String source);

    void removeAttribute(String source);

    Double getAttributeTotal(String attributeKey);

    Double getAttributeAmount(String attributeKey);

    Double getAttributePercent(String attributeKey);

    Double getAttributeMax(String attributeKey);

    Double getAttributeMin(String attributeKey);

    void updateAttribute();

    void updateVanilla();

    EntityAttributesData getEntityAttributesData();

}
