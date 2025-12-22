package org.qiuhua.troveserver.arcartx.core.entity;

import lombok.Getter;
import net.minestom.server.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.qiuhua.troveserver.arcartx.core.entity.data.ArcartXPlayer;
import org.qiuhua.troveserver.player.RPGPlayer;

import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class ArcartXEntityManager {


    @Getter
    private static final ConcurrentHashMap<UUID, ArcartXPlayer> players = new ConcurrentHashMap<>();

    private static final ConcurrentHashMap<UUID, ArcartXPlayer> entitys = new ConcurrentHashMap<>();


    public static ArcartXPlayer getPlayer(Player player){
        UUID uuid = player.getUuid();
        if(!players.containsKey(uuid)){
            ArcartXPlayer arcartXPlayer = new ArcartXPlayer(player);
            players.put(uuid, arcartXPlayer);
        }
        return players.get(uuid);
    }



    /**
     * 移除玩家对象
     *
     * @param player 要移除的玩家
     */
    public static void removePlayer(Player player) {
        // 从玩家映射表中移除
        players.remove(player.getUuid());

        //对能看到该玩家的所有玩家执行操作
        player.getViewers().forEach(player1 -> {
            //向其他玩家发送控制器设置消息

        });


    }




}
