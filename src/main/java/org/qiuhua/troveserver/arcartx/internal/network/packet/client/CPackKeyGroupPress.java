package org.qiuhua.troveserver.arcartx.internal.network.packet.client;

import com.google.gson.annotations.SerializedName;
import lombok.Getter;
import lombok.Setter;
import net.minestom.server.MinecraftServer;
import net.minestom.server.entity.Player;
import org.qiuhua.troveserver.arcartx.core.config.key.group.KeyGroupElement;
import org.qiuhua.troveserver.arcartx.core.config.key.group.KeyGroupFolder;
import org.qiuhua.troveserver.arcartx.event.client.ClientKeyGroupPressEvent;
import org.qiuhua.troveserver.arcartx.internal.network.packet.PacketBase;

public class CPackKeyGroupPress implements PacketBase {


    /**
     * 按键组合名称
     */
    @SerializedName("group")
    @Setter @Getter
    private String group;



    /**
     * 处理数据包的核心方法，由具体实现类实现
     *
     * @param player 玩家对象
     */
    @Override
    public void handle(Player player) {
        // 获取按键组合配置元素
        KeyGroupElement keyGroupElement = KeyGroupFolder.keyGroups.get(this.group);
        //如果按键组合配置存在 就触发事件
        if (keyGroupElement != null) {
            ClientKeyGroupPressEvent clientKeyGroupPressEvent = new ClientKeyGroupPressEvent(player, this.group);
            MinecraftServer.getGlobalEventHandler().call(clientKeyGroupPressEvent);
            if(clientKeyGroupPressEvent.isCancelled()) return;
            keyGroupElement.getCallBack().onPress(player);
        }

    }

    /**
     * 判断是否为异步处理
     *
     * @return 返回false，表示同步处理
     */
    @Override
    public boolean isAsync() {
        return false;
    }
}
