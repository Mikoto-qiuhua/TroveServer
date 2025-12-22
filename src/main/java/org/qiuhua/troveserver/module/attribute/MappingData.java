package org.qiuhua.troveserver.module.attribute;

import com.ezylang.evalex.EvaluationException;
import com.ezylang.evalex.Expression;
import com.ezylang.evalex.parser.ParseException;
import lombok.Getter;
import org.jetbrains.annotations.Nullable;
import org.qiuhua.troveserver.Main;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class MappingData {


    /**
     * 属性key名称
     */
    @Getter
    private final String attributeKey;

    /**
     * 映射的属性名称  不是这个属性本身的名称
     */
    @Getter
    private final String attributeName;

    /**
     * 计算方式
     */
    @Getter
    private final String read_pattern;

    /**
     * 属性值预编译公式
     */
    private final Expression expression;

    @Getter
    private final String value;

    public MappingData(String string, String attributeKey){
        this.attributeKey = attributeKey;
        if(string.endsWith("%")){
            this.read_pattern = "Percent";
        }else {
            this.read_pattern = "Default";
        }
        string = string.replaceAll("%", "");
        String[] parts = string.split(":");
        if(parts.length == 2){
            this.attributeName = parts[0];
            this.value = parts[1];
            Pattern pattern = Pattern.compile("\\{([^}]*)}");
            Matcher matcher = pattern.matcher(parts[1]);
            if(matcher.find()) {
                String content = matcher.group(1); // 获取第一个捕获组的内容
                this.expression = new Expression(content);
            }else {
                this.expression = null;
            }
        }else {
            this.attributeName = null;
            this.expression = null;
            this.value = null;
        }

    }

    /**
     * 计算结果
     * @param map
     * @return
     */
    public Double total(Map<String, Object> map){
        try {
            return this.expression.withValues(map).evaluate().getNumberValue().doubleValue();
        } catch (EvaluationException | ParseException e) {
            Main.getLogger().error("计算错误 {} | " + e, expression);
            return 0.0;
        }

    }

    /**
     * 获取计算后的属性
     * @param map
     * @return
     */
    @Nullable
    public AttributeCompileGroup.CompileData getCompileData(Map<String, Object> map){
        AttributeDataConfig mappingAttributeDataConfig = AttributeManager.getAttributeDataConfig(this.attributeName);
        if(mappingAttributeDataConfig != null){
            String mappingAttributeKey = mappingAttributeDataConfig.getAttributeKey();
            AttributeCompileGroup.CompileData compileData = new AttributeCompileGroup.CompileData(mappingAttributeKey);
            Double total = total(map);
            if(this.read_pattern.equals("Percent")){
                compileData.setPercent(total / 100);
            }else {
                compileData.setAmount(total);
            }
            return compileData;
        }
        return null;
    }





}
