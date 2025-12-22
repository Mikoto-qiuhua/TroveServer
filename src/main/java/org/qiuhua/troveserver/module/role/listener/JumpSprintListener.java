package org.qiuhua.troveserver.module.role.listener;

import lombok.Getter;
import lombok.Setter;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.sound.Sound;
import net.minestom.server.MinecraftServer;
import net.minestom.server.coordinate.Pos;
import net.minestom.server.coordinate.Vec;
import net.minestom.server.entity.attribute.Attribute;
import net.minestom.server.entity.attribute.AttributeInstance;
import net.minestom.server.event.player.PlayerDisconnectEvent;
import net.minestom.server.event.player.PlayerPacketEvent;
import net.minestom.server.instance.Instance;
import net.minestom.server.instance.block.Block;
import net.minestom.server.network.packet.client.play.ClientInputPacket;
import org.qiuhua.troveserver.api.skill.AbstractSkillMechanic;
import org.qiuhua.troveserver.arcartx.core.entity.ArcartXEntityManager;
import org.qiuhua.troveserver.module.role.RoleManager;
import org.qiuhua.troveserver.module.role.config.RoleConfig;
import org.qiuhua.troveserver.module.role.event.RoleJumpEvent;
import org.qiuhua.troveserver.module.role.event.RoleSprintEvent;
import org.qiuhua.troveserver.player.RPGPlayer;

