package org.qiuhua.troveserver.entity;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.title.Title;
import net.minestom.server.coordinate.Pos;
import net.minestom.server.entity.EntityType;
import net.minestom.server.entity.Player;
import net.minestom.server.entity.attribute.Attribute;
import net.minestom.server.entity.metadata.display.AbstractDisplayMeta;
import net.minestom.server.event.entity.EntityDamageEvent;
import net.minestom.server.instance.Instance;
import net.minestom.server.timer.TaskSchedule;
import net.worldseed.multipart.events.ModelInteractEvent;
import org.jetbrains.annotations.NotNull;
import org.qiuhua.troveserver.Main;
import org.qiuhua.troveserver.api.entity.IMobCreature;
import org.qiuhua.troveserver.entity.display.TextDisplayEntity;
import org.qiuhua.troveserver.module.mob.MobConfig;
import org.qiuhua.troveserver.player.RPGPlayer;
import org.qiuhua.troveserver.skill.targeter.LocationUtils;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

public class DamageDummyEntity extends ModelEntity implements IMobCreature {


    /**
     * 记录玩家的伤害数据 有排序系统
     * 按value的最高值排序
     */
    private final LinkedHashMap<String, Float> playerDamageStatistics = new LinkedHashMap<>();


    /**
     * 当前是否处于打桩模式
     */
    private Boolean trainingMode = false;

    /**
     * 剩余的打桩时间 单位是tick
     * 结束时应该为0
     */
    private Integer trainingTick = 0;

    /**
     * 打桩模式的伤害累计
     * 结束时应该为0
     */
    private Float damageStatistics = 0.0f;

    /**
     * 打桩模式的伤害最大值
     * 结束时应该为0
     */
    private Float damageMax = 0.0f;

    /**
     * 当前交互的玩家
     * 当进入30秒打桩时应该将进入的玩家设置
     * 屏蔽来自其他玩家的伤害
     * 结束时应该为null
     */
    private RPGPlayer interactivePlayer;

    /**
     * 顶部的全息文字
     * 默认情况显示 -> 潜行+右键开始打桩 -> 30s
     * 当激活打桩后显示 -> 玩家名称 -> 剩余秒数s
     */
    private TextDisplayEntity topText = new TextDisplayEntity("潜行+右键开始打桩 -> 30s");

    /**
     * 左侧的全息文字 用于显示伤害排名
     */
    private TextDisplayEntity leftText = new TextDisplayEntity("伤害排行榜");

    /**
     * 右侧的全息文字 用于显示打桩状态下的最高伤害和累计伤害
     */
    private TextDisplayEntity rightText = new TextDisplayEntity("");


    public DamageDummyEntity() {
        super("damagedummy.bbmodel", EntityType.ZOMBIE);
        setNoGravity(false);
        //关掉他的自动显示 全部由本体进行
        topText.setAutoViewEntities(false);
        topText.setBillboardConstraints(AbstractDisplayMeta.BillboardConstraints.FIXED);
        leftText.setAutoViewEntities(false);
        leftText.setBillboardConstraints(AbstractDisplayMeta.BillboardConstraints.FIXED);
        rightText.setAutoViewEntities(false);
        rightText.setBillboardConstraints(AbstractDisplayMeta.BillboardConstraints.FIXED);

        //给这个模型对象添加交互事件
        //处理玩家潜行交互时激活打桩
        getModelsData().eventNode().addListener(ModelInteractEvent.class, event ->{
            RPGPlayer rpgPlayer = (RPGPlayer) event.getInteracted();
            if(!trainingMode && rpgPlayer.isSneaking()){
                startTraining(rpgPlayer, 600);
            }
        });
        //实体受伤事件
        eventNode().addListener(EntityDamageEvent.class, event -> {
            if(event.isCancelled()) return;
            //如果记录了交互的玩家 那就只有对应的玩家才能攻击
            //否则取消事件
            if(interactivePlayer != null && event.getDamage().getSource() == interactivePlayer){
                //是否是打桩模式
                //是打桩模式的话要记录伤害
                //否则取消事件
                if(trainingMode){
                    float amount = event.getDamage().getAmount();
                    damageStatistics = damageStatistics + amount;
                    //记录最大伤害
                    if(amount > damageMax){
                        damageMax = amount;
                    }
                    Component text = Component.text("累计伤害: " + damageStatistics + " | 最高伤害: " + damageMax);
                    rightText.setText(text);
                }
            }else if (interactivePlayer != null){
                event.setCancelled(true);
            }

        });


    }



