package org.qiuhua.troveserver.arcartx.internal.network.packet.client;

import com.google.gson.annotations.SerializedName;
import lombok.Getter;
import lombok.Setter;
import net.minestom.server.MinecraftServer;
import net.minestom.server.entity.Player;
import org.qiuhua.troveserver.arcartx.core.config.key.client.ClientKeyElement;
import org.qiuhua.troveserver.arcartx.core.config.key.client.ClientKeyFolder;
import org.qiuhua.troveserver.arcartx.event.client.ClientKeyPressEvent;
import org.qiuhua.troveserver.arcartx.event.client.ClientKeyReleaseEvent;
import org.qiuhua.troveserver.arcartx.event.client.ClientMouseClickEvent;
import org.qiuhua.troveserver.arcartx.internal.network.packet.PacketBase;

/**
 * 客户端鼠标点击数据包
 */
public class CPacketMouseClick implements PacketBase {
    /**
     * 0=左键 1=右键
     */
    @SerializedName(value="id")
    @Getter @Setter
    private int id;

    /**
     * 0=按下 1=弹起
     */
    @SerializedName(value="action")
    @Getter @Setter
    private int action;



    /**
     * 处理数据包的核心方法，由具体实现类实现
     *
     * @param player 玩家对象
     */
    @Override
    public void handle(Player player) {
        MinecraftServer.getGlobalEventHandler().call(new ClientMouseClickEvent(player, id, action));
    }

    /**
     * 是否异步执行
     * @return true表示异步执行
     */
    @Override
    public boolean isAsync() {
        return false;
    }

}
