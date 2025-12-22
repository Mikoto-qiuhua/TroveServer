package org.qiuhua.troveserver.arcartx.internal.network.packet.client;

import com.google.gson.annotations.SerializedName;
import lombok.Getter;
import lombok.Setter;
import net.minestom.server.MinecraftServer;
import net.minestom.server.entity.Player;
import org.qiuhua.troveserver.arcartx.event.client.ClientEntityJoinEvent;
import org.qiuhua.troveserver.arcartx.event.client.ClientInitializedEvent;
import org.qiuhua.troveserver.arcartx.internal.network.packet.PacketBase;

import java.util.HashSet;
import java.util.UUID;

public class CPackInitialized implements PacketBase {

    /**
     * 实体UUID集合
     * 客户端已加载的实体列表
     */
    @SerializedName("entity")
    @Getter
    @Setter
    private HashSet<UUID> entities = new HashSet<>();


    /**
     * 是否重新加载
     * true表示客户端重新加载，false表示首次初始化
     */
    @SerializedName("reload")
    @Getter
    @Setter
    private boolean reload;


    /**
     * 处理数据包的核心方法，由具体实现类实现
     *
     * @param player 玩家对象
     */
    @Override
    public void handle(Player player) {
        if (this.reload) {
            //客户端重新加载的情况
            //触发客户端重新加载事件
            MinecraftServer.getGlobalEventHandler().call(new ClientInitializedEvent.Reload(player));
        } else {
            //客户端首次初始化的情况
            //发送世界变更消息给客户端
            //NetworkMessageSender.sendWorldChange(player, player.getWorld());

            //为每个实体触发加入事件
            for (UUID entityId : this.entities) {
                MinecraftServer.getGlobalEventHandler().call(new ClientEntityJoinEvent(player, entityId));
            }

            //触发客户端初始化结束事件
            MinecraftServer.getGlobalEventHandler().call(new ClientInitializedEvent.End(player));
        }
    }

    /**
     * 数据包是否异步处理
     * @return false表示在主线程处理
     */
    @Override
    public boolean isAsync() {
        return false;
    }
}
