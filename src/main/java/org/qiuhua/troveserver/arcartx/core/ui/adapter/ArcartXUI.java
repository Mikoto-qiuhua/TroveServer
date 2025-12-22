package org.qiuhua.troveserver.arcartx.core.ui.adapter;

import net.minestom.server.entity.Player;
import org.qiuhua.troveserver.Main;
import org.qiuhua.troveserver.arcartx.core.entity.ArcartXEntityManager;
import org.qiuhua.troveserver.arcartx.core.entity.data.ArcartXPlayer;
import org.qiuhua.troveserver.arcartx.internal.network.packet.NetworkMessageSender;
import org.qiuhua.troveserver.arcartx.util.collections.CallBack;
import org.qiuhua.troveserver.arcartx.util.collections.UICallBack;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public interface ArcartXUI {

    Map<CallBackType, List<UICallBack>> getCallbacks();

    String getId();

    /**
     * 注册回调函数
     * @param type 回调类型
     * @param callBack 回调函数
     */
    default void registerCallBack(CallBackType type, UICallBack callBack) {

        // 如果该回调类型还没有列表，创建一个新的列表
        if(!getCallbacks().containsKey(type)) {
            getCallbacks().put(type, new ArrayList<>());
        }

        //获取对应类型的回调列表并添加回调函数
        List<UICallBack> list = getCallbacks().get(type);
        list.add(callBack);
    }

    /**
     * 打开UI给指定玩家
     * @param player 玩家对象
     */
    default void open(Player player) {
        //发送打开UI的网络消息
        NetworkMessageSender.sendOpenUI(player, getId());
    }


    /**
     * 打开UI给指定玩家，并设置回调函数
     * @param player 玩家对象
     * @param callBack 回调函数
     */
    default void open(Player player, CallBack callBack) {
        //获取玩家的ArcartXPlayer对象
        ArcartXPlayer arcartXPlayer = ArcartXEntityManager.getPlayer(player);
        //将回调函数注册到玩家数据中
        arcartXPlayer.getCallbacks().put("OpenCallBack:" + getId(), callBack);
        //发送打开UI的网络消息
        NetworkMessageSender.sendOpenUI(player, getId());
    }

    /**
     * 关闭指定玩家的UI
     * @param player 玩家对象
     */
    default void close(Player player) {
        //发送关闭UI的网络消息
        NetworkMessageSender.sendCloseUI(player, getId());
    }

    /**
     * 向指定玩家发送UI数据包
     * @param player 玩家对象
     * @param handlerName 处理器名称
     * @param packet 数据包对象
     */
    default void sendPacket(Player player, String handlerName, Object packet) {
        //发送屏幕数据包
        NetworkMessageSender.sendScreenPacket(player, handlerName, packet, getId());

    }

    /**
     * 在客户端的UI中运行代码
     * @param player 玩家对象
     * @param code 要运行的代码
     */
    default void run(Player player, String code) {
        //发送运行代码的网络消息
        NetworkMessageSender.sendUIRunCode(player, getId(), code);
    }






}
