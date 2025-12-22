package org.qiuhua.troveserver.arcartx.internal.network.packet;

import net.minestom.server.entity.Player;
import org.qiuhua.troveserver.Main;

public interface PacketBase {

    /**
     * 处理数据包的核心方法，由具体实现类实现
     * @param player 玩家对象
     */
    void handle(Player player);

    /**
     * 是否异步执行，默认true
     * @return 是否异步
     */
    default boolean isAsync() {
        return true;
    }

    /**
     * 运行数据包处理逻辑
     * 根据isAsync()决定同步还是异步执行
     * @param player 玩家对象
     */
    default void run(Player player) {
        if (isAsync()) {
            Main.getLogger().debug("异步数据包处理");
        } else {
            Main.getLogger().debug("同步数据包处理");
        }
    }

}
