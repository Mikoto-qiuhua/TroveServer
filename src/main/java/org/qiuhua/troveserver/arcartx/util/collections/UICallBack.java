package org.qiuhua.troveserver.arcartx.util.collections;

/**
 * UI回调接口 - 用于处理UI相关事件的回调函数
 * 与CallBack接口不同，这个接口接收CallData作为参数
 */
public interface UICallBack {

    /**
     * 执行UI回调函数
     * @param event 回调事件数据，包含玩家、标识符和相关数据
     */
    void call(CallData event);
}
