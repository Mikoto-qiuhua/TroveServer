package org.qiuhua.troveserver.arcartx.internal.network.packet.client;

import com.google.gson.annotations.SerializedName;
import lombok.Getter;
import lombok.Setter;
import net.minestom.server.MinecraftServer;
import net.minestom.server.entity.Player;
import org.qiuhua.troveserver.arcartx.core.entity.ArcartXEntityManager;
import org.qiuhua.troveserver.arcartx.core.entity.data.ArcartXPlayer;
import org.qiuhua.troveserver.arcartx.core.ui.ArcartXUIRegistry;
import org.qiuhua.troveserver.arcartx.core.ui.adapter.ArcartXUI;
import org.qiuhua.troveserver.arcartx.core.ui.adapter.CallBackType;
import org.qiuhua.troveserver.arcartx.event.client.ClientHudCloseEvent;
import org.qiuhua.troveserver.arcartx.event.client.ClientHudOpenEvent;
import org.qiuhua.troveserver.arcartx.event.client.ClientMenuCloseEvent;
import org.qiuhua.troveserver.arcartx.event.client.ClientMenuOpenEvent;
import org.qiuhua.troveserver.arcartx.internal.network.packet.PacketBase;
import org.qiuhua.troveserver.arcartx.util.collections.CallBack;
import org.qiuhua.troveserver.arcartx.util.collections.CallData;
import org.qiuhua.troveserver.arcartx.util.collections.UICallBack;

import java.util.List;

public class CPackDoWithScreen implements PacketBase {

    /**
     * UI名称
     * 要操作的UI的标识符
     */
    @SerializedName("name")
    @Getter @Setter
    private String name;

    /**
     * 操作命令
     * "open"表示打开，"close"表示关闭
     */
    @SerializedName("cmd")
    @Getter @Setter
    private String cmd;

    /**
     * 类型
     * "menu"表示菜单，"hud"表示HUD
     */
    @SerializedName("type")
    @Getter @Setter
    private String type;


    /**
     * 处理数据包的核心方法，由具体实现类实现
     *
     * @param player 玩家对象
     */
    @Override
    public void handle(Player player) {
        //获取UI名称
        String uiName = this.name;
        //根据名称获取UI对象
        ArcartXUI ui = ArcartXUIRegistry.get(uiName);
        if(ui == null) return;

        //处理打开操作
        if ("open".equals(this.cmd)) {
            handleOpen(player, ui);
            //处理关闭操作
        } else if ("close".equals(this.cmd)) {
            handleClose(player, ui);
        }
    }

    /**
     * 处理打开UI操作
     * @param player 玩家对象
     * @param ui UI对象
     */
    private void handleOpen(Player player, ArcartXUI ui) {
        // 根据类型触发不同的打开事件
        if ("menu".equals(this.type)) {
            //触发菜单打开事件
            MinecraftServer.getGlobalEventHandler().call(new ClientMenuOpenEvent(player, ui));
        } else {
            //触发HUD打开事件
            MinecraftServer.getGlobalEventHandler().call(new ClientHudOpenEvent(player, ui));
        }

        //获取玩家对象
        ArcartXPlayer arcartXPlayer = ArcartXEntityManager.getPlayer(player);
        if (arcartXPlayer != null) {
            // 执行打开回调
            CallBack callBack = arcartXPlayer.getCallbacks().remove("OpenCallBack:" + ui.getId());
            if (callBack != null) {
                callBack.call();
            }
        }

        // 执行UI的打开回调函数
        List<UICallBack> openCallbacks = ui.getCallbacks().get(CallBackType.OPEN);
        if (openCallbacks != null) {
            for (UICallBack callback : openCallbacks) {
                // 创建回调数据并执行回调
                callback.call(new CallData(player, "", List.of()));
            }
        }

    }

    /**
     * 处理关闭UI操作
     * @param player 玩家对象
     * @param ui UI对象
     */
    private void handleClose(Player player, ArcartXUI ui) {
        //根据类型触发不同的关闭事件
        if ("menu".equals(this.type)) {
            //触发菜单关闭事件
            MinecraftServer.getGlobalEventHandler().call(new ClientMenuCloseEvent(player, ui));
        } else {
            //触发HUD关闭事件
            MinecraftServer.getGlobalEventHandler().call(new ClientHudCloseEvent(player, ui));
        }

        // 执行UI的关闭回调函数
        List<UICallBack> closeCallbacks = ui.getCallbacks().get(CallBackType.CLOSE);
        if (closeCallbacks != null) {
            for (UICallBack callback : closeCallbacks) {
                // 创建回调数据并执行回调
                callback.call(new CallData(player, "", List.of()));
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
