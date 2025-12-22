package org.qiuhua.troveserver.module.role.listener;

import net.minestom.server.item.ItemStack;
import org.qiuhua.troveserver.Main;
import org.qiuhua.troveserver.entity.PlayerBindingModelEntity;
import org.qiuhua.troveserver.module.playermode.PlayerMode;
import org.qiuhua.troveserver.module.playermode.PlayerModeManager;
import org.qiuhua.troveserver.module.playermode.event.PlayerModeSwitchEvent;
import org.qiuhua.troveserver.module.role.EquipSlotData;
import org.qiuhua.troveserver.module.role.RoleData;
import org.qiuhua.troveserver.module.role.RoleManager;
import org.qiuhua.troveserver.module.role.event.RolePutEquipEvent;
import org.qiuhua.troveserver.module.role.event.RoleSwitchEvent;
import org.qiuhua.troveserver.module.role.event.RoleTakeEquipEvent;
import org.qiuhua.troveserver.player.RPGPlayer;

import java.util.concurrent.ConcurrentHashMap;

public class RoleArmsListener {

    /**
     * 记录了全部玩家的武器映射实体
     */
    public final static ConcurrentHashMap<RPGPlayer, PlayerBindingModelEntity> playerArmsMap = new ConcurrentHashMap<>();



    public RoleArmsListener(){
        RoleManager.roleNode.addListener(RoleSwitchEvent.class, this::onRoleSwitchEvent);
        PlayerModeManager.battleModeNode.addListener(RolePutEquipEvent.class, this::onRolePutEquipEvent);
        PlayerModeManager.battleModeNode.addListener(RoleTakeEquipEvent.class, this::onRoleTakeEquipEvent);
        PlayerModeManager.playerModeNode.addListener(PlayerModeSwitchEvent.class, this::onPlayerModeSwitchEvent);
    }


    /**
     * 角色切换完成事件
     * 这里要给玩家重新设置武器模型
     *
     * @param event
     */
    public void onRoleSwitchEvent(RoleSwitchEvent event){
        RPGPlayer rpgPlayer = event.getPlayer();
        if(rpgPlayer.getPlayerMode() == PlayerMode.Build) return;

        RoleData roleData = event.getRoleData();
        removeArmsModelEntity(rpgPlayer);
        String modelId = roleData.getArmsModelId();
        if(modelId == null) return;
        //重新按当前角色获取一个实体
        //获取武器
        ItemStack itemStack = ItemStack.AIR;
        EquipSlotData equipSlotData = roleData.getEquipSlotMap().get("arms");
        if(equipSlotData != null){
            itemStack = equipSlotData.getItemStack();
        }
        if(itemStack != ItemStack.AIR){
            spawnArmsModelEntity(rpgPlayer, modelId, itemStack);
        }
    }



    /**
     * 放入指定物品的时候要更新武器实体
     * @param event
     */
    public void onRolePutEquipEvent(RolePutEquipEvent event){
        RPGPlayer rpgPlayer = event.getPlayer();
        if(event.isCancelled()) return;
        if(rpgPlayer.getPlayerMode() == PlayerMode.Build) return;
        RoleData roleData = event.getRoleData();
        String modelId = roleData.getArmsModelId();
        if(modelId == null) return;
        if(rpgPlayer.getUseRoleData() == roleData){
            //获取武器
            ItemStack itemStack = event.getItemStack();
            if(itemStack != null && itemStack != ItemStack.AIR){
                switchArmsItem(rpgPlayer, modelId, itemStack);
            }
        }
    }

    /**
     * 拿走物品时也要更新
     * @param event
     */
    public void onRoleTakeEquipEvent(RoleTakeEquipEvent event){
        RPGPlayer rpgPlayer = event.getPlayer();
        if(event.isCancelled()) return;
        if(rpgPlayer.getUseRoleData() == event.getRoleData()){
            removeArmsModelEntity(rpgPlayer);
        }
    }

    /**
     * 切换模式时也要生成和移除
     * @param event
     */
    public void onPlayerModeSwitchEvent(PlayerModeSwitchEvent event){
        RPGPlayer rpgPlayer = event.getPlayer();
        if(event.isCancelled()) return;
        //建造模式移除
        if(event.getPlayerMode() == PlayerMode.Build){
            removeArmsModelEntity(rpgPlayer);
            return;
        }
        //战斗模式生成
        RoleData roleData = rpgPlayer.getUseRoleData();
        if(roleData == null) return;
        String modelId = roleData.getArmsModelId();
        ItemStack itemStack = ItemStack.AIR;
        EquipSlotData equipSlotData = roleData.getEquipSlotMap().get("arms");
        if(equipSlotData != null){
            itemStack = equipSlotData.getItemStack();
        }
        if(itemStack != ItemStack.AIR){
            spawnArmsModelEntity(rpgPlayer, modelId, itemStack);
        }
    }









    /**
     * 生成一个武器模型实体
     * @param rpgPlayer
     * @param modelId
     * @param itemStack
     */
    private void spawnArmsModelEntity(RPGPlayer rpgPlayer, String modelId, ItemStack itemStack){
        PlayerBindingModelEntity entity = new PlayerBindingModelEntity(modelId, rpgPlayer);
        entity.spawnEntity(rpgPlayer.getInstance(), rpgPlayer.getPosition(), itemStack);
        playerArmsMap.put(rpgPlayer, entity);
        Main.getLogger().debug("{} 生成 {} 武器模型", rpgPlayer.getUsername(), modelId);
    }

    /**
     * 移除武器模型实体
     * @param rpgPlayer
     */
    private void removeArmsModelEntity(RPGPlayer rpgPlayer){
        PlayerBindingModelEntity playerBindingModelEntity = playerArmsMap.get(rpgPlayer);
        if(playerBindingModelEntity != null){
            playerBindingModelEntity.remove();
            playerArmsMap.remove(rpgPlayer);
            Main.getLogger().debug("{} 移除 {} 武器模型", rpgPlayer.getUsername(), playerBindingModelEntity.getModelsData().getId());
        }
    }




    /**
     * 切换当前
     * @param rpgPlayer
     * @param itemStack
     */
    private void switchArmsItem(RPGPlayer rpgPlayer, String modelId, ItemStack itemStack){
        PlayerBindingModelEntity playerBindingModelEntity = playerArmsMap.get(rpgPlayer);
        if(playerBindingModelEntity == null){
            spawnArmsModelEntity(rpgPlayer, modelId, itemStack);
            return;
        }
        playerBindingModelEntity.getModelsData().setModelItem("left_item", itemStack);
        playerBindingModelEntity.getModelsData().setModelItem("right_item", itemStack);
        if(playerBindingModelEntity.getInstance() == null) {
            playerBindingModelEntity.spawnEntity(rpgPlayer.getInstance(), rpgPlayer.getPosition(), itemStack);
        }
        Main.getLogger().debug("{} 切换 {} 武器模型", rpgPlayer.getUsername(), modelId);
    }




}
