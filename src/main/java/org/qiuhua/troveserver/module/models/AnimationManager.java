package org.qiuhua.troveserver.module.models;

import lombok.Getter;
import net.worldseed.multipart.animations.AnimationHandler;
import net.worldseed.multipart.animations.AnimationHandlerImpl;
import net.worldseed.multipart.animations.ModelAnimation;
import org.qiuhua.troveserver.Main;


import java.util.function.Consumer;

public class AnimationManager {
    @Getter
    private final AnimationHandler animationHandler;
    @Getter
    private final ModelsData modelsData;

    public AnimationManager(ModelsData modelsData) {
        this.animationHandler =  new AnimationHandlerImpl(modelsData);;
        this.modelsData = modelsData;
        Main.getLogger().debug(animationHandler.animationPriorities().toString());
    }

    /**
     * 播放重复动画 会立即停止当前动画
     *
     * @param animationId
     * @return
     */
    public boolean playRepeatAnimation(String animationId){
        //没有这个动画则不播放
        ModelAnimation modelAnimation = animationHandler.getAnimation(animationId);
        if(modelAnimation == null) return false;
        String nowAnimationId = animationHandler.getRepeating();
        if(nowAnimationId != null && nowAnimationId.equals(animationId)) return false;
        if(nowAnimationId != null){
            animationHandler.stopRepeat(nowAnimationId);
        }
        animationHandler.playRepeat(animationId);
        Main.getLogger().debug("切换到动画 {}", animationId);
        return true;
    }


    /**
     * 播放一次动画
     * 不允许覆盖死亡和出生
     * @param animationId
     * @param override
     * @param onComplete
     * @return
     */
    public boolean playOnceAnimation(String animationId, boolean override, Consumer<AnimationManager> onComplete){
        //没有这个动画则不播放
        ModelAnimation modelAnimation = animationHandler.getAnimation(animationId);
        if(modelAnimation == null) return false;
        String nowAnimationId = animationHandler.getPlaying();
        if(nowAnimationId != null && (nowAnimationId.equals("spawn") || nowAnimationId.equals("death"))) return false;
        animationHandler.playOnce(animationId, override, ()->{
            onComplete.accept(this);
        });
        Main.getLogger().debug("切换到动画 {}", animationId);
        return true;
    }



}
