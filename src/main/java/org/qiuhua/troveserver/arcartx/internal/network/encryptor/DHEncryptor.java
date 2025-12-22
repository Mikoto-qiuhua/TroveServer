package org.qiuhua.troveserver.arcartx.internal.network.encryptor;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.crypto.KeyAgreement;
import javax.crypto.SecretKey;
import java.security.*;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

/**
 * Diffie-Hellman 加密器单例类
 * 用于生成DH密钥对和协商共享密钥
 */
public class DHEncryptor {

    /**
     * 生成DH密钥对
     *
     * @return DH密钥对，如果生成失败则返回null
     */
    public static KeyPair generateKeyPair() {
        try {
            // 获取DH密钥对生成器
            KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("DH");
            // 初始化密钥长度为1024位
            keyPairGenerator.initialize(1024);
            // 生成密钥对
            return keyPairGenerator.generateKeyPair();
        } catch (NoSuchAlgorithmException e) {
            // 打印异常堆栈信息
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 根据公钥和私钥生成共享密钥
     * 使用Diffie-Hellman密钥协商协议
     *
     * @param publicKey 对方公钥的字节数组
     * @param privateKey 己方私钥的字节数组
     * @return 生成的共享密钥（AES格式）
     * @throws RuntimeException 如果密钥协商过程中发生异常
     */
    public static SecretKey getSecretKey(byte[] publicKey, byte[] privateKey) {
        try {
            // 设置系统属性，启用旧版密钥派生函数以兼容
            System.getProperties().setProperty("jdk.crypto.KeyAgreement.legacyKDF", "true");

            // 获取DH算法的KeyFactory实例
            KeyFactory keyFactory = KeyFactory.getInstance("DH");

            // 从字节数组重建公钥（使用X509编码规范）
            X509EncodedKeySpec pubKeySpec = new X509EncodedKeySpec(publicKey);
            PublicKey pubKey = keyFactory.generatePublic(pubKeySpec);

            // 从字节数组重建私钥（使用PKCS8编码规范）
            PKCS8EncodedKeySpec priKeySpec = new PKCS8EncodedKeySpec(privateKey);
            PrivateKey priKey = keyFactory.generatePrivate(priKeySpec);

            // 创建DH密钥协商实例
            KeyAgreement keyAgreement = KeyAgreement.getInstance("DH");

            // 使用己方私钥初始化密钥协商
            keyAgreement.init(priKey);

            // 执行密钥协商阶段，传入对方公钥
            keyAgreement.doPhase(pubKey, true);

            // 生成共享密钥，使用AES算法
            SecretKey secretKey = keyAgreement.generateSecret("AES");
            return secretKey;
        } catch (Exception e) {
            // 将检查异常包装为运行时异常抛出
            throw new RuntimeException(e);
        }
    }
}
