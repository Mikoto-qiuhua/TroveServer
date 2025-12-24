package org.qiuhua.troveserver.arcartx.internal.network.packet;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import net.minestom.server.MinecraftServer;
import net.minestom.server.entity.Player;
import net.minestom.server.item.ItemStack;
import net.minestom.server.network.player.PlayerConnection;
import net.minestom.server.network.player.PlayerSocketConnection;
import org.jetbrains.annotations.NotNull;
import org.qiuhua.troveserver.Main;
import org.qiuhua.troveserver.arcartx.core.config.camera.CameraElement;
import org.qiuhua.troveserver.arcartx.core.config.camera.SceneCamera;
import org.qiuhua.troveserver.arcartx.core.config.camera.SceneCameraFolder;
import org.qiuhua.troveserver.arcartx.core.config.ui.type.UI;
import org.qiuhua.troveserver.arcartx.core.entity.ArcartXEntityManager;
import org.qiuhua.troveserver.arcartx.core.entity.data.ArcartXPlayer;
import org.qiuhua.troveserver.arcartx.event.client.ClientInitializedEvent;
import org.qiuhua.troveserver.arcartx.internal.network.encryptor.AESEncryptor;
import org.qiuhua.troveserver.arcartx.internal.network.encryptor.Base64Encryptor;
import org.qiuhua.troveserver.arcartx.internal.network.message.DecodeType;
import org.qiuhua.troveserver.arcartx.internal.network.message.MessageID;
import org.qiuhua.troveserver.arcartx.internal.network.packet.server.*;
import org.qiuhua.troveserver.arcartx.util.IOUtils;
import org.qiuhua.troveserver.arcartx.util.JsonUtils;
import org.qiuhua.troveserver.utils.task.SchedulerManager;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class NetworkMessageSender {

    private static int id;

    private static final String CHANNEL_NAME = "arcartx:main";
    private static final byte PACKET_ID = 32;  // 与原版一致


    private static int getNextID() {
        int n = id;
        id = n + 1;
        return n;
    }


    /**
     * 发送玩家加入包 这里会获取通讯解密用的秘钥
     * @param player
     */
    public static void sendPlayerJoinPacket(Player player) {
        sendPacketSync(player, MessageID.Server.CONNECTION, DecodeType.NORMAL, new SPackConnection(player));
    }

    /**
     * 发送资源唤起包
     * @param player
     * @param init 是否是首次加载
     */
    public static void sendResourceReload(Player player, boolean init){
        if(init){
            MinecraftServer.getGlobalEventHandler().call(new ClientInitializedEvent.Start(player));
        }
        sendPacketSync(player, MessageID.Server.RESOURCE_RELOAD, DecodeType.AES, new SPackResourceReload(!init));
    }

    /**
     * 发送客户端标题
     * @param player
     * @param text
     */
    public static void sendClientTitle(Player player, String text) {
        sendPacketSync(player, MessageID.Server.CLIENT_TITLE, DecodeType.NORMAL, new SPackClientTitle(text));
    }


    /**
     * 发送全部AX的配置
     * @param player
     */
    public static void sendSetting(Player player) {
        sendPacketSync(player, MessageID.Server.SETTING, DecodeType.NORMAL, new SPackSettings());
    }

    /**
     * 发送一个ui
     * @param player
     * @param ui
     */
    public static void sendUI(Player player, UI ui) {
        sendPacketSync(player, MessageID.Server.SCREEN, DecodeType.NORMAL, new SPackScreen(ui));
    }

    /**
     * 发送界面自定义数据包
     * @param player
     * @param key
     * @param value
     * @param updateID
     */
    public static void sendScreenPacket(Player player, String key, Object value, String updateID) {
        sendPacketSync(player, MessageID.Server.PAPI, DecodeType.NORMAL, new SPackPlaceholder(key, value, true, updateID));
    }

    /**
     * 打开ui
     * @param player
     * @param screenID
     */
    public static void sendOpenUI(Player player, String screenID) {
        sendPacketSync(player, MessageID.Server.SCREEN_COMMAND, DecodeType.NORMAL, new SPackScreenCommand("open", screenID, null, 4, null));
    }

    /**
     * 关闭ui
     * @param player
     * @param screenID
     */
    public static void sendCloseUI(Player player, String screenID) {
        sendPacketSync(player, MessageID.Server.SCREEN_COMMAND, DecodeType.NORMAL, new SPackScreenCommand("close", screenID, null, 4, null));
    }

    /**
     * ui中执行代码
     * @param player
     * @param screenID
     * @param code
     */
    public static void sendUIRunCode(Player player, String screenID, String code) {
        sendPacketSync(player, MessageID.Server.SCREEN_COMMAND, DecodeType.NORMAL, new SPackScreenCommand("run", screenID, code));
    }

    /**
     * 发送自定义数据包
     * @param player
     * @param key
     * @param value
     */
    public static void sendCustomPacket(Player player, String key, String ... value) {
        sendPacketSync(player, MessageID.Server.CUSTOM, DecodeType.NORMAL, new SPackCustomPacket(key, Arrays.asList(Arrays.copyOf(value, value.length))));
    }

    /**
     * 给一个槽位发送物品
     * @param player
     * @param id
     * @param itemStack
     */
    public static void sendSlotItemStack(Player player, String id, ItemStack itemStack) {
        sendPacketSync(player, MessageID.Server.SLOT_ITEM_STACK, DecodeType.NORMAL, new SPackSlotItemStack(id, itemStack));
    }

    /**
     * 移除一个虚拟槽位物品
     * @param player
     * @param id
     * @param isFirstWith 是否自动给末尾添加数字 例如 Slot_  = Slot_1 Slot_2 这样的槽位都移除物品
     */
    public static void sendSlotItemRemove(Player player, String id, boolean isFirstWith) {
        sendPacketSync(player, MessageID.Server.SLOT_ITEM_STACK, DecodeType.NORMAL, new SPackSlotItemStack(id, isFirstWith, true));
    }


    /**
     * 发送一个场景相机
     * @param player
     * @param name 场景相机名称
     */
    public static void sendSceneCamera(Player player, String name) {
        SceneCamera sceneCamera = SceneCameraFolder.sceneCameras.get(name);
        if (sceneCamera == null) return;
        sendPacketSync(player, MessageID.Server.SCENE_CAMERA, DecodeType.NORMAL, new SPackSceneCamera(sceneCamera));

    }

    /**
     * 停止玩家当前的场景相机
     * @param player
     */
    public static void sendSceneCameraStop(Player player) {
        sendPacketSync(player, MessageID.Server.SCENE_CAMERA, DecodeType.NORMAL, new SPackSceneCamera(null));
    }

    /**
     * 给玩家设置相机
     * @param player
     * @param x
     * @param y
     * @param z
     * @param freeView
     */
    public static void setCamera(Player player, double x, double y, double z, boolean freeView) {
        sendPacketSync(player, MessageID.Server.CAMERA_SET, DecodeType.NORMAL, new SPackCamera(x, y, z, freeView));
    }

    /**
     * 给玩家设置一个预设相机
     * @param player
     * @param presetName
     */
    public static void setCameraFromPreset(Player player, String presetName) {
        sendPacketSync(player, MessageID.Server.CAMERA_PRESET, DecodeType.NORMAL, new SPackCameraPreset(presetName));
    }

    /**
     * 给玩家设置一个相机
     * @param player
     * @param element
     */
    public static void setCameraFromElement(Player player, CameraElement element) {
        sendPacketSync(player, MessageID.Server.CAMERA_ELEMENT, DecodeType.NORMAL, new SPackCameraElement(element));
    }

    /**
     * 设置视角锁定
     * @param player
     * @param lockMode
     */
    public static void setViewLock(Player player, int lockMode) {
        sendPacketSync(player, MessageID.Server.CAMERA_LOCK_MODE, DecodeType.NORMAL, new SPackLockView(lockMode));
    }

    /**
     * 设置第三人称视角
     * @param player
     * @param thirdPerson
     */
    public static void setThirdPerson(Player player, boolean thirdPerson) {
        sendPacketSync(player, MessageID.Server.CAMERA_THIRD_PERSON, DecodeType.NORMAL, new SPackThirdPerson(thirdPerson));
    }


    /**
     * 同步发送数据包（在主线程执行）
     *
     * @param player 目标玩家
     * @param messageID 消息ID
     * @param decodeType 解码类型（Base64或AES）
     * @param packet 数据包实例
     */
    private static void sendPacketSync(Player player, MessageID messageID, DecodeType decodeType, PacketBase packet) {
        //在玩家的tick线程中发送
        player.scheduleNextTick(entity -> {
            // 创建消息并发送
            List<BaseMessage> messages = createMessage(baseMessage -> {
                // 设置消息基本信息
                baseMessage.setType(decodeType.getId());
                baseMessage.setCode(messageID.getId());
                baseMessage.setId(getNextID());
                // 根据解码类型进行加密
                if (decodeType == DecodeType.AES) {
                    // 使用AES加密
                    ArcartXPlayer arcartXPlayer = ArcartXEntityManager.getPlayer(player);
                    if (arcartXPlayer != null) {
                        AESEncryptor encryptor = arcartXPlayer.getEncryptor();
                        if (encryptor != null) {
                            String jsonData = JsonUtils.toJson(packet);
                            String encryptedData = encryptor.encode(jsonData);
                            if (encryptedData != null) {
                                baseMessage.setMessage(encryptedData);
                            }
                        }
                    }
                } else {
                    // 使用Base64编码
                    String jsonData = JsonUtils.toJson(packet);
                    String encodedData = Base64Encryptor.base64Encrypt(jsonData);
                    baseMessage.setMessage(encodedData);
                }
            });

            // 发送所有消息片段
            for (BaseMessage message : messages) {
                // 序列化为JSON
                String json = JsonUtils.toJson(message);
                byte[] jsonBytes = json.getBytes(StandardCharsets.UTF_8);

                // 压缩数据
                byte[] compressedData = compressedByteArray(jsonBytes);

                // 创建网络缓冲区
                ByteBuf buf = Unpooled.buffer(compressedData.length + 1);
                buf.writeByte(PACKET_ID);
                buf.writeBytes(compressedData);

                //发送数据
                player.sendPluginMessage(CHANNEL_NAME, buf.array());

                // 释放资源
                buf.release();
            }



        });
    }

    /**
     * 异步发送数据包（在异步线程执行）
     *
     * @param player 目标玩家
     * @param messageID 消息ID
     * @param decodeType 解码类型（Base64或AES）
     * @param packet 数据包实例
     */
    private static void sendPacketAsync(Player player, MessageID messageID, DecodeType decodeType, PacketBase packet){
        SchedulerManager.submitAsync(()->{

        });
    }


    /**
     * 创建消息列表
     * 如果消息长度超过30000字符，会自动分割成多个片段
     *
     * @param packetWriter 数据包写入器
     * @return 消息列表
     */
    private static ArrayList<BaseMessage> createMessage(IPacketWriter packetWriter) {
        ArrayList<BaseMessage> result = new ArrayList<>();
        BaseMessage message = new BaseMessage();

        // 使用写入器填充消息内容
        packetWriter.write(message);

        // 检查消息长度是否超过限制
        if (message.getMessage().length() > 30000) {
            // 计算需要分割成多少片段
            int size = message.getMessage().length() / 30000;
            if (message.getMessage().length() % 30000 > 0) {
                size++;
            }

            // 分割消息
            for (int part = 0; part < size; ++part) {
                BaseMessage partMessage = getBaseMessage(message, part, size);
                result.add(partMessage);
            }
        } else {
            // 消息长度在限制内，直接添加
            result.add(message);
        }

        return result;
    }

    /**
     * 获取分割后的消息片段
     *
     * @param message 原始消息
     * @param part 片段索引
     * @param size 总片段数
     * @return 分割后的消息片段
     */
    private static BaseMessage getBaseMessage(BaseMessage message, int part, int size) {
        BaseMessage partMessage = new BaseMessage();

        // 复制基本信息
        partMessage.setType(message.getType());
        partMessage.setId(message.getId());
        partMessage.setCode(message.getCode());

        // 分割消息内容
        String cutString;
        if (part + 1 == size) {
            // 最后一个片段：从起始位置到结尾
            cutString = message.getMessage().substring(30000 * part);
        } else {
            // 中间片段：截取指定范围
            cutString = message.getMessage().substring(30000 * part, 30000 * (part + 1));
        }

        partMessage.setMessage(cutString);
        partMessage.setPart(part);
        partMessage.setSize(size);

        return partMessage;
    }


    /**
     * 数据包写入器接口
     * 定义如何将数据包内容写入到BaseMessage中
     */
    public interface IPacketWriter {
        /**
         * 将数据包内容写入到消息对象中
         *
         * @param message 目标消息对象
         */
        void write(BaseMessage message);
    }


    /**
     * 使用GZIP压缩字节数组
     *
     * @param data 原始字节数组
     * @return 压缩后的字节数组，如果压缩失败返回null
     */
    private static byte[] compressedByteArray(byte[] data) {
        java.io.ByteArrayOutputStream byteArrayOutputStream = new java.io.ByteArrayOutputStream();
        java.util.zip.GZIPOutputStream gzipOutputStream = null;
        try {
            gzipOutputStream = new java.util.zip.GZIPOutputStream(byteArrayOutputStream);
            gzipOutputStream.write(data);
            gzipOutputStream.close();

            return byteArrayOutputStream.toByteArray();
        } catch (java.io.IOException e) {
            e.printStackTrace();
            return null;
        } finally {
            IOUtils.closeQuietly(byteArrayOutputStream);
            IOUtils.closeQuietly(gzipOutputStream);
        }
    }



}
