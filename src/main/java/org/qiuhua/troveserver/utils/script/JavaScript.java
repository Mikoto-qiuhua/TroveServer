package org.qiuhua.troveserver.utils.script;


import net.minestom.server.item.ItemStack;
import org.qiuhua.troveserver.Main;
import org.qiuhua.troveserver.player.RPGPlayer;
import org.qiuhua.troveserver.utils.script.wrapper.ItemWrapper;
import org.qiuhua.troveserver.utils.script.wrapper.PlayerWrapper;

import javax.script.*;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class JavaScript {

    private static final ScriptEngineManager engineManager = new ScriptEngineManager();
    private static final Map<String, CompiledScript> compiledScripts = new ConcurrentHashMap<>();

    /**
     * 获取 ScriptEngine 实例
     */
    private static ScriptEngine getEngine() {
        return engineManager.getEngineByName("nashorn");
    }

    /**
     * 执行 JavaScript
     */
    public static Object evaluate(String conditionScript, ItemStack item) {
        if (conditionScript == null || conditionScript.trim().isEmpty()) {
            return true;
        }
        try {
            ScriptEngine engine = getEngine();
            Bindings bindings = engine.createBindings();
            bindings.put("item", new ItemWrapper(item));
            return evalWithCache(engine, conditionScript, bindings); //使用缓存执行脚本

        } catch (Exception e) {
            Main.getLogger().error("JS执行错误: {}", conditionScript);
            Main.getLogger().error("错误信息: {}", e.getMessage());
            return false;
        }
    }

    /**
     * 执行 JavaScript
     */
    public static Object evaluate(String conditionScript, RPGPlayer rpgPlayer) {
        if (conditionScript == null || conditionScript.trim().isEmpty()) {
            return true;
        }
        try {
            ScriptEngine engine = getEngine();
            Bindings bindings = engine.createBindings();
            bindings.put("player", new PlayerWrapper(rpgPlayer));
            return evalWithCache(engine, conditionScript, bindings); //使用缓存执行脚本

        } catch (Exception e) {
            Main.getLogger().error("JS执行错误: {}", conditionScript);
            Main.getLogger().error("错误信息: {}", e.getMessage());
            return false;
        }
    }

    /**
     * 执行 JavaScript
     */
    public static Object evaluate(String conditionScript, RPGPlayer rpgPlayer, ItemStack item) {
        if (conditionScript == null || conditionScript.trim().isEmpty()) {
            return true;
        }
        try {
            ScriptEngine engine = getEngine();
            Bindings bindings = engine.createBindings();
            bindings.put("player", new PlayerWrapper(rpgPlayer));
            bindings.put("item", new ItemWrapper(item));
            return evalWithCache(engine, conditionScript, bindings); //使用缓存执行脚本

        } catch (Exception e) {
            Main.getLogger().error("JS执行错误: {}", conditionScript);
            Main.getLogger().error("错误信息: {}", e.getMessage());
            return false;
        }
    }






    /**
     * 预编译脚本并缓存
     */
    public static void precompile(String script) {
        if (script == null || script.trim().isEmpty()) {
            return;
        }
        compiledScripts.computeIfAbsent(script, key -> {
            try {
                ScriptEngine engine = getEngine();
                if (engine instanceof Compilable) {
                    return ((Compilable) engine).compile(script);
                }
            } catch (ScriptException e) {
                Main.getLogger().warn("脚本预编译失败: {} - {}", script, e.getMessage());
            }
            return null;
        });
    }



    /**
     * 预热脚本（执行一次以触发 JIT 编译）
     */
    public static void warmUp(String script) {
        precompile(script);
        try {
            ScriptEngine engine = getEngine();
            Bindings bindings = engine.createBindings();
            CompiledScript compiled = compiledScripts.get(script);
            if (compiled != null) {
                compiled.eval(bindings);
            } else {
                engine.eval(script, bindings);
            }
            //Main.getLogger().debug("脚本预热完成: {}", script);
        } catch (Exception e) {
            //Main.getLogger().debug("脚本预热失败(可忽略): {}", e.getMessage());
        }
        Main.getLogger().debug(compiledScripts.toString());
    }

    /**
     * 批量执行脚本
     */
    public static Map<String, Object> evaluateBatch(Map<String, String> scripts, ItemStack item) {
        Map<String, Object> results = new ConcurrentHashMap<>();
        scripts.entrySet().parallelStream().forEach(entry -> {
            String key = entry.getKey();
            String script = entry.getValue();
            try {
                results.put(key, evaluate(script, item));
            } catch (Exception e) {
                Main.getLogger().warn("批量执行脚本失败 [{}]: {}", key, e.getMessage());
                results.put(key, false);
            }
        });
        return results;
    }




    /**
     * 清理缓存
     */
    public static void clearCache() {
        compiledScripts.clear();
        Main.getLogger().info("Nashorn 脚本缓存已清理");
    }


    /**
     * 使用缓存执行脚本
     */
    private static Object evalWithCache(ScriptEngine engine, String script, Bindings bindings) throws ScriptException {
        CompiledScript compiled = compiledScripts.get(script);
        if (compiled != null) {
            return compiled.eval(bindings);
        }
        // 如果没有缓存，编译并缓存
        if (engine instanceof Compilable) {
            compiled = ((Compilable) engine).compile(script);
            compiledScripts.put(script, compiled);
            return compiled.eval(bindings);
        } else {
            return engine.eval(script, bindings);
        }
    }






}
