package org.qiuhua.troveserver.arcartx.internal.network.packet.server;

import com.google.gson.annotations.SerializedName;
import lombok.Getter;
import net.minestom.server.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.qiuhua.troveserver.arcartx.core.entity.ArcartXEntityManager;
import org.qiuhua.troveserver.arcartx.core.entity.data.ArcartXPlayer;
import org.qiuhua.troveserver.arcartx.internal.network.encryptor.AESEncryptor;
import org.qiuhua.troveserver.arcartx.internal.network.encryptor.Base64Encryptor;
import org.qiuhua.troveserver.arcartx.internal.network.encryptor.DHEncryptor;
import org.qiuhua.troveserver.player.RPGPlayer;

import java.security.KeyPair;

public class SPackConnection implements ServerPacketBase {

    @SerializedName("message")
    @Getter
    private final String message;

    /**
     * 构造函数
     * 创建连接数据包，生成DH密钥对并初始化玩家密钥
     *
     * @param player 玩家对象
     */
    public SPackConnection(Player player){
        // 生成Diffie-Hellman密钥对
        KeyPair keyPair = DHEncryptor.generateKeyPair();

        if (keyPair == null) {
            throw new IllegalStateException("生成的密钥对不能为空");
        }

        // 获取玩家的ArcartXPlayer对象
        ArcartXPlayer arcartXPlayer = ArcartXEntityManager.getPlayer(player);

        // 如果玩家存在，将私钥Base64编码后存储到玩家数据中
        if(arcartXPlayer != null){

            // 将私钥Base64编码后存储
            byte[] privateKeyEncoded = keyPair.getPrivate().getEncoded();
            String base64PrivateKey = Base64Encryptor.base64Encrypt(privateKeyEncoded);
            arcartXPlayer.setKey(base64PrivateKey);
        }

        // 将公钥Base64编码后作为消息内容
        byte[] publicKeyEncoded = keyPair.getPublic().getEncoded();
        this.message = Base64Encryptor.base64Encrypt(publicKeyEncoded);

    }
}
