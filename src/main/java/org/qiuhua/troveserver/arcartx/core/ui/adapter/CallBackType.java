package org.qiuhua.troveserver.arcartx.core.ui.adapter;

/**
 * 回调类型枚举
 * 定义了UI系统中可能的回调事件类型
 */
public enum CallBackType {

    /**
     * 数据包回调 - 当收到来自客户端的数据包时触发
     */
    PACKET,

    /**
     * 打开回调 - 当UI被打开时触发
     */
    OPEN,

    /**
     * 关闭回调 - 当UI被关闭时触发
     */
    CLOSE
}