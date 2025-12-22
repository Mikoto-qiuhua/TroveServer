package org.qiuhua.troveserver.arcartx.internal.network.packet;


import com.google.gson.Gson;
import net.minestom.server.MinecraftServer;
import net.minestom.server.entity.Player;
import net.minestom.server.event.player.PlayerPluginMessageEvent;
import org.qiuhua.troveserver.Main;
import org.qiuhua.troveserver.arcartx.core.entity.ArcartXEntityManager;
import org.qiuhua.troveserver.arcartx.core.entity.data.ArcartXPlayer;
import org.qiuhua.troveserver.arcartx.internal.network.encryptor.AESEncryptor;
import org.qiuhua.troveserver.arcartx.internal.network.encryptor.Base64Encryptor;
import org.qiuhua.troveserver.arcartx.internal.network.message.DecodeType;
import org.qiuhua.troveserver.arcartx.internal.network.message.MessageID;
import org.qiuhua.troveserver.arcartx.internal.network.packet.client.*;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.zip.GZIPInputStream;

public class NetWorkManager {


    /**
     * 插件消息通道名称
     */
    private static final String CHANNEL = "arcartx:main";

    /**
     * 数据包注册表
     * Key: 消息ID
     * Value: 对应的PacketBase类
     */
    private final Map<Integer, Class<? extends PacketBase>> packets = new HashMap<>();

    /**
     * 分片消息缓存
     * 用于存储被分割的消息片段
     */
    private final Map<String, String> partMessage = new HashMap<>();

    private final Gson gson = new Gson();

    /**
     * 构造函数
     * 注册插件消息通道和数据包处理器
     */
    public NetWorkManager(){
        //注册所有客户端数据包类型
        this.registerPacket(MessageID.Client.CONNECTION, CPackConnection.class);
        this.registerPacket(MessageID.Client.ENTITY_JOIN, CPackEntityJoin.class);
        this.registerPacket(MessageID.Client.DONE, CPackResourceLoaded.class);
        this.registerPacket(MessageID.Client.INITIALIZE, CPackInitialized.class);
        this.registerPacket(MessageID.Client.SCREEN_DO, CPackDoWithScreen.class);
        this.registerPacket(MessageID.Client.CUSTOM, CPackCustomPacket.class);
        //注册插件消息监听器
        MinecraftServer.getGlobalEventHandler().addListener(PlayerPluginMessageEvent.class, this::onPlayerPluginMessageEvent);
    }



    /**
     * 注册数据包处理器
     */
    private void registerPacket(MessageID.Client messageId, Class<? extends PacketBase> packetBase) {
        this.packets.put(messageId.getId(), packetBase);
    }

    /**
     * 注册插件消息监听器
     */
    private void onPlayerPluginMessageEvent(PlayerPluginMessageEvent event) {
            if (!event.getIdentifier().equals(CHANNEL)) {
                return;
            }

            Player player = event.getPlayer();
            byte[] bytes = event.getMessage();

            try {
                this.handlePluginMessage(player, bytes);
            } catch (Exception e) {
                Main.getLogger().warn("处理插件消息时发生异常: {}", e.getMessage());
            }
    }

    /**
     * 处理接收到的插件消息
     */
    private void handlePluginMessage(Player player, byte[] bytes) {
        ByteBuffer buffer = ByteBuffer.wrap(bytes);

        // 检查消息头（字节32）
        byte header = buffer.get();
        if (header != 32) {
            Main.getLogger().warn("无效的消息头: {}", header);
            return;
        }

        // 读取压缩的数据
        byte[] compressedBytes = new byte[buffer.remaining()];
        buffer.get(compressedBytes);

        // 解压缩GZIP数据
        byte[] uncompressedBytes = this.uncompress(compressedBytes);
        if (uncompressedBytes == null) {
            Main.getLogger().warn("GZIP解压缩失败");
            return;
        }

        // 将解压缩的数据转换为字符串
        String jsonSrc = new String(uncompressedBytes, StandardCharsets.UTF_8);

        // 如果JSON数据为空，直接返回
        if (jsonSrc.isEmpty()) {
            return;
        }

        // 解析BaseMessage
        BaseMessage message = gson.fromJson(jsonSrc, BaseMessage.class);

        // 提取消息字段
        int id = message.getId();           // 消息唯一ID
        int level = message.getType();      // 解码类型
        int code = message.getCode();       // 消息代码
        String msg = message.getMessage();  // 消息内容
        int part = message.getPart();       // 分片索引
        int size = message.getSize();       // 总分片数

        // 处理分片消息
        if (size - part != 1) {
            // 不是最后一个分片，缓存到分片消息中
            String cacheKey = player.getUuid().toString() + id;
            String cached = this.partMessage.getOrDefault(cacheKey, "");
            this.partMessage.put(cacheKey, cached + msg);
            return;
        }

        // 如果是最后一个分片，且消息被分片过
        if (size > 1) {
            String cacheKey = player.getUuid().toString() + id;
            msg = this.partMessage.remove(cacheKey) + msg;
        }

        try {
            // 根据解码类型解码消息内容
            String decodedMsg = null;

            if (level == DecodeType.AES.getId()) {
                // AES解密
                ArcartXPlayer arcartXPlayer = ArcartXEntityManager.getPlayer(player);
                if (arcartXPlayer != null) {
                    AESEncryptor encryptor = arcartXPlayer.getEncryptor();
                    if (encryptor != null) {
                        decodedMsg = encryptor.decode(msg);
                    }
                }
            } else {
                // Base64解码
                decodedMsg = Base64Encryptor.base64Decrypt(msg);
            }

            // 根据消息代码获取对应的PacketBase类
            Class<? extends PacketBase> packetClass = this.packets.get(code);
            if (packetClass == null) {
                Main.getLogger().warn("未注册的消息类型: {}", code);
                return; // 未注册的消息类型
            }

            // 解析具体的PacketBase对象
            if (decodedMsg != null && !decodedMsg.isEmpty()) {
                PacketBase packet = gson.fromJson(decodedMsg, packetClass);
                if (packet != null) {
                    // 执行数据包处理
                    packet.handle(player);
                    //Main.getLogger().debug("处理消息类型: {}", code);
                }
            }

        } catch (Exception e) {
            // 处理异常
            Main.getLogger().warn("处理数据包时发生异常: {}", e.getMessage());
        }
    }


    /**
     * GZIP解压缩
     */
    private byte[] uncompress(byte[] bytes) {
        if (bytes == null || bytes.length == 0) {
            return null;
        }

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        ByteArrayInputStream in = new ByteArrayInputStream(bytes);

        try (GZIPInputStream ungzip = new GZIPInputStream(in)) {
            byte[] buffer = new byte[256];
            int n;

            while ((n = ungzip.read(buffer)) >= 0) {
                out.write(buffer, 0, n);
            }

            return out.toByteArray();

        } catch (IOException e) {
            Main.getLogger().warn("GZIP解压缩失败: {}", e.getMessage());
            return null;
        }
    }



}
