package org.qiuhua.troveserver.api.buff;

import java.util.concurrent.ConcurrentHashMap;

public interface IBuff {


    /**
     * 获取当前的buffMap
     * @return
     */
    ConcurrentHashMap<String, IBuffData> getBuffMap();


}
