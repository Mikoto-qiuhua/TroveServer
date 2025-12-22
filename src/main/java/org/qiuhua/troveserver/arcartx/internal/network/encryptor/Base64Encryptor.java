package org.qiuhua.troveserver.arcartx.internal.network.encryptor;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

public class Base64Encryptor {

    /**
     * 对字符串进行Base64编码（使用UTF-8字符集）
     *
     * @param data 要编码的字符串
     * @return Base64编码后的字符串
     */
    public static String base64Encrypt(String data) {
        // 使用Base64编码器
        Base64.Encoder encoder = Base64.getEncoder();
        // 将字符串转换为UTF-8字节数组
        byte[] dataBytes = data.getBytes(StandardCharsets.UTF_8);
        // 对字节数组进行Base64编码
        byte[] encodedBytes = encoder.encode(dataBytes);
        // 将编码后的字节数组转换回字符串
        return new String(encodedBytes, StandardCharsets.UTF_8);
    }

    /**
     * 对字节数组进行Base64编码
     *
     * @param data 要编码的字节数组
     * @return Base64编码后的字符串
     */
    public static String base64Encrypt(byte[] data) {
        // 对字节数组进行Base64编码
        byte[] encodedBytes = Base64.getEncoder().encode(data);
        // 将编码后的字节数组转换为UTF-8字符串
        return new String(encodedBytes, StandardCharsets.UTF_8);
    }

    /**
     * 对Base64编码的字符串进行解码
     *
     * @param data Base64编码的字符串
     * @return 解码后的原始字符串
     */
    public static String base64Decrypt(String data) {
        // 对Base64字符串进行解码，得到原始字节数组
        byte[] resultBytes = Base64.getDecoder().decode(data);
        // 将解码后的字节数组转换为UTF-8字符串
        return new String(resultBytes, StandardCharsets.UTF_8);
    }


    /**
     * 对Base64编码的字符串进行解码，返回字节数组
     *
     * @param data Base64编码的字符串
     * @return 解码后的字节数组
     */
    public static byte[] base64DecryptByte(String data) {
        // 对Base64字符串进行解码，直接返回字节数组
        byte[] decodedBytes = Base64.getDecoder().decode(data);
        return decodedBytes;
    }


}
