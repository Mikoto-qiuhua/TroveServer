package org.qiuhua.troveserver.arcartx.internal.network.encryptor;

import lombok.Getter;
import lombok.Setter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.crypto.Cipher;
import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import java.security.Key;
import java.util.Locale;

public class AESEncryptor {

    /**
     * 伴生对象，提供十六进制字符串转换的静态方法
     */
    public static class Companion {
        // 私有构造函数
        private Companion() {}

        /**
         * 字节数组转换为十六进制字符串
         *
         * @param buf 字节数组
         * @return 十六进制字符串（大写）
         */
        public String parseByte2HexStr(byte [] buf) {
            StringBuffer sb = new StringBuffer();
            int length = buf.length;
            for (int i = 0; i < length; ++i) {
                // 将字节转换为十六进制字符串（0-FF）
                String hex = Integer.toHexString(buf[i] & 0xFF);
                // 如果只有一位，前面补0
                if (hex.length() == 1) {
                    hex = "0" + hex;
                }
                // 转换为大写并追加到字符串缓冲区
                String upperHex = hex.toUpperCase(Locale.getDefault());
                sb.append(upperHex);
            }
            return sb.toString();
        }

        /**
         * 十六进制字符串转换为字节数组
         *
         * @param hexStr 十六进制字符串
         * @return 字节数组，如果字符串为空则返回null
         */
        public byte [] parseHexStr2Byte(String hexStr) {
            // 如果字符串为空，返回null
            if (hexStr.isEmpty()) {
                return null;
            }

            // 每两个十六进制字符表示一个字节
            byte[] result = new byte[hexStr.length() / 2];
            int length = hexStr.length() / 2;

            for (int i = 0; i < length; ++i) {
                // 获取高4位字符
                String highStr = hexStr.substring(i * 2, i * 2 + 1);
                // 获取低4位字符
                String lowStr = hexStr.substring(i * 2 + 1, i * 2 + 2);

                // 将十六进制字符转换为十进制数值
                int high = Integer.parseInt(highStr, 16);
                int low = Integer.parseInt(lowStr, 16);

                // 组合成字节
                result[i] = (byte)(high * 16 + low);
            }
            return result;
        }

    }

    // 伴生对象实例
    public static Companion Companion = new Companion();

    // AES密钥（有状态，必须先设置）
    @Setter
    @Getter
    private Key aesKey;

    /**
     * 使用AES解密字节数组
     *
     * @param input 待解密的字节数组
     * @return 解密后的字节数组
     * @throws GeneralSecurityException 如果解密失败
     */
    private byte[] decryptAES(byte[] input) throws GeneralSecurityException {
        // 获取AES/ECB/PKCS5Padding模式的Cipher实例
        Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
        // 初始化为解密模式
        cipher.init(Cipher.DECRYPT_MODE, this.aesKey);
        // 执行解密
        return cipher.doFinal(input);
    }

    /**
     * 使用AES加密字节数组
     *
     * @param input 待加密的字节数组
     * @return 加密后的字节数组
     * @throws GeneralSecurityException 如果加密失败
     */
    private byte[] encryptAES(byte[] input) throws GeneralSecurityException {
        // 获取AES/ECB/PKCS5Padding模式的Cipher实例
        Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
        // 初始化为加密模式
        cipher.init(Cipher.ENCRYPT_MODE, this.aesKey);
        // 执行加密
        return cipher.doFinal(input);
    }


    /**
     * 加密字符串
     * 将字符串转换为UTF-8字节数组，进行AES加密，再转换为十六进制字符串
     *
     * @param data 待加密的字符串
     * @return 加密后的十六进制字符串，如果加密失败返回null
     */
    public String encode(String data) {
        try {
            // 将字符串转换为UTF-8字节数组
            byte[] dataBytes = data.getBytes(StandardCharsets.UTF_8);
            // 使用AES加密
            byte[] encryptedBytes = encryptAES(dataBytes);
            // 将加密后的字节数组转换为十六进制字符串
            return Companion.parseByte2HexStr(encryptedBytes);
        } catch (GeneralSecurityException e) {
            // 加密失败，返回null
            return null;
        }
    }

    /**
     * 解密字符串
     * 将十六进制字符串转换为字节数组，进行AES解密，再转换为UTF-8字符串
     *
     * @param data 待解密的十六进制字符串
     * @return 解密后的字符串，如果解密失败返回null
     */
    public String decode(String data) {
        try {
            // 将十六进制字符串转换为字节数组
            byte[] encryptedBytes = Companion.parseHexStr2Byte(data);
            // 使用AES解密
            byte[] decryptedBytes = decryptAES(encryptedBytes);
            // 将解密后的字节数组转换为UTF-8字符串
            return new String(decryptedBytes, StandardCharsets.UTF_8);
        } catch (GeneralSecurityException e) {
            // 解密失败，返回null
            return null;
        }
    }



}
