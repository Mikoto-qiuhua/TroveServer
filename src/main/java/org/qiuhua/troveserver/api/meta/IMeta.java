package org.qiuhua.troveserver.api.meta;

import java.util.HashMap;
import java.util.Map;

import static org.qiuhua.troveserver.utils.yaml.util.NumberConversions.toDouble;
import static org.qiuhua.troveserver.utils.yaml.util.NumberConversions.toInt;

public interface IMeta {

    Map<String, Object> getMeta();

    default String getMetaString(String key) {
        Object def = getMeta().get(key);
        return getMetaString(key, def != null ? def.toString() : null);
    }

    default String getMetaString(String key, String def) {
        Object val = getMeta().get(key);
        return (val != null) ? val.toString() : def;
    }

    default int getMetaInt(String key) {
        Object def = getMeta().get(key);
        return getMetaInt(key, (def instanceof Number) ? toInt(def) : 0);
    }

    default int getMetaInt(String key, int def) {
        Object val = getMeta().get(key);
        return (val instanceof Number) ? toInt(val) : def;
    }


    default boolean getMetaBoolean(String key) {
        Object def = getMeta().get(key);
        return getMetaBoolean(key, (def instanceof Boolean) ? (Boolean) def : false);
    }

    default boolean getMetaBoolean(String key, boolean def) {
        Object val = getMeta().get(key);
        return (val instanceof Boolean) ? (Boolean) val : def;
    }


    default double getMetaDouble(String key) {
        Object def = getMeta().get(key);
        return getMetaDouble(key, (def instanceof Number) ? toDouble(def) : 0);
    }

    default double getMetaDouble(String key, double def) {
        Object val = getMeta().get(key);
        return (val instanceof Number) ? toDouble(val) : def;
    }

}
