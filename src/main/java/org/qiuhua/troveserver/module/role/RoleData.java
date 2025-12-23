package org.qiuhua.troveserver.module.role;

import lombok.Getter;
import lombok.Setter;
import net.kyori.adventure.text.Component;
import net.minestom.server.MinecraftServer;
import net.minestom.server.item.ItemStack;
import org.jetbrains.annotations.Nullable;
import org.qiuhua.troveserver.Main;
import org.qiuhua.troveserver.module.role.event.*;
import org.qiuhua.troveserver.player.RPGPlayer;
import org.qiuhua.troveserver.module.attribute.AttributeCompileGroup;
import org.qiuhua.troveserver.module.attribute.AttributeManager;
import org.qiuhua.troveserver.module.attribute.AttributeNBTData;
import org.qiuhua.troveserver.module.item.ItemCompileData;
import org.qiuhua.troveserver.utils.yaml.ConfigurationSection;
import org.qiuhua.troveserver.utils.yaml.file.YamlConfiguration;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class RoleData {

    /**
     * 这个数据所属的玩家对象
     */
    @Getter
    private final RPGPlayer rpgPlayer;

    /**
     * 角色的id
     */
    @Getter
    private final String roleId;


    /**
     * 角色是否不可见
     */
    @Getter
    private final Boolean hide;

    /**
     * 角色是否处于测试模式
     */
    @Getter
    private final Boolean testModel;

    /**
     * 解锁状态 所有角色默认是未解锁
     */
    @Getter
    @Setter
    private RoleUnlockedState roleUnlockedState = RoleUnlockedState.NotUnlocked;

    /**
     * 经验的追踪列表
     */
    @Getter
    private final List<String> expTraceList = new ArrayList<>();


    /**
     * 每个等级的经验数据
     */
    @Getter
    private final NavigableMap<Integer, RoleLevelExpData> levelExpDataMap = new TreeMap<>();

    /**
     * 角色的当前等级
     */
    @Getter
    private Integer level;

    /**
     * 当前经验值
     */
    @Getter
    private Integer exp;

    /**
     * 升到下一级需要的经验值
     */
    @Getter
    private Integer expMax;

    /**
     * 装备槽位数据
     */
    @Getter
    private final Map<String, EquipSlotData> equipSlotMap = new HashMap<>();

    /**
     * 存储了当前角色自身提供的属性数据
     */
    @Getter
    private final Map<String, AttributesData> attributesDataMap = new HashMap<>();

    /**
     * 缓存的属性合集
     */
    @Getter
    private AttributeCompileGroup attributeCompileGroup;

    /**
     * 技能列表
     */
    @Getter
    private final List<RoleSkillData> roleSkillDataList = new ArrayList<>();

    /**
     * 这个角色使用的武器模型实体
     */
    @Getter
    private final String armsModelId;

    public RoleData(String roleId, YamlConfiguration config, RPGPlayer rpgPlayer) {
        this.rpgPlayer = rpgPlayer;
        this.roleId = roleId;
        hide = config.getBoolean("Hide", false);
        testModel = config.getBoolean("TestModel", false);
        //加载trace
        expTraceList.addAll(config.getStringList("Level.trace"));
        //初始化等级
        level = config.getInt("Level.base", 1);
        armsModelId = config.getString("ArmsModelId", null);

        //加载RoleLevelExpData
        ConfigurationSection levelSection = config.getConfigurationSection("Level.key");
        for(String key : levelSection.getKeys(false)){
            Integer level = Integer.parseInt(key);
            RoleLevelExpData roleLevelExpData = new RoleLevelExpData(levelSection.getConfigurationSection(key), level, this);
            levelExpDataMap.put(level, roleLevelExpData);
        }

        //获取当前等级数据
        RoleLevelExpData levelData = getLevelExpData(level);

        //初始化等级信息
        exp = 0;
        expMax = levelData != null ? levelData.getExp() : 0;

        //初始化槽位信息加载equipSlotMap
        ConfigurationSection equipSection = config.getConfigurationSection("Equip");
        for(String key : equipSection.getKeys(false)){
            EquipSlotData equipSlotData = new EquipSlotData(key, equipSection.getConfigurationSection(key), this);
            equipSlotMap.put(key, equipSlotData);
        }

        //加载属性数据
        ConfigurationSection attributesSection = config.getConfigurationSection("Attributes");
        if(attributesSection != null){
            for(String key : attributesSection.getKeys(false)){
                AttributesData attributesData = new AttributesData(key, attributesSection.getConfigurationSection(key));
                attributesDataMap.put(key, attributesData);
            }
        }
        //加载技能配置
        ConfigurationSection skillSection = config.getConfigurationSection("Skills");
        if(skillSection != null){
            for (String key : skillSection.getKeys(false)){
                roleSkillDataList.add(new RoleSkillData(key, skillSection.getConfigurationSection(key)));
            }
        }

        Main.getLogger().debug("{} 的角色 {} 加载 {}", rpgPlayer.getUsername(), roleId, toString());
        updateAttribute();
    }


    /**
     * 从槽位中获取武器物品
     * @return
     */
    public ItemStack getArms(){
        EquipSlotData equipSlotData = equipSlotMap.get("arms");
        //没有这个槽位就返回空气
        if(equipSlotData == null) return ItemStack.AIR;
        return equipSlotData.getItemStack();
    }



    /**
     * 当前角色释放技能 触发器 -1=左键  其余为对应的快捷栏按键 正常情况只支持到0-3 0为右键技能
     * @param trigger
     * @return
     */
    public void castSkill(int trigger){
//        roleSkillDataList.forEach(roleSkillData -> {
//            if(roleSkillData.getTrigger() == trigger){
//                RoleCastSkillEvent roleCastSkillEvent = new RoleCastSkillEvent(rpgPlayer, this, roleSkillData.getSkillName());
//                MinecraftServer.getGlobalEventHandler().call(roleCastSkillEvent);
//                if(roleCastSkillEvent.isCancelled()) return;
//                RoleManager.castSkill(rpgPlayer, roleCastSkillEvent.getSkillName(), roleSkillData.getMetaData(1));
//            }
//        });

    }



    /**
     * 往指定槽位添加一个装备
     * @param equipSlotData
     * @param itemStack
     * @return
     */
    public Boolean putEquipItem(EquipSlotData equipSlotData, ItemStack itemStack){
        if(roleUnlockedState == RoleUnlockedState.NotUnlocked) return false;
        if(equipSlotData == null) return false;
        //检查物品是否符合条件
        if(equipSlotData.checkCondition(itemStack, rpgPlayer)){
            //触发装备放入事件
            RolePutEquipEvent rolePutEquipEvent = new RolePutEquipEvent(rpgPlayer, this, equipSlotData, itemStack);
            MinecraftServer.getGlobalEventHandler().call(rolePutEquipEvent);

            if(rolePutEquipEvent.isCancelled()) return false;

            equipSlotData.putItemStack(rolePutEquipEvent.getItemStack());
            updateAttribute();
            Main.getLogger().debug("{} 往角色 {} 添加了一个 {} 物品", rpgPlayer.getUsername(), roleId, equipSlotData.getEquipId());
            return true;

        }else {
            Main.getLogger().debug("{} 不满足添加 {} 物品 {} 条件", rpgPlayer.getUsername(), equipSlotData.getEquipId(), equipSlotData.getCondition());
        }
        return false;
    }


    /**
     * 从指定槽位拿取物品
     * @param equipSlotData
     * @return
     */
    public ItemStack takeEquipItem(EquipSlotData equipSlotData){
        if(roleUnlockedState == RoleUnlockedState.NotUnlocked) return ItemStack.AIR;
        if(equipSlotData == null) return ItemStack.AIR;
        RoleTakeEquipEvent roleTakeEquipEvent = new RoleTakeEquipEvent(rpgPlayer, this, equipSlotData, equipSlotData.getItemStack());
        MinecraftServer.getGlobalEventHandler().call(roleTakeEquipEvent);

        if(roleTakeEquipEvent.isCancelled()) return ItemStack.AIR;

        Main.getLogger().debug("{} 往角色 {} 拿取了一个 {} 物品", rpgPlayer.getUsername(), roleId, equipSlotData.getEquipId());
        equipSlotData.takeItemStack();
        updateAttribute();
        return roleTakeEquipEvent.getItemStack();
    }


    /**
     * 更新缓存的属性合集
     */
    public void updateAttribute(){
        List<AttributeNBTData> attributeNBTDataList = new ArrayList<>();
        //先获取角色自身的属性
        attributesDataMap.values().forEach(attributesData -> {
            attributeNBTDataList.add(AttributeManager.getAttributeNBTData(attributesData.getStringResult(level)));
        });
        //获取物品的
        equipSlotMap.values().forEach(equipSlotData -> {
            ItemStack itemStack = equipSlotData.getItemStack();
            if(itemStack != ItemStack.AIR){
                attributeNBTDataList.addAll(AttributeManager.getItemAttributeNBTData(itemStack));
            }
        });
        attributeCompileGroup = new AttributeCompileGroup(attributeNBTDataList);
        //如果玩家本身使用的是这个角色 那应当同步更新玩家当前属性
        if(rpgPlayer.getUseRoleData() == this){
            //添加属性
            rpgPlayer.addAttribute(attributeCompileGroup, "role_attribute");
            rpgPlayer.updateAttribute();
        }

    }


    /**
     * 获取 ≥level 的最小等级数据（如level=5，{3:A,6:B}返回B）
     * @param level 目标等级（必须 ≥1）
     * @return 对应的LevelExpData，若无更高等级则返回null
     */
    @Nullable
    public RoleLevelExpData getLevelExpData(Integer level) {
        Integer key = levelExpDataMap.ceilingKey(level);  // 获取 ≥level 的最小Key
        return key != null ? levelExpDataMap.get(key) : null;
    }

    /**
     * 添加经验值并处理升级逻辑 会检查经验追踪
     * @param amount 要添加的经验值
     * @param trace 经验来源标签
     * @return 是否升级成功
     */
    public boolean addExp(int amount, String trace){
        //如果角色未解锁
        if(roleUnlockedState == RoleUnlockedState.NotUnlocked){
            return false;
        }
        if(testModel) return false;
        if(!expTraceList.contains(trace)) return false;
        if(amount <= 0) return false;

        RoleAddExpEvent roleAddExpEvent = new RoleAddExpEvent(rpgPlayer, this, amount);
        MinecraftServer.getGlobalEventHandler().call(roleAddExpEvent);
        if(roleAddExpEvent.isCancelled()) return false;
        amount = roleAddExpEvent.getExp();

        //防止整数溢出
        long newExp = (long)this.exp + amount;
        if(newExp > Integer.MAX_VALUE) {
            this.exp = Integer.MAX_VALUE;
        } else {
            this.exp = (int)newExp;
        }
        Main.getLogger().debug("{} 的角色 {} 获取经验 {}/{}", rpgPlayer.getUsername(), roleId, amount, this.exp);
        if(checkLevelUp()){
            //这里要实现属性更新
            Main.getLogger().debug("这里要实现属性更新");



            return true;
        }
        return false;
    }

    /**
     * 添加经验值并处理升级逻辑 不检查经验追踪
     * @param amount 要添加的经验值
     * @return 是否升级成功
     */
    public boolean addExp(int amount){
        //如果角色未解锁
        if(roleUnlockedState == RoleUnlockedState.NotUnlocked){
            return false;
        }
        if(testModel) return false;
        if(amount <= 0) return false;

        RoleAddExpEvent roleAddExpEvent = new RoleAddExpEvent(rpgPlayer, this, amount);
        MinecraftServer.getGlobalEventHandler().call(roleAddExpEvent);
        if(roleAddExpEvent.isCancelled()) return false;
        amount = roleAddExpEvent.getExp();

        //防止整数溢出
        long newExp = (long)this.exp + amount;

        if(newExp > Integer.MAX_VALUE) {
            this.exp = Integer.MAX_VALUE;
        } else {
            this.exp = (int)newExp;
        }
        Main.getLogger().debug("{} 的角色 {} 获取经验 {}/{}", rpgPlayer.getUsername(), roleId, amount, this.expMax);
        if(checkLevelUp()){
            //这里要实现属性更新
            Main.getLogger().debug("这里要实现属性更新");



            return true;
        }
        return false;
    }

    /**
     * 检查并执行升级
     */
    private boolean checkLevelUp() {
        boolean leveledUp = false;
        while(canLevelUp()) {
            if(!performLevelUp()) break;
            leveledUp = true;
        }
        return leveledUp;
    }

    /**
     * 检查是否可以升级
     */
    private boolean canLevelUp() {
        RoleLevelExpData currentData = getLevelExpData(level);
        if(currentData == null) return false;

        return getLevelExpData(level + 1) != null      // 存在下一级
                && exp >= currentData.getExp()          // 经验足够
                && currentData.checkCondition(rpgPlayer);  // 条件满足
    }

    /**
     * 执行升级逻辑
     */
    private boolean performLevelUp() {
        RoleLevelExpData currentData = getLevelExpData(level);
        if (currentData == null || exp < currentData.getExp()) {
            return false;
        }

        //保存旧等级用于事件
        int oldLevel = this.level;
        int newLevel = this.level + 1;

        //触发事件
        RoleLevelUpEvent roleLevelUpEvent = new RoleLevelUpEvent(rpgPlayer, this, oldLevel, newLevel);
        MinecraftServer.getGlobalEventHandler().call(roleLevelUpEvent);
        //如果事件被取消 那就不升级了
        if(roleLevelUpEvent.isCancelled()) return false;

        // 扣除经验并升级
        this.exp -= currentData.getExp();
        this.level++;

        // 检查是否达到最高等级
        RoleLevelExpData nextData = getLevelExpData(level+1);
        if (nextData != null) {
            // 非最高级：正常设置下一级所需经验
            this.expMax = nextData.getExp();
        } else {
            // 最高级特殊处理：exp和expMax都设为0
            this.exp = 0;
            this.expMax = 0;
            Main.getLogger().debug("{} 的角色 {} 达到最高等级", rpgPlayer.getUsername(), roleId);
        }
        currentData.runActions(rpgPlayer);
        Main.getLogger().debug("{} 的角色 {} 级成功 经验值: {}/{} | 当前等级 {}", rpgPlayer.getUsername(), roleId, exp, expMax, level);
        return true;
    }


    @Override
    public String toString(){
        return "hide: " + hide +
                ", TestModel: " + testModel +
                ", expTrace: " + expTraceList +
                ", levelExpDataSize: " + levelExpDataMap.size() +
                ", equipSlotSize: " + equipSlotMap.size() +
                ", attributesDataSize: " + attributesDataMap.size() +
                ", roleSkillDataListSize: " + roleSkillDataList.size();
    }

}
