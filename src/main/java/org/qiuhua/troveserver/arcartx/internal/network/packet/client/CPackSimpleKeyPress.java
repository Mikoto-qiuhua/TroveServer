package org.qiuhua.troveserver.arcartx.internal.network.packet.client;

import com.google.gson.annotations.SerializedName;
import lombok.Getter;
import lombok.Setter;
import net.minestom.server.MinecraftServer;
import net.minestom.server.entity.Player;
import org.qiuhua.troveserver.Main;
import org.qiuhua.troveserver.arcartx.core.config.key.simple.SimpleKeyElement;
import org.qiuhua.troveserver.arcartx.core.config.key.simple.SimpleKeyFolder;
import org.qiuhua.troveserver.arcartx.event.client.ClientSimpleKeyPressEvent;
import org.qiuhua.troveserver.arcartx.event.client.ClientSimpleKeyReleaseEvent;
import org.qiuhua.troveserver.arcartx.internal.network.packet.PacketBase;

public class CPackSimpleKeyPress implements PacketBase {

    /**
     * 按键ID
     */
    @SerializedName("name")
    @Getter
    @Setter
    private String name;

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
        SimpleKeyElement simpleKeyElement = SimpleKeyFolder.simpleKey.get(this.name);
        if(simpleKeyElement == null) return;
        //处理按键按下事件
        if(this.isDown) {
            //触发按键按下事件
            ClientSimpleKeyPressEvent clientSimpleKeyPressEvent = new ClientSimpleKeyPressEvent(player, this.name);
            MinecraftServer.getGlobalEventHandler().call(clientSimpleKeyPressEvent);
            if(clientSimpleKeyPressEvent.isCancelled()) return;
            //执行按键按下回调
            if(simpleKeyElement.getCallBack() != null) {
                simpleKeyElement.getCallBack().onPress(player);
            }
        } else {
            //触发按键释放事件
            ClientSimpleKeyReleaseEvent clientSimpleKeyReleaseEvent = new ClientSimpleKeyReleaseEvent(player, this.name);
            MinecraftServer.getGlobalEventHandler().call(clientSimpleKeyReleaseEvent);
            if(clientSimpleKeyReleaseEvent.isCancelled()) return;
            //执行按键释放回调
            if(simpleKeyElement.getCallBack() != null) {
                simpleKeyElement.getCallBack().onRelease(player);
            }
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
