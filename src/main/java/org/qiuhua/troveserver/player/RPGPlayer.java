package org.qiuhua.troveserver.player;

import lombok.Getter;
import lombok.Setter;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.title.Title;
import net.kyori.adventure.title.TitlePart;
import net.kyori.adventure.util.Ticks;
import net.minestom.server.MinecraftServer;
import net.minestom.server.entity.GameMode;
import net.minestom.server.entity.LivingEntity;
import net.minestom.server.entity.Player;
import net.minestom.server.entity.attribute.Attribute;
import net.minestom.server.entity.attribute.AttributeInstance;
import net.minestom.server.item.ItemStack;
import net.minestom.server.network.packet.server.play.SetCooldownPacket;
import net.minestom.server.network.player.GameProfile;
import net.minestom.server.network.player.PlayerConnection;
import org.jetbrains.annotations.Nullable;
import org.qiuhua.troveserver.Main;
import org.qiuhua.troveserver.api.attribute.IAttribute;
import org.qiuhua.troveserver.api.buff.IBuff;
import org.qiuhua.troveserver.api.buff.IBuffData;
import org.qiuhua.troveserver.config.ServerConfig;
import org.qiuhua.troveserver.module.role.RoleData;
import org.qiuhua.troveserver.module.playermode.event.PlayerModeSwitchEvent;
import org.qiuhua.troveserver.module.attribute.EntityAttributesData;
import org.qiuhua.troveserver.module.playermode.PlayerMode;
import org.qiuhua.troveserver.module.role.RoleUnlockedState;
import org.qiuhua.troveserver.module.role.config.RoleConfig;
import org.qiuhua.troveserver.module.role.event.RoleSwitchEvent;
import org.qiuhua.troveserver.module.role.event.RoleSwitchPreEvent;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class RPGPlayer extends Player implements IAttribute, IBuff {


    /**
     * 玩家是否是管理员
     */
    @Getter
    private final boolean isAdmin;


    /**
     * 属性数据 临时性的无需缓存
     */
    @Getter
    private final EntityAttributesData entityAttributesData = new EntityAttributesData();

    /**
     * 玩家当前的模式
     */
    @Getter
    @Nullable
    private PlayerMode playerMode = null;

    /**
     * 玩家是否已经登入 默认是false 只有登入成功后才是true
     */
    @Getter
    @Setter
    private Boolean isLogin = false;

    /**
     * 存储玩家角色数据的合集
     */
    @Getter
    private final Map<String, RoleData> roleDataMap = new HashMap<>();

    /**
     * 建造模式的物品栏 对应0-8
     */
    @Getter
    private final List<ItemStack> buildModeInventory = new ArrayList<>();

    /**
     * 当前使用的角色数据
     */
    @Getter
    private RoleData useRoleData;

    /**
     * 当前有效的buff合集
     */
    @Getter
    private final ConcurrentHashMap<String, IBuffData> buffMap = new ConcurrentHashMap<>();


    public RPGPlayer(PlayerConnection playerConnection, GameProfile gameProfile) {
        super(playerConnection, gameProfile);
        isAdmin = ServerConfig.adminList.contains(getUsername());
    }


    /**
     * @return
     */
    @Override
    public LivingEntity getEntity() {
        return this;
    }

    /**
     * 设置玩家当前的游戏模式
     * @param playerMode
     * @return
     */
    public void setPlayerMode(PlayerMode playerMode){
        if(playerMode == this.playerMode) return;
        PlayerModeSwitchEvent playerModeSwitchEvent = new PlayerModeSwitchEvent(this, playerMode);
        MinecraftServer.getGlobalEventHandler().call(playerModeSwitchEvent);
        if(playerModeSwitchEvent.isCancelled()) return;
        this.playerMode = playerModeSwitchEvent.getPlayerMode();
        if(this.playerMode == PlayerMode.Battle){
            //切换到战斗模式
            setGameMode(GameMode.ADVENTURE);
        }else {
            //切换到建造模式
            setGameMode(GameMode.SURVIVAL);
        }
        Main.getLogger().debug("{} 模式切换为 {}", getUsername(), this.playerMode);
    }


    /**
     * 将玩家当前切换到其他角色
     * @param roleId
     */
    public void switchRole(String roleId){
        RoleData roleData = roleDataMap.get(roleId);
        if(roleData == null) return;
        RoleSwitchPreEvent roleSwitchPreEvent = new RoleSwitchPreEvent(this, roleData);
        MinecraftServer.getGlobalEventHandler().call(roleSwitchPreEvent);
        if(roleSwitchPreEvent.isCancelled()) return;
        roleData = roleSwitchPreEvent.getRoleData();
        if(roleData.getRoleUnlockedState() == RoleUnlockedState.NotUnlocked){
            Main.getLogger().debug("{} 需要切换的角色 {} 未解锁", getUsername(), roleData.getRoleId());
            return;
        }
        useRoleData = roleData;
        //移除原有基础的属性
        removeAttribute("base_attribute_player");
        //添加属性
        addAttribute(roleData.getAttributeCompileGroup(), "role_attribute");
        updateAttribute();
        //设置生命值
        Attribute attribute = Attribute.fromKey("max_health");
        AttributeInstance attributeInstance = getAttribute(attribute);
        Double health = attributeInstance.getValue();
        setHealth((float) (health * RoleConfig.switchHealthValue));
        RoleSwitchEvent roleSwitchEvent = new RoleSwitchEvent(this, roleData);
        MinecraftServer.getGlobalEventHandler().call(roleSwitchEvent);
        Main.getLogger().debug("{} 使用的角色切换为 {}", getUsername(), roleData.getRoleId());

    }

    /**
     * 发送物品冷却数据包
     * @param cooldownGroup 命名空间:id 全小写
     * @param cooldownTicks 冷却时间 tick
     */
    public void setItemCooldown(String cooldownGroup, int cooldownTicks){
        // 创建冷却数据包
        SetCooldownPacket packet = new SetCooldownPacket(cooldownGroup, cooldownTicks);
        sendPacket(packet);
    }


    /**
     * 向玩家发送一个title
     * @param title 主标题
     * @param subtitle 副标题
     * @param fadeInTicks 淡入tick
     * @param stayTicks 持续tick
     * @param fadeOutTicks 淡出tick
     */
    public void showTitle(Component title, Component subtitle,
                                 int fadeInTicks, int stayTicks, int fadeOutTicks) {
        if(title != null){
            sendTitlePart(TitlePart.TITLE, title);
        }
        if(subtitle != null){
            sendTitlePart(TitlePart.SUBTITLE, subtitle);
        }
        //设置显示时间
        Title.Times times = Title.Times.times(
                Ticks.duration(fadeInTicks),
                Ticks.duration(stayTicks),
                Ticks.duration(fadeOutTicks)
        );
        sendTitlePart(TitlePart.TIMES, times);
    }



}


