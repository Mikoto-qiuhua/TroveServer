package org.qiuhua.troveserver.arcartx.internal.network.packet.server;

import net.minestom.server.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.qiuhua.troveserver.arcartx.internal.network.packet.PacketBase;
import org.qiuhua.troveserver.player.RPGPlayer;

public interface ServerPacketBase extends PacketBase {

    /**
     * 处理服务器数据包的默认实现
     * 默认抛出UnsupportedOperationException，需要子类重写
     *
     * @param player 玩家对象
     */
    @Override
    default void handle(@NotNull Player player) {
        //默认实现 抛出不支持的操作异常，强制子类重写此方法
        throw new UnsupportedOperationException("不支持的操作");
    }

    /**
     * 是否异步执行的默认实现
     * 服务器数据包默认异步执行
     *
     * @return 默认返回true，表示异步执行
     */
    @Override
    default boolean isAsync() {
        return true;
    }



}
