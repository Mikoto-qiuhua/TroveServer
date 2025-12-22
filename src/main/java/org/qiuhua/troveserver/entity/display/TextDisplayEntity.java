package org.qiuhua.troveserver.entity.display;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import net.kyori.adventure.text.Component;
import net.minestom.server.coordinate.Pos;
import net.minestom.server.entity.EntityType;
import net.minestom.server.entity.metadata.display.TextDisplayMeta;
import net.minestom.server.instance.Instance;
import org.qiuhua.troveserver.api.entity.AbstractDisplayEntity;


public class TextDisplayEntity extends AbstractDisplayEntity {

    /**
     * 需要显示的内容
     */
    @Getter
    @Accessors(chain=true)
    private Component text;

    @Getter
    private final TextDisplayMeta textDisplayMeta;


    /**
     * 是否穿透渲染文本
     */
    @Getter
    @Setter
    @Accessors(chain=true)
    private Boolean seeThrough = false;


    /**
     * 文本的对齐方式
     */
    @Getter
    @Setter
    @Accessors(chain=true)
    private TextDisplayMeta.Alignment alignment = TextDisplayMeta.Alignment.CENTER;

    /**
     * 文本的背景颜色
     */
    @Getter
    private int background = 0x00000000;

    /**
     * 文本自动换行的长度
     */
    @Getter
    @Setter
    @Accessors(chain=true)
    private int line_width = 200;

    /**
     * 文本是否显示阴影
     */
    @Getter
    @Setter
    @Accessors(chain=true)
    private Boolean shadow = true;


    public TextDisplayEntity(Component text) {
        super(EntityType.TEXT_DISPLAY);
        this.text = text;
        textDisplayMeta = (TextDisplayMeta) getEntityMeta();
    }


    public TextDisplayEntity(String text) {
        super(EntityType.TEXT_DISPLAY);
        this.text = Component.text(text);
        textDisplayMeta = (TextDisplayMeta) getEntityMeta();
    }

    /**
     * 设置需要显示的内容
     * @param text
     * @return
     */
    public TextDisplayEntity setText(String text){
        this.text = Component.text(text);
        textDisplayMeta.setText(this.text);
        return this;
    }

    public TextDisplayEntity setText(Component text){
        this.text = text;
        textDisplayMeta.setText(text);
        return this;
    }

    @Override
    public void spawnEntity(Instance instance, Pos pos){
        textDisplayMeta.setAlignment(alignment);
        textDisplayMeta.setBackgroundColor(background);
        textDisplayMeta.setLineWidth(line_width);
        textDisplayMeta.setShadow(shadow);
        textDisplayMeta.setText(text);
        textDisplayMeta.setSeeThrough(seeThrough);
        super.spawnEntity(instance, pos);
    }


    public TextDisplayEntity setBackground(String color, float transparency){
        background = convertColorWithTransparency(color, transparency);
        //Main.getLogger().debug("颜色为 {}", background);
        return this;
    }

    private int convertColorWithTransparency(String webColor, float transparency) {
        //参数验证
        if (transparency < 0) transparency = 0;
        if (transparency > 1) transparency = 1;
        //解析web颜色（移除#号）
        String colorStr = webColor.startsWith("#") ? webColor.substring(1) : webColor;
        int rgb = Integer.parseInt(colorStr, 16);
        //计算Alpha值：0=完全透明(0x00)，1=完全不透明(0xFF)
        int alpha = (int) (transparency * 0xFF);
        //组合ARGB
        return (alpha << 24) | rgb;
    }



}
