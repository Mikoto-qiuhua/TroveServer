package org.qiuhua.troveserver.arcartx.util.collections;

import lombok.Getter;
import net.minestom.server.entity.Player;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Objects;

public class CallData {
    @Getter
    private final Player player;          // 玩家对象
    @Getter
    private final String identifier;      // 标识符
    @Getter
    private final List<String> data;      // 数据列表

    /**
     * 构造函数
     * @param player 玩家对象
     * @param identifier 标识符
     * @param data 数据列表
     */
    public CallData(Player player, String identifier, List<String> data) {
        this.player = player;
        this.identifier = identifier;
        this.data = data;
    }


    /**
     * 创建副本（由于Java没有Kotlin的copy方法，这里提供类似的静态方法）
     * @param player 新的玩家对象，如果为null则使用原来的
     * @param identifier 新的标识符，如果为null则使用原来的
     * @param data 新的数据列表，如果为null则使用原来的
     * @return 新的CallData对象
     */
    public CallData copy(Player player, String identifier, List<String> data) {
        return new CallData(
                player != null ? player : this.player,
                identifier != null ? identifier : this.identifier,
                data != null ? data : this.data
        );
    }


    @Override
    public String toString() {
        return "CallData(player=" + player + ", identifier=" + identifier + ", data=" + data + ")";
    }

    @Override
    public int hashCode() {
        int result = player.hashCode();
        result = 31 * result + identifier.hashCode();
        result = 31 * result + data.hashCode();
        return result;
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) return true;
        if (!(other instanceof CallData)) return false;

        CallData callData = (CallData) other;
        return Objects.equals(player, callData.player) &&
                Objects.equals(identifier, callData.identifier) &&
                Objects.equals(data, callData.data);
    }


}
