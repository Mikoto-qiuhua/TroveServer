package org.qiuhua.troveserver.utils.script.wrapper;

import org.qiuhua.troveserver.player.RPGPlayer;

public class PlayerWrapper {

    private final RPGPlayer rpgPlayer;

    public PlayerWrapper(RPGPlayer rpgPlayer){
        this.rpgPlayer = rpgPlayer;
    }

    /**
     * 获取原始对象
     * @return
     */
    public RPGPlayer getOriginal() {
        return rpgPlayer;
    }


}
