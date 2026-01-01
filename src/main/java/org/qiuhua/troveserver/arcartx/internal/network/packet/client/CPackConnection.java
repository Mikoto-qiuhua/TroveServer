package org.qiuhua.troveserver.arcartx.internal.network.packet.client;

import com.google.gson.annotations.SerializedName;
import lombok.Getter;
import lombok.Setter;
import net.minestom.server.entity.Player;
import org.qiuhua.troveserver.Main;
import org.qiuhua.troveserver.arcartx.core.config.Setting;
import org.qiuhua.troveserver.arcartx.core.entity.ArcartXEntityManager;
import org.qiuhua.troveserver.arcartx.core.entity.data.ArcartXPlayer;
import org.qiuhua.troveserver.arcartx.internal.network.encryptor.AESEncryptor;
import org.qiuhua.troveserver.arcartx.internal.network.encryptor.Base64Encryptor;
import org.qiuhua.troveserver.arcartx.internal.network.encryptor.DHEncryptor;
import org.qiuhua.troveserver.arcartx.internal.network.packet.NetworkMessageSender;
import org.qiuhua.troveserver.arcartx.internal.network.packet.PacketBase;
import org.qiuhua.troveserver.config.ServerConfig;
import org.qiuhua.troveserver.player.RPGPlayer;

import javax.crypto.SecretKey;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CPackConnection implements PacketBase {


    /**
     * 消息内容（Base64编码的客户端公钥）
     */
    @Getter
    @Setter
    @SerializedName("message")
    private String message;

    /**
     * 加密的CRC64校验码列表
     */
    @SerializedName("code")
    @Getter
    @Setter
    private List<String> code = new ArrayList<>();

    @SerializedName(value="resource")
    @Getter
    private final Map<String, String> resource = new HashMap<>();

    /**
     * 处理数据包的核心方法，由具体实现类实现
     *
     * @param player 玩家对象
     */
    @Override
    public void handle(Player player) {
        //获取玩家的ArcartXPlayer对象
        ArcartXPlayer arcartXPlayer = ArcartXEntityManager.getPlayer(player);
        //计算共享密钥
        //this.message: 客户端公钥（Base64编码）
        //arcartXPlayer.getKey(): 服务器私钥（Base64编码）
        SecretKey key = DHEncryptor.getSecretKey(
                Base64Encryptor.base64DecryptByte(this.message),  // 客户端公钥
                Base64Encryptor.base64DecryptByte(
                        arcartXPlayer != null ? arcartXPlayer.getKey() : null  // 服务器私钥
                )
        );
        //创建AES加密器并设置共享密钥
        AESEncryptor encryptor = new AESEncryptor();
        encryptor.setAesKey(key);
        //保存加密器到玩家数据
        if (arcartXPlayer != null) {
            arcartXPlayer.setEncryptor(encryptor);
        }
        //解密并解析CRC64校验码
        List<Long> crc64 = new ArrayList<>();
        try {
            for (String encryptedCrc : this.code) {
                // 使用AES解密CRC64值
                String decrypted = encryptor.decode(encryptedCrc);
                Long crcValue = decrypted != null ? Long.parseLong(decrypted) : null;
                crc64.add(crcValue);
            }
        } catch (Exception e) {
            // 解密失败，添加默认值
            crc64.add(0L);
        }

        //校验CRC64
        if (this.checkCrc64(player, crc64)) {
            // 校验通过，发送资源重载（初始化）
            NetworkMessageSender.sendResourceReload(player, true);
        }
    }

    /**
     * 校验客户端CRC64值
     * 临时全部通过 该功能还未实现
     * @param player 玩家
     * @param clientCrc 客户端提供的CRC64列表
     * @return 校验是否通过
     */
    private boolean checkCrc64(Player player, List<Long> clientCrc) {
        //如果未启用CRC64校验，直接通过
        if (!Setting.crc64_enable) {
            return true;
        }

        //获取服务器配置的CRC64列表
        List<Long> serverCrcList = Setting.crc64_list;

        //如果不允许部分匹配，且客户端CRC数量与服务器不匹配
        if (!Setting.crc64_allowPartial && clientCrc.size() != serverCrcList.size()) {
            return false;
        }

        //如果客户端CRC数量大于服务器配置
        if (clientCrc.size() > serverCrcList.size()) {
            return false;
        }

        //逐个比较CRC值
        for (int i = 0; i < clientCrc.size(); i++) {
            long clientCrcValue = clientCrc.get(i);
            long serverCrcValue = serverCrcList.get(i);
            if (clientCrcValue != serverCrcValue) {
                return false;
            }
        }
        //所有CRC校验通过
        return true;
    }

    /**
     * 是否异步执行
     * @return true表示异步执行
     */
    @Override
    public boolean isAsync() {
        return false;
    }



}
