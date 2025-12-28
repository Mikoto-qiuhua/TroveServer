package org.qiuhua.troveserver.arcartx.internal.network.message;

/**
 * 消息ID接口
 * 定义所有网络消息的标识符
 */
public interface MessageID {

    /**
     * 获取消息ID值
     *
     * @return 消息ID
     */
    int getId();

    /**
     * 客户端消息ID枚举
     * 客户端发送给服务器的消息类型
     */
    enum Client implements MessageID {
        /** 连接请求 */
        CONNECTION(0),
        /** 按键组按下 */
        KEY_GROUP_PRESS(1),
        /** 简单按键按下 */
        SIMPLE_KEY_PRESS(2),
        /** PAPI变量相关 */
        PAPI(3),
        /** 实体加入 */
        ENTITY_JOIN(4),
        /** 获取槽位信息 */
        GET_SLOT(5),
        /** 屏幕操作 */
        SCREEN_DO(6),
        /** 自定义消息 */
        CUSTOM(7),
        /** 点击槽位 */
        CLICK_SLOT(8),
        /** 初始化 */
        INITIALIZE(9),
        /** 状态改变 */
        STATE_CHANGE(10),
        /** 尺寸数据 */
        SIZE_DATE(11),
        /** 骨骼点击 */
        BONE_HIT(12),
        /** 按键按下 */
        KEY_PRESS(13),
        /** 完成 */
        DONE(14),
        /** 方块模型 */
        BLOCK_MODEL(15),
        /** 鼠标点击 */
        MOUSE_CLICK(16),
        /** 文件列表 */
        FILE_LIST(17),
        /** 下载内容 */
        DOWNLOAD_DONE(18),
        /** UI数据 */
        UI_DATA(19);

        private final int id;

        Client(int id) {
            this.id = id;
        }

        @Override
        public int getId() {
            return this.id;
        }
    }

    /**
     * 服务器消息ID枚举
     * 服务器发送给客户端的消息类型
     * 注意：服务器消息的ID会加上1000作为偏移量
     */
    enum Server implements MessageID {
        /** 连接响应 */
        CONNECTION(0),
        /** 资源重载 */
        RESOURCE_RELOAD(1),
        /** 执行特效 */
        EXECUTE_SHIMMER(2),
        /** 设置 */
        SETTING(3),
        /** Base64图片 */
        BASE64_IMAGE(4),
        /** 自定义消息 */
        CUSTOM(5),
        /** 实体发光 */
        ENTITY_GLOW(6),
        /** 物品冷却 */
        ITEM_COOLDOWN(7),
        /** PAPI变量 */
        PAPI(8),
        /** 实体音效 */
        ENTITY_SOUND(9),
        /** 位置音效 */
        LOCATION_SOUND(10),
        /** 屏幕命令 */
        SCREEN_COMMAND(11),
        /** 槽位物品堆叠 */
        SLOT_ITEM_STACK(12),
        /** 着色器 */
        SHADER(13),
        /** 天空盒 */
        SKYBOX(14),
        /** 世界改变 */
        WORLD_CHANGE(15),
        /** 实体交互 */
        ENTITY_INTERACTION(16),
        /** 玩家跳跃 */
        PLAYER_JUMP(17),
        /** 状态改变 */
        STATE_CHANGE(18),
        /** 控制器 */
        CONTROLLER(19),
        /** 场景相机 */
        SCENE_CAMERA(20),
        /** 客户端标题 */
        CLIENT_TITLE(21),
        /** 相机设置 */
        CAMERA_SET(22),
        /** 相机预设 */
        CAMERA_PRESET(23),
        /** 相机锁定模式 */
        CAMERA_LOCK_MODE(24),
        /** 第三人称相机 */
        CAMERA_THIRD_PERSON(25),
        /** 相机元素 */
        CAMERA_ELEMENT(26),
        /** 实体模型 */
        ENTITY_MODEL(30),
        /** 实体尺寸 */
        ENTITY_SIZE(31),
        /** 实体动画 */
        ENTITY_ANIMATION(32),
        /** 实体动画默认状态 */
        ENTITY_ANIMATION_DEFAULT_STATE(33),
        /** 隐藏骨骼 */
        ENTITY_HIDE_BONE(34),
        /** 隐藏名称 */
        ENTITY_HIDE_NAME(35),
        /** 屏幕 */
        SCREEN(36),
        /** 模型效果 */
        MODEL_EFFECT(37),
        /** 创建路径点 */
        WAYPOINT_CREATE(39),
        /** 删除路径点 */
        WAYPOINT_DELETE(40),
        /** 伤害显示 */
        DAMAGE_DISPLAY(41),
        /** Adyeshach加入 */
        ADYESHACH_JOIN(42),
        /** 方块动画 */
        BLOCK_ANIMATION(43),
        /** 锤子裂缝 */
        HAMMER_CRACK(44),
        /** 额外模型 */
        EXTRA_MODEL(45),
        /** 卡片消息 */
        CARD_MESSAGE(46),
        /** 方块模型 */
        BLOCK_MODEL(47),
        /** 槽位调试 */
        SLOT_DEBUG(48),
        /** 移除变量 */
        REMOVE_VARIABLE(49),
        /** 玩家替换模型 */
        PLAYER_SUBSTITUTION_MODEL(50),
        /** 世界纹理 */
        WORLD_TEXTURE(51),
        /** 移除世界纹理 */
        WORLD_TEXTURE_REMOVE(52),
        /** 打开编辑器 */
        OPEN_EDITOR(53),
        /** 文件列表 */
        FILE_LIST(54),
        /** 隐藏命中框 */
        HIDE_HIT_BOX(55);

        private final int id;

        Server(int id) {
            this.id = id;
        }

        @Override
        public int getId() {
            return this.id + 1000; // 服务器消息ID加上1000偏移量
        }
    }
}
