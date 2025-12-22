package org.qiuhua.troveserver.entity;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import net.minestom.server.coordinate.Pos;
import net.minestom.server.coordinate.Vec;
import net.minestom.server.entity.ItemEntity;
import net.minestom.server.event.item.PickupItemEvent;
import net.minestom.server.instance.Instance;
import net.minestom.server.item.ItemStack;
import net.minestom.server.utils.time.TimeUnit;
import org.qiuhua.troveserver.Main;
import org.qiuhua.troveserver.api.loot.AbstractLootReward;
import org.qiuhua.troveserver.player.RPGPlayer;

import java.time.Duration;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;

public class DropItemEntity extends ItemEntity {

    /**
     * 物品的可见玩家
     */
    private final Set<RPGPlayer> viewerPlayers;


    /**
     * 生成的掉落物是否随机弹出
     */
    @Setter
    @Getter
    @Accessors(chain = true)
    private boolean isSputtering = false;

    /**
     * 这个掉落物绑定的奖励对象
     */
    @Setter
    @Getter
    @Accessors(chain = true)
    private AbstractLootReward lootReward;

    /**
     * 这个实体的持续时间
     * null为永久持续
     */
    @Getter
    @Setter
    @Accessors(chain=true)
    private Integer removeDuration = 1200;

    public DropItemEntity(ItemStack itemStack, Set<RPGPlayer> viewerPlayers) {
        super(itemStack);
        this.viewerPlayers = viewerPlayers;
        setAutoViewable(false);
        setPickupDelay(Duration.of(20, TimeUnit.SERVER_TICK));
    }


    public DropItemEntity(ItemStack itemStack) {
        super(itemStack);
        this.viewerPlayers = Set.of();
        setPickupDelay(Duration.of(20, TimeUnit.SERVER_TICK));
    }



    /**
     * @param instance
     * @param pos
     */
    public void spawnEntity(Instance instance, Pos pos) {
        setInstance(instance, pos);
        viewerPlayers.forEach(this::addViewer);
        if(isSputtering){
            applyRandomVelocity();
        }
    }


    /**
     * 这里重写了更新实体
     * 实现倒计时移除 当duration=0时将实体移除
     * @param time
     */
    @Override
    public void update(long time){
        if(removeDuration != null){
            if(removeDuration <= 0){
                remove();
                Main.getLogger().debug("实体 {} 因已达到存活时间而卸载", this);
                return;
            }else {
                removeDuration--;
            }
        }
        super.update(time);
    }



    /**
     * 应用随机速度（球形随机分布）
     */
    private void applyRandomVelocity() {
        // 方法1: 球形随机分布（更自然）
        double theta = ThreadLocalRandom.current().nextDouble() * 2 * Math.PI; // 0-360度
        double phi = ThreadLocalRandom.current().nextDouble() * Math.PI / 4; // 限制垂直角度（0-45度）
        // 随机速度大小（0.15-0.35）
        double speed = 1 + ThreadLocalRandom.current().nextDouble() * 1.8;
        // 计算XYZ分量
        double x = speed * Math.sin(phi) * Math.cos(theta);
        double y = speed * Math.cos(phi) + 1;
        double z = speed * Math.sin(phi) * Math.sin(theta);
        setVelocity(new Vec(x, y, z));
    }



}
