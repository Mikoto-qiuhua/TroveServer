package org.qiuhua.troveserver.utils;

import org.qiuhua.troveserver.Main;

public class PerformanceTester {
    /**
     * 测量代码执行时间
     */
    public static long measureExecutionTime(Runnable task) {
        long startTime = System.currentTimeMillis();
        task.run();
        long endTime = System.currentTimeMillis();
        return endTime - startTime;
    }

    /**
     * 多次测试取平均值
     */
    public static void measureAverageTime(Runnable task, int iterations) {
        long totalTime = 0;
        for (int i = 0; i < iterations; i++) {
            totalTime += measureExecutionTime(task);
        }
        Main.getLogger().error("平均耗时 {}ms | 总耗时 {}ms", (totalTime / iterations), totalTime);
    }
}
