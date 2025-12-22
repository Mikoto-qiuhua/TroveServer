package org.qiuhua.troveserver.arcartx.event.client;

import lombok.Getter;
import net.minestom.server.entity.Player;
import net.minestom.server.event.trait.PlayerEvent;

import java.util.List;
import java.util.UUID;
import java.util.regex.Pattern;

public class ClientCustomPacketEvent implements PlayerEvent {

    @Getter
    private final Player player;

    /**
     * 数据包ID
     */
    @Getter
    private final String id;

    /**
     * 数据内容
     */
    @Getter
    private final List<String> data;

    /**
     * 参数数量
     */
    @Getter
    private final int argSize;

    /**
     * 主构造函数
     * @param player 玩家对象
     * @param id 数据包ID
     * @param data 数据列表
     * @param argSize 参数数量
     */
    public ClientCustomPacketEvent(Player player, String id, List<String> data, int argSize) {
        this.player = player;
        this.id = id;
        this.data = data;
        this.argSize = argSize;
    }

    /**
     * 简化构造函数（自动计算参数数量）
     * @param player 玩家对象
     * @param id 数据包ID
     * @param data 数据列表
     */
    public ClientCustomPacketEvent(Player player, String id, List<String> data) {
        this(player, id, data, data.size());
    }

    /**
     * 获取指定索引的参数（字符串形式）
     * 如果索引越界，返回空字符串
     * @param index 参数索引
     * @return 参数值（字符串）
     */
    public String getArg(int index) {
        if (!hasArg(index)) {
            return "";
        }
        return data.get(index);
    }

    /**
     * 获取指定索引的参数（字符串形式）
     * @param index 参数索引
     * @return 参数值（字符串）
     */
    public String getArgAsString(int index) {
        return getArg(index);
    }

    /**
     * 获取指定索引的参数（整数形式）
     * 如果转换失败，返回0
     * @param index 参数索引
     * @return 参数值（整数）
     */
    public int getArgAsInt(int index) {
        try {
            String val = getArgAsString(index);
            if (val.contains(".")) {
                // 处理浮点数，只取整数部分
                String[] parts = val.split(Pattern.quote("."));
                return Integer.parseInt(parts[0]);
            } else {
                return Integer.parseInt(val);
            }
        } catch (NumberFormatException exception) {
            return 0;
        }
    }

    /**
     * 获取指定索引的参数（双精度浮点数形式）
     * 如果转换失败，返回0.0
     * @param index 参数索引
     * @return 参数值（双精度浮点数）
     */
    public double getArgAsDouble(int index) {
        try {
            String val = getArgAsString(index);
            return Double.parseDouble(val);
        } catch (NumberFormatException exception) {
            return 0.0;
        }
    }

    /**
     * 获取指定索引的参数（布尔值形式）
     * 如果转换失败，返回false
     * @param index 参数索引
     * @return 参数值（布尔值）
     */
    public boolean getArgAsBoolean(int index) {
        try {
            String val = getArgAsString(index);
            return Boolean.parseBoolean(val);
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * 获取指定索引的参数（长整数形式）
     * 如果转换失败，返回0L
     * @param index 参数索引
     * @return 参数值（长整数）
     */
    public long getArgAsLong(int index) {
        try {
            String val = getArgAsString(index);
            return Long.parseLong(val);
        } catch (NumberFormatException exception) {
            return 0L;
        }
    }

    /**
     * 获取指定索引的参数（单精度浮点数形式）
     * 如果转换失败，返回0.0f
     * @param index 参数索引
     * @return 参数值（单精度浮点数）
     */
    public float getArgAsFloat(int index) {
        try {
            String val = getArgAsString(index);
            return Float.parseFloat(val);
        } catch (NumberFormatException exception) {
            return 0.0f;
        }
    }

    /**
     * 获取指定索引的参数（UUID形式）
     * 如果转换失败，返回null
     * @param index 参数索引
     * @return 参数值（UUID）
     */
    public UUID getArgAsUUID(int index) {
        try {
            String val = getArgAsString(index);
            return UUID.fromString(val);
        } catch (Exception exception) {
            return null;
        }
    }


    /**
     * 检查索引是否有效
     * @param index 参数索引
     * @return 索引是否有效
     */
    public boolean hasArg(int index) {
        return index >= 0 && index < argSize;
    }

    /**
     * 获取所有参数的字符串表示
     * @return 所有参数连接的字符串
     */
    public String getArgsAsString() {
        return String.join(" ", data);
    }

}