import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class JumpSprintListener {



    private final ConcurrentHashMap<UUID, JumpSprintData> jumpSprintDataMap = new ConcurrentHashMap<>();



    public JumpSprintListener(){
        RoleManager.roleNode.addListener(PlayerPacketEvent.class, this::onPlayerPacketEvent);
        //玩家离开服务器 卸载玩家数据
        MinecraftServer.getGlobalEventHandler().addListener(PlayerDisconnectEvent.class, event -> {
            jumpSprintDataMap.remove(event.getPlayer().getUuid());
        });
    }

    /**
     * 玩家输入数据包事件
     * 负责连跳和冲刺
     * @param event
     */
    public void onPlayerPacketEvent(PlayerPacketEvent event){
        if(event.isCancelled()) return;

        if(event.getPacket() instanceof ClientInputPacket clientInputPacket){
            RPGPlayer rpgPlayer = (RPGPlayer) event.getPlayer();
            if(!rpgPlayer.getIsLogin() || rpgPlayer.getUseRoleData() == null) return;

            if(AbstractSkillMechanic.isGlobalCooldown(rpgPlayer)) return;
            //获取玩家所在的世界
            Instance instance = rpgPlayer.getInstance();
            // 获取玩家当前位置
            Pos playerPosition = rpgPlayer.getPosition();
            Long time = System.currentTimeMillis();

            JumpSprintData jumpSprintData = jumpSprintDataMap.computeIfAbsent(rpgPlayer.getUuid(), k -> new JumpSprintData());

            if(clientInputPacket.sprint()){
                if((time - jumpSprintData.getSprintTime()) >= RoleConfig.sprintTime){
                    RoleSprintEvent roleSprintEvent = new RoleSprintEvent(rpgPlayer);
                    MinecraftServer.getGlobalEventHandler().call(roleSprintEvent);
                    if(!roleSprintEvent.isCancelled()){
                        //Main.getLogger().debug("{} 触发突进", rpgPlayer.getUsername());
                        rpgPlayer.setVelocity(getSprintSpeed(rpgPlayer));
                        jumpSprintData.setSprintTime(time);
                        //发送技能冷却数据包
                        rpgPlayer.setItemCooldown("roleskill:sprint", RoleConfig.sprintTime/50);
                        rpgPlayer.playSound(Sound.sound(Key.key("entity.ender_dragon.flap"), Sound.Source.PLAYER, 1f, 1f));
                    }
                }
            }
            if(clientInputPacket.jump()){
                int blockX = (int) Math.floor(playerPosition.x());
                int blockY = (int) Math.floor(playerPosition.y() - 0.1); //减去一个小偏移量以确保获取的是脚下的方块
                int blockZ = (int) Math.floor(playerPosition.z());
                //从世界实获取该坐标的方块
                Block block = instance.getBlock(blockX, blockY, blockZ);
                Integer jumpMax = rpgPlayer.getAttributeAmount("跳跃次数").intValue();
                if(block.isAir()){
                    RoleJumpEvent roleJumpEvent = new RoleJumpEvent(rpgPlayer);
                    MinecraftServer.getGlobalEventHandler().call(roleJumpEvent);
                    if(roleJumpEvent.isCancelled()) return;
                    if((time - jumpSprintData.getJumpTime()) >= RoleConfig.jumpTime && jumpSprintData.getJumpAmount() < jumpMax){
                        jumpSprintData.setJumpAmount(jumpSprintData.getJumpAmount() + 1);
                        jumpSprintData.setJumpTime(time);
                        Vec vec = new Vec(0, 10, 0);
                        //如果在向前移动
                        if(clientInputPacket.forward()){
                            vec = getJumpSpeed(rpgPlayer);
                        }
                        rpgPlayer.setVelocity(vec);
                        rpgPlayer.playSound(Sound.sound(Key.key("entity.player.attack.nodamage"), Sound.Source.PLAYER, 1f, 1f));
                        //Main.getLogger().debug("{} 跳跃次数 {}/{}", rpgPlayer.getUsername(), jumpData.getAmount(), jumpMax);
                    }
                }else {
                    jumpSprintData.setJumpAmount(1);
                    if(block.isLiquid() && (time - jumpSprintData.getJumpTime()) >= RoleConfig.jumpTime){
                        jumpSprintData.setJumpTime(time);
                        rpgPlayer.setVelocity(new Vec(0, RoleConfig.jumpSpeed, 0));
                        rpgPlayer.playSound(Sound.sound(Key.key("entity.player.attack.nodamage"), Sound.Source.PLAYER, 1f, 1f));
                    }
                    //Main.getLogger().debug("{} 第一次跳跃", rpgPlayer.getUsername());
                }
            }

        }

    }





    /**
     * 获取跳跃前进时速度
     * @param rpgPlayer
     * @return
     */
    private Vec getJumpSpeed(RPGPlayer rpgPlayer) {
        AttributeInstance attributeInstance = rpgPlayer.getAttribute(Attribute.MOVEMENT_SPEED);
        //获取玩家当前的偏航角（yaw）
        float yaw = rpgPlayer.getPosition().yaw();
        //将角度转换为弧度
        double rad = Math.toRadians(yaw);
        //计算正前方向量（只使用偏航角）
        double x = -Math.sin(rad) * (attributeInstance.getValue() * 60);   // 前方向的X分量
        double z = Math.cos(rad) * (attributeInstance.getValue() * 60);   // 前方向的Z分量
        // 施加力（只影响X和Z轴，保持Y轴速度不变）
        return new Vec(x, RoleConfig.jumpSpeed, z);
    }

    /**
     * 获取突进速度
     * @param rpgPlayer
     * @return
     */
    private Vec getSprintSpeed(RPGPlayer rpgPlayer) {
        AttributeInstance attributeInstance = rpgPlayer.getAttribute(Attribute.MOVEMENT_SPEED);
        //获取玩家当前的偏航角（yaw）
        float yaw = rpgPlayer.getPosition().yaw();
        //将角度转换为弧度
        double rad = Math.toRadians(yaw);
        //计算正前方向量（只使用偏航角）
        double x = -Math.sin(rad) * (attributeInstance.getValue() * RoleConfig.sprintSpeed);   // 前方向的X分量
        double z = Math.cos(rad) * (attributeInstance.getValue() * RoleConfig.sprintSpeed);   // 前方向的Z分量
        // 施加力（只影响X和Z轴，保持Y轴速度不变）
        return new Vec(x, 0, z);
    }

    @Setter
    @Getter
    private static class JumpSprintData{
        /**
         * 跳跃次数
         */
        private Integer jumpAmount = 0;

        /**
         * 上一次跳跃时间
         */
        private Long jumpTime = 0L;

        /**
         * 上一次冲刺的时间
         */
        private Long sprintTime = 0L;

        public JumpSprintData(){

        }
    }



}
