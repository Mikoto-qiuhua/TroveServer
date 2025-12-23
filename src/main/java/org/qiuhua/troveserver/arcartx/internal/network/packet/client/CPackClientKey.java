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
import org.qiuhua.troveserver.arcartx.internal.network.packet.PacketBase;

/**
 * 客户端按键数据包
 * 处理客户端发送的按键按下/释放事件
 */
public class CPackClientKey implements PacketBase {

    /**
     * 按键ID
     */
    @SerializedName("keyID")
    @Getter @Setter
    private String keyID;

    /**
     * 按键状态（按下/释放）
     */
    @SerializedName("isDown")
    @Getter @Setter
    private boolean isDown;



    /**
     * 处理数据包的核心方法，由具体实现类实现
     *
     * @param player 玩家对象
     */
    @Override
    public void handle(Player player) {
        //获取按键配置
        ClientKeyElement clientKeyElement = ClientKeyFolder.clientKey.get(this.keyID);
        if(clientKeyElement == null) return;
        //处理按键按下事件
        if(this.isDown) {
            //触发按键按下事件
            ClientKeyPressEvent clientKeyPressEvent = new ClientKeyPressEvent(player, this.keyID);
            MinecraftServer.getGlobalEventHandler().call(clientKeyPressEvent);
            if(clientKeyPressEvent.isCancelled()) return;
            //执行按键按下回调
            if(clientKeyElement.getCallBack() != null) {
                clientKeyElement.getCallBack().onPress(player);
            }
        } else {
            //触发按键释放事件
            ClientKeyReleaseEvent clientKeyReleaseEvent = new ClientKeyReleaseEvent(player, this.keyID);
            MinecraftServer.getGlobalEventHandler().call(clientKeyReleaseEvent);
            if(clientKeyReleaseEvent.isCancelled()) return;
            //执行按键释放回调
            if(clientKeyElement.getCallBack() != null) {
                clientKeyElement.getCallBack().onRelease(player);
            }
        }

    }
}
