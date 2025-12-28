package org.qiuhua.troveserver.arcartx.internal.network.packet.client;

import com.google.gson.annotations.SerializedName;
import lombok.Getter;
import lombok.Setter;
import net.minestom.server.MinecraftServer;
import net.minestom.server.entity.Player;
import org.qiuhua.troveserver.arcartx.core.config.ui.type.UI;
import org.qiuhua.troveserver.arcartx.core.ui.ArcartXUIRegistry;
import org.qiuhua.troveserver.arcartx.core.ui.adapter.ArcartXUI;
import org.qiuhua.troveserver.arcartx.core.ui.adapter.CallBackType;
import org.qiuhua.troveserver.arcartx.event.client.ClientCustomPacketEvent;
import org.qiuhua.troveserver.arcartx.internal.network.packet.PacketBase;
import org.qiuhua.troveserver.arcartx.util.collections.CallData;
import org.qiuhua.troveserver.arcartx.util.collections.UICallBack;

import java.util.List;

public class CPackCustomPacket implements PacketBase {

    /**
     * 数据包ID
     * 用于标识数据包类型
     */
    @SerializedName("id")
    @Getter @Setter
    private String id;

    /**
     * 数据内容
     * 数据包携带的数据列表
     */
    @SerializedName("data")
    @Getter @Setter
    private List<String> data;

    /**
     * 类型
     * 数据包类型（如"ui"表示UI相关）
     */
    @SerializedName("type")
    @Getter @Setter
    private String type;

    /**
     * 名称
     * 目标名称（如UI名称）
     */
    @SerializedName("name")
    @Getter @Setter
    private String name;

    /**
     * 处理数据包的核心方法
     * @param player 玩家对象
     */
    @Override
    public void handle(Player player) {
        //获取数据包ID和数据
        String packetId = this.id;
        if (packetId == null) {
            return;
        }

        List<String> packetData = this.data;
        if (packetData == null) {
            //如果数据为空，使用空列表
            packetData = List.of();
        }

        //触发自定义数据包事件
        ClientCustomPacketEvent clientCustomPacketEvent = new ClientCustomPacketEvent(player, packetId, packetData);
        MinecraftServer.getGlobalEventHandler().call(clientCustomPacketEvent);
        if(clientCustomPacketEvent.isCancelled()) return;

        //处理UI相关的数据包
        if (this.name != null && "ui".equals(this.type)) {
            handleUIPacket(player, packetId, packetData);
        }
    }


    /**
     * 处理UI相关的数据包
     * @param player 玩家对象
     * @param packetId 数据包ID
     * @param packetData 数据包数据
     */
    private void handleUIPacket(Player player, String packetId, List<String> packetData) {
        //获取UI名称
        String uiName = this.name;
        if (uiName == null) {
            return;
        }

        //根据名称获取UI对象
        UI ui = ArcartXUIRegistry.getRegisteredUI().get(uiName);
        if (ui == null) {
            return;
        }

        //检查UI是否有数据包回调
        if (ui.getCallbacks().containsKey(CallBackType.PACKET)) {
            //获取数据包回调列表
            List<UICallBack> packetCallbacks = ui.getCallbacks().get(CallBackType.PACKET);
            if (packetCallbacks != null) {
                //为每个回调执行处理
                for (UICallBack callback : packetCallbacks) {
                    //创建回调数据并执行回调
                    callback.call(new CallData(player, packetId, packetData));
                }
            }
        }
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
