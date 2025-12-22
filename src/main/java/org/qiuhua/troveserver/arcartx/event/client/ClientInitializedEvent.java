package org.qiuhua.troveserver.arcartx.event.client;

import lombok.Getter;
import net.minestom.server.entity.Player;
import net.minestom.server.event.trait.PlayerEvent;

public class ClientInitializedEvent implements PlayerEvent {


    @Getter
    private final Player player;

    public ClientInitializedEvent(Player player){
        this.player = player;
    }


    /**
     * 客户端初始化开始事件
     * 当客户端开始初始化时触发
     */
    public static class Start extends ClientInitializedEvent {
        public Start(Player player) {
            super(player);
        }
    }


    /**
     * 客户端资源加载完成事件
     * 当客户端资源加载完成时触发
     */
    public static class ResourceLoaded extends ClientInitializedEvent {
        public ResourceLoaded(Player player) {
            super(player);
        }
    }

    /**
     * 客户端初始化结束事件
     * 当客户端初始化完成时触发
     */
    public static class End extends ClientInitializedEvent {
        public End(Player player) {
            super(player);
        }
    }

    /**
     * 客户端重新加载
     * 当客户端重新加载时触发
     */
    public static class Reload extends ClientInitializedEvent {
        public Reload(Player player) {
            super(player);
        }
    }





}