    /**
     * 开始训练
     * @param rpgPlayer
     * @param trainingTick
     * @return
     */
    public boolean startTraining(RPGPlayer rpgPlayer, Integer trainingTick){
        if(interactivePlayer != null) return false; //如果记录了交互的玩家 那就不允许启动
        if(trainingMode) return false; //如果已经是打桩模式 结束运行
        damageStatistics = 0.0f;
        this.trainingTick = trainingTick;
        interactivePlayer = rpgPlayer;
        topText.setText("准备中");
        interactivePlayer.showTitle(Title.title(Component.text("准备"), Component.text(""), 10, 30, 0));

        interactivePlayer.showTitle(Component.text("准备"), null, 10, 30, 0);
        scheduler().buildTask(()->{
            if (interactivePlayer.isOnline()) {
                interactivePlayer.showTitle(Component.text("3"), null, 0, 30, 0);
            }
        }).delay(TaskSchedule.tick(20)).schedule();
        scheduler().buildTask(()->{
            if (interactivePlayer.isOnline()) {
                interactivePlayer.showTitle(Component.text("2"), null, 0, 30, 0);
            }
        }).delay(TaskSchedule.tick(40)).schedule();
        scheduler().buildTask(()->{
            if (interactivePlayer.isOnline()) {
                interactivePlayer.showTitle(Component.text("1"), null, 0, 30, 0);
            }
        }).delay(TaskSchedule.tick(60)).schedule();
        scheduler().buildTask(()->{
            if (interactivePlayer.isOnline()) {
                interactivePlayer.showTitle(Component.text("开始"), null, 0, 10, 0);
                topText.setText(interactivePlayer.getUsername() + " -> 30s");
                trainingMode = true;
            }else {
                interactivePlayer = null;
                this.trainingTick = 0;
            }
        }).delay(TaskSchedule.tick(80)).schedule();
        Main.getLogger().debug("{} 激活了训练假人 {}", interactivePlayer.getUsername(), this);
        return true;
    }

    /**
     * 结束训练
     */
    public void endTraining(){
        Main.getLogger().debug("{} 激活的训练假人结束 {}", interactivePlayer.getUsername(), this);
        interactivePlayer.showTitle(Component.text("§6累计伤害: " + damageStatistics + " §f| §c最高伤害: " + damageMax), Component.text("§7秒伤: " + damageStatistics/30), 10, 100, 10);
        damageStatistics = 0.0f;
        trainingTick = 0;
        damageMax = 0.0f;
        interactivePlayer = null;
        trainingMode = false;
        rightText.setText("");
        topText.setText("潜行+右键开始打桩 -> 30s");
    }


    /**
     * 这里要重写update去 减少倒计时
     * @param time
     */
    @Override
    public void update(long time){
        //如果打桩模式在运行
        if(trainingMode){
            if(interactivePlayer == null || !interactivePlayer.isOnline()){
                endTraining();
            }
            if(trainingTick <= 0){
                endTraining();
                return;
            }
            //减少时间
            trainingTick--;
            //计算剩余秒数并更新
            //每20tick更新一次
            if(trainingTick % 20 == 0){
                int a = trainingTick/20;
                topText.setText(interactivePlayer.getUsername() + " -> " + a + "s");
            }
        }
        super.update(time);

    }






    /**
     * 使用配置生成一个实体
     * @param mobConfig
     * @param settingsId
     * @param instance
     * @param pos
     */
    @Override
    public void spawnEntity(MobConfig mobConfig, String settingsId, Instance instance, Pos pos) {
        setDisplayName(mobConfig.getDisplayName()).setDisplayHealth(mobConfig.getDisplayHealth());
        mobConfig.setSettings(settingsId, this);
        spawnEntity(instance, pos);
    }



    @Override
    public void spawnEntity(Instance instance, Pos pos){
        topText.spawnEntity(instance, pos.add(0, 2.2, 0).withPitch(0));
        leftText.spawnEntity(instance, LocationUtils.relativeLocationOffset(pos.withPitch(0), 0, 3, 3));
        rightText.spawnEntity(instance, LocationUtils.relativeLocationOffset(pos.withPitch(0), 0, -1.1, 1.8));
        super.spawnEntity(instance, pos.withPitch(0));
        setHealth((float) this.getAttribute(Attribute.MAX_HEALTH).attribute().maxValue());
    }


    @Override
    public void updateNewViewer(@NotNull Player player) {
        super.updateNewViewer(player);
        topText.addViewer(player);
        leftText.addViewer(player);
        rightText.addViewer(player);
    }

    @Override
    public void updateOldViewer(@NotNull Player player) {
        super.updateOldViewer(player);
        topText.removeViewer(player);
        leftText.removeViewer(player);
        rightText.removeViewer(player);
    }

    @Override
    public void remove(){
        super.remove();
        topText.remove();
        leftText.remove();
        rightText.remove();
    }


}
