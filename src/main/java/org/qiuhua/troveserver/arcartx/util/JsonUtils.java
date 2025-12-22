package org.qiuhua.troveserver.arcartx.util;

import com.google.gson.Gson;
import org.jetbrains.annotations.NotNull;

/**
 * JSON工具类
 * 提供JSON序列化和反序列化的便捷方法
 */
public class JsonUtils {


    /**
     * Gson实例
     */
    private static final Gson gson = new Gson();



    /**
     * 将对象序列化为JSON字符串
     *
     * @param object 要序列化的对象
     * @return JSON字符串
     */
    public static String toJson(Object object) {
        return gson.toJson(object);
    }

    /**
     * 将JSON字符串反序列化为指定类型的对象
     *
     * @param <T> 目标类型
     * @param json JSON字符串
     * @param clazz 目标类的Class对象
     * @return 反序列化后的对象
     */
    public static <T> T fromJson(String json, Class<T> clazz) {
        return gson.fromJson(json, clazz);
    }
}
