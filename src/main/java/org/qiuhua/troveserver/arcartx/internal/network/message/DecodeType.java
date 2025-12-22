package org.qiuhua.troveserver.arcartx.internal.network.message;

/**
 * 解码类型枚举
 * 定义消息的解码/加密方式
 */
public enum DecodeType {
    /**
     * 普通解码方式（可能是Base64）
     * ID: 1
     */
    NORMAL(1),

    /**
     * AES加密方式
     * ID: 3
     */
    AES(3);

    private final int id;

    /**
     * 私有构造函数
     *
     * @param id 枚举对应的ID值
     */
    DecodeType(int id) {
        this.id = id;
    }

    /**
     * 获取枚举的ID值
     *
     * @return 枚举ID
     */
    public int getId() {
        return this.id;
    }
}