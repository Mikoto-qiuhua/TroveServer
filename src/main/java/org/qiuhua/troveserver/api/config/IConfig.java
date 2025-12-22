package org.qiuhua.troveserver.api.config;

public interface IConfig {

    /**
     * 重载配置
     */
    void reload();

    /**
     * 加载配置
     */
    void load();

}
