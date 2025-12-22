package org.qiuhua.troveserver.utils.task;

import lombok.Getter;
import lombok.Setter;
import org.jetbrains.annotations.NotNull;
import org.qiuhua.troveserver.api.task.*;

import java.util.List;
import java.util.Map;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

public class SchedulerManager {
    private static ScheduledExecutorService scheduler;
    private static ExecutorService asyncExecutor;
    private static final Map<Long, TaskWrapper<?>> runningTasks = new ConcurrentHashMap<>();
    private static final AtomicLong totalTasksExecuted = new AtomicLong(0);

    public static void init() {
        scheduler = Executors.newScheduledThreadPool(
                Runtime.getRuntime().availableProcessors(),
                new ThreadFactory() {
                    private final AtomicLong counter = new AtomicLong(0);
                    @Override
                    public Thread newThread(@NotNull Runnable r) {
                        Thread thread = new Thread(r, "Scheduler-" + counter.incrementAndGet());
                        thread.setDaemon(true);
                        return thread;
                    }
                }
        );
        asyncExecutor = Executors.newCachedThreadPool(
                new ThreadFactory() {
                    private final AtomicLong counter = new AtomicLong(0);
                    @Override
                    public Thread newThread(@NotNull Runnable r) {
                        Thread thread = new Thread(r, "AsyncWorker-" + counter.incrementAndGet());
                        thread.setDaemon(true);
                        return thread;
                    }
                }
        );
    }

    // ========== 异步任务管理 ==========

    /**
     * 提交异步任务（立即执行）
     */
    private static AsyncTask submitAsync(AsyncTask task) {
        TaskWrapper<AsyncTask> wrapper = new TaskWrapper<>(task);
        runningTasks.put(task.getTaskId(), wrapper);

        asyncExecutor.submit(() -> {
            wrapper.execute();
            totalTasksExecuted.incrementAndGet();
            runningTasks.remove(task.getTaskId());
        });

        return task;
    }

    /**
     * 快捷方法：创建并提交异步任务
     */
    public static AsyncTask submitAsync(Runnable task) {
        return submitAsync(new AsyncTask.Builder().task(task).build());
    }

    public static AsyncTask submitAsync(String name, Runnable task) {
        return submitAsync(new AsyncTask.Builder().name(name).task(task).build());
    }

    /**
     * 提交延迟异步任务
     */
    private static DelayedAsyncTask scheduleDelayed(DelayedAsyncTask task) {
        TaskWrapper<DelayedAsyncTask> wrapper = new TaskWrapper<>(task);
        runningTasks.put(task.getTaskId(), wrapper);

        ScheduledFuture<?> future = scheduler.schedule(() -> {
            wrapper.execute();
            totalTasksExecuted.incrementAndGet();
            runningTasks.remove(task.getTaskId());
        }, task.getDelayMillis(), TimeUnit.MILLISECONDS);

        wrapper.setFuture(future);
        return task;
    }

    /**
     * 快捷方法：创建并提交延迟任务
     */
    public static DelayedAsyncTask scheduleDelayed(long delayTicks, Runnable task) {
        return scheduleDelayed(new DelayedAsyncTask.Builder()
                .delay(delayTicks)
                .task(task)
                .build());
    }

    public static DelayedAsyncTask scheduleDelayed(String name, long delayTicks, Runnable task) {
        return scheduleDelayed(new DelayedAsyncTask.Builder()
                .name(name)
                .delay(delayTicks)
                .task(task)
                .build());
    }

    // ========== 定时异步任务管理 ==========

    /**
     * 提交定时异步任务
     */
    private static TimerAsyncTask scheduleTimer(TimerAsyncTask task) {
        TaskWrapper<TimerAsyncTask> wrapper = new TaskWrapper<>(task);
        runningTasks.put(task.getTaskId(), wrapper);

        ScheduledFuture<?> future = scheduler.scheduleAtFixedRate(() -> {
            if (!task.shouldContinue()) {
                cancelTask(task.getTaskId());
                return;
            }

            wrapper.execute();
            totalTasksExecuted.incrementAndGet();
        }, task.getDelayMillis(), task.getPeriodTicks() * 50, TimeUnit.MILLISECONDS);

        wrapper.setFuture(future);
        return task;
    }




