package org.qiuhua.troveserver.arcartx.util;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * IO工具类
 * 提供IO操作的便捷方法，特别是资源关闭操作
 */
public final class IOUtils {


    /**
     * 静默关闭可关闭资源
     * 如果关闭过程中发生异常，会被静默忽略
     *
     * @param closeable 要关闭的资源，可以为null
     */
    public static void closeQuietly(AutoCloseable closeable) {
        if (closeable != null) {
            try {
                closeable.close();
            } catch (Exception e) {
                // 静默忽略异常
            }
        }
    }

    /**
     * 静默关闭多个可关闭资源
     *
     * @param closeables 要关闭的资源数组
     */
    public static void closeQuietly(AutoCloseable... closeables) {
        for (AutoCloseable closeable : closeables) {
            closeQuietly(closeable);
        }
    }
}
