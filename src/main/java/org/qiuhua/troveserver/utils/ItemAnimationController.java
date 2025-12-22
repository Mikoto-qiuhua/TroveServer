package org.qiuhua.troveserver.utils;

import lombok.Getter;
import net.minestom.server.item.ItemStack;
import net.minestom.server.item.Material;
import org.qiuhua.troveserver.module.item.ItemManager;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

public class ItemAnimationController {
    private final List<ItemStack> frames = new ArrayList<>();
    // 获取总时长
    @Getter
    private final long totalDuration; // 总播放时长（tick数）
    // 获取每帧时长
    @Getter
    private final long frameDuration; // 每帧显示时长（tick数）

    /**
     * @param totalDuration 完整播放一次的总时长（tick数）
     * @param modelId 模型id
     */
    public ItemAnimationController(long totalDuration, String... modelId) {
        this.totalDuration = totalDuration;
        for (String id : modelId) {
            ItemStack item = ItemManager.setItemModel(ItemStack.builder(Material.PAPER), id).build();
            frames.add(item);
        }
        this.frameDuration = totalDuration / frames.size(); // 自动计算每帧时长
    }

    public ItemStack getFrame(long tick) {
        // 计算在当前循环中的位置
        long cycleTime = tick % totalDuration;
        // 计算当前帧索引
        int frameIndex = (int)((cycleTime * frames.size()) / totalDuration);
        return frames.get(frameIndex);
    }

}