    /**
     * 快捷方法：创建并提交定时任务
     */
    public static TimerAsyncTask scheduleTimer(long delayTicks, long periodTicks, Runnable task) {
        return scheduleTimer(new TimerAsyncTask.Builder()
                .delay(delayTicks)
                .period(periodTicks)
                .task(task)
                .build());
    }

    public static TimerAsyncTask scheduleTimer(String name, long delayTicks, long periodTicks, Runnable task) {
        return scheduleTimer(new TimerAsyncTask.Builder()
                .name(name)
                .delay(delayTicks)
                .period(periodTicks)
                .task(task)
                .build());
    }

    // ========== 任务控制 ==========

    /**
     * 取消任务
     */
    public static boolean cancelTask(long taskId) {
        TaskWrapper<?> wrapper = runningTasks.get(taskId);
        if (wrapper != null) {
            wrapper.cancel();
            runningTasks.remove(taskId);
            return true;
        }
        return false;
    }

    /**
     * 取消所有任务
     */
    public static void cancelAllTasks() {
        for (TaskWrapper<?> wrapper : runningTasks.values()) {
            wrapper.cancel();
        }
        runningTasks.clear();
    }

    /**
     * 获取任务状态
     */
    public static TaskStatus getTaskStatus(long taskId) {
        TaskWrapper<?> wrapper = runningTasks.get(taskId);
        return wrapper != null ? wrapper.getTask().getStatus() : null;
    }

    /**
     * 获取所有运行中的任务
     */
    public static List<ITask> getRunningTasks() {
        return runningTasks.values().stream()
                .map(TaskWrapper::getTask)
                .collect(Collectors.toList());
    }

    /**
     * 按类型获取任务
     */
    @SuppressWarnings("unchecked")
    public static  <T extends ITask> List<T> getRunningTasks(Class<T> taskType) {
        return runningTasks.values().stream()
                .map(TaskWrapper::getTask)
                .filter(taskType::isInstance)
                .map(task -> (T) task)
                .collect(Collectors.toList());
    }

    // ========== 统计信息 ==========

    public static SchedulerStats getStats() {
        return new SchedulerStats(
                runningTasks.size(),
                totalTasksExecuted.get(),
                ((ThreadPoolExecutor) asyncExecutor).getActiveCount(),
                ((ThreadPoolExecutor) scheduler).getActiveCount()
        );
    }

    /**
     * 关闭调度器
     */
    public static void shutdown() {
        cancelAllTasks();
        scheduler.shutdown();
        asyncExecutor.shutdown();
        try {
            if (!scheduler.awaitTermination(5, TimeUnit.SECONDS)) {
                scheduler.shutdownNow();
            }
            if (!asyncExecutor.awaitTermination(5, TimeUnit.SECONDS)) {
                asyncExecutor.shutdownNow();
            }
        } catch (InterruptedException e) {
            scheduler.shutdownNow();
            asyncExecutor.shutdownNow();
            Thread.currentThread().interrupt();
        }
    }

    // ========== 内部包装类 ==========

    private static class TaskWrapper<T extends ITask> {
        @Getter
        private final T task;
        @Setter
        private ScheduledFuture<?> future;

        public TaskWrapper(T task) {
            this.task = task;
        }

        public void execute() {
            if (task instanceof AsyncTask) {
                ((AsyncTask) task).execute();
            } else if (task instanceof DelayedAsyncTask) {
                ((DelayedAsyncTask) task).execute();
            } else if (task instanceof TimerAsyncTask) {
                ((TimerAsyncTask) task).execute();
            }
        }

        public void cancel() {
            task.cancel();
            if (future != null) {
                future.cancel(false);
            }
        }

    }





    /**
     * 统计信息类
     */
    public record SchedulerStats(int runningTasks, long totalTasksExecuted, int activeAsyncWorkers,
                                 int activeSchedulerThreads) {
    @Override
        public String toString() {
            return String.format(
                    "运行中任务: %d, 总执行数: %d, 活跃工作线程: %d, 活跃调度线程: %d",
                    runningTasks, totalTasksExecuted, activeAsyncWorkers, activeSchedulerThreads
            );
        }
    }

}
