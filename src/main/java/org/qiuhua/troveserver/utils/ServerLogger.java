package org.qiuhua.troveserver.utils;

import org.tinylog.Logger;

public class ServerLogger {
    public ServerLogger() {
    }

    // ANSI 颜色代码
    private static final String RESET = "\033[0m";
    private static final String RED = "\033[31m";
    private static final String GREEN = "\033[32m";
    private static final String YELLOW = "\033[33m";
    private static final String BLUE = "\033[34m";
    private static final String CYAN = "\033[36m";
    private static final String MAGENTA = "\033[35m";

    // 基础日志方法
    public void trace(String message) {
        Logger.trace(CYAN + message + RESET);
    }

    public void debug(String message) {
        Logger.debug(GREEN + message + RESET);
    }

    public void info(String message) {
        Logger.info(message); // INFO 保持原色
    }

    public void warn(String message) {
        Logger.warn(YELLOW + message + RESET);
    }

    public void error(String message) {
        Logger.error(RED + message + RESET);
    }

    public void error(String message, Throwable throwable) {
        Logger.error(throwable, RED + message + RESET);
    }

    // 支持参数化日志
    public void info(String format, Object... args) {
        Logger.info(format, args);
    }

    public void debug(String format, Object... args) {
        String coloredFormat = GREEN + format + RESET;
        Logger.debug(coloredFormat, args);
    }

    public void warn(String format, Object... args) {
        String coloredFormat = YELLOW + format + RESET;
        Logger.warn(coloredFormat, args);
    }

    public void error(String format, Object... args) {
        String coloredFormat = RED + format + RESET;
        Logger.error(coloredFormat, args);
    }
}