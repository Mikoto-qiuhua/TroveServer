package org.qiuhua.troveserver.arcartx.internal.network.packet.client;

import com.google.gson.annotations.SerializedName;
import lombok.Getter;
import lombok.Setter;
import net.minestom.server.MinecraftServer;
import net.minestom.server.entity.Player;
import net.minestom.server.event.server.ClientPingServerEvent;
import org.qiuhua.troveserver.arcartx.event.client.ClientEntityJoinEvent;
import org.qiuhua.troveserver.arcartx.event.client.ClientEntityLeaveEvent;
import org.qiuhua.troveserver.arcartx.internal.network.packet.PacketBase;

import java.util.UUID;

public class CPackEntityJoin implements PacketBase {




    /**
     * 实体的UUID
     */
    @SerializedName("UUID")
    @Setter
    @Getter
    private UUID uuid;

    /**
     * 是否是加入事件（true=加入，false=离开）
     */
    @SerializedName("isJoin")
    @Setter
    @Getter
    private boolean isJoin;


    /**
     * 处理实体加入/离开事件
     *
     * @param player 发送数据包的玩家
     */
    @Override
    public void handle(Player player) {
        //验证UUID不为空
        if (this.uuid == null) {
            throw new IllegalStateException("UUID 不能为空");
        }

        //根据isJoin值触发不同的事件
        if (this.isJoin) {
            //实体在客户端加入视野
            MinecraftServer.getGlobalEventHandler().call(new ClientEntityJoinEvent(player, this.uuid));

        } else {
            //实体在客户端离开视野
            MinecraftServer.getGlobalEventHandler().call(new ClientEntityLeaveEvent(player, this.uuid));
        }
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
