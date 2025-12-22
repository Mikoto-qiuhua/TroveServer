package org.qiuhua.troveserver.arcartx.core.ui;

import lombok.Getter;
import net.minestom.server.MinecraftServer;
import org.jetbrains.annotations.Nullable;
import org.qiuhua.troveserver.arcartx.core.config.ui.type.UI;
import org.qiuhua.troveserver.arcartx.core.entity.ArcartXEntityManager;
import org.qiuhua.troveserver.arcartx.core.entity.data.ArcartXPlayer;
import org.qiuhua.troveserver.arcartx.core.ui.adapter.ArcartXUI;
import org.qiuhua.troveserver.arcartx.internal.network.packet.NetworkMessageSender;
import org.qiuhua.troveserver.utils.yaml.file.YamlConfiguration;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.UUID;

public class ArcartXUIRegistry {


    /**
     * 已注册的ui列表
     */
    @Getter
    public static final Map<String, UI> registeredUI = new HashMap<>();

    /**
     * 注册一个新的UI
     * @param id UI的唯一标识符
     * @param config YAML配置对象
     * @return 返回注册的UI对象
     */
    public static ArcartXUI register(String id, YamlConfiguration config){
        UI ui = new UI(id, config);
        registeredUI.put(id, ui);
        return ui;
    }

    /**
     * 取消注册一个UI
     * @param id UI的唯一标识符
     */
    public static void unRegister(String id) {
        registeredUI.remove(id);
    }

    /**
     * 获取指定ID的UI
     * @param id UI的唯一标识符
     * @return 返回UI对象，如果不存在则返回null
     */
    @Nullable
    public static ArcartXUI get(String id) {
        return registeredUI.get(id);
    }

    /**
     * 重新加载指定ID的UI配置
     * @param id UI的唯一标识符
     * @param config 新的YAML配置对象
     */
    public static void reload(String id, YamlConfiguration config) {
        //创建新的UI对象
        UI ui = new UI(id, config);

        //保留旧UI的回调函数
        UI oldUI = registeredUI.get(id);
        if(oldUI != null) {
            ui.getCallbacks().putAll(oldUI.getCallbacks());
        }

        //更新注册表中的UI
        registeredUI.put(id, ui);

        //向所有AX记录的玩家发送新的ui
        Map<UUID, ArcartXPlayer> players = new HashMap<>(ArcartXEntityManager.getPlayers());
        players.values().forEach(arcartXPlayer -> {
            //这里要实现发包更新
            NetworkMessageSender.sendUI(arcartXPlayer.getPlayer(), ui);
        });

    }




}
