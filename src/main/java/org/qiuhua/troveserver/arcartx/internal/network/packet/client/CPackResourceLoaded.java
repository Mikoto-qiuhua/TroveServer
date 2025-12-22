package org.qiuhua.troveserver.arcartx.internal.network.packet.client;

import net.minestom.server.MinecraftServer;
import net.minestom.server.entity.Player;
import org.qiuhua.troveserver.arcartx.event.client.ClientInitializedEvent;
import org.qiuhua.troveserver.arcartx.internal.network.packet.PacketBase;

public class CPackResourceLoaded implements PacketBase {

    /**
     * 处理资源加载完成事件
     *
     * @param player 发送数据包的玩家
     */
    @Override
    public void handle(Player player) {
        // 触发客户端资源加载完成事件
        MinecraftServer.getGlobalEventHandler().call(new ClientInitializedEvent.ResourceLoaded(player));
    }

    /**
     * 是否异步执行
     *
     * @return false表示同步执行
     */
    @Override
    public boolean isAsync() {
        return false;
    }


}
