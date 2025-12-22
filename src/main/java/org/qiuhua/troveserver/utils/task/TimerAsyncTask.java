package org.qiuhua.troveserver.utils.task;

import lombok.Getter;
import org.qiuhua.troveserver.api.task.AbstractTask;
import org.qiuhua.troveserver.api.task.TaskStatus;

import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Consumer;

public class TimerAsyncTask extends AbstractTask {
    @Getter
    private final long delayTicks;
    @Getter
    private final long periodTicks;
    @Getter
    private final int maxExecutions;
    private final Consumer<TimerAsyncTask> tickHandler;
    private final Consumer<TimerAsyncTask> completionHandler;

    private final AtomicLong executionCount = new AtomicLong(0);
    private volatile boolean shouldContinue = true;

    private TimerAsyncTask(Builder builder) {
        super(builder);
        this.delayTicks = builder.delayTicks;
        this.periodTicks = builder.periodTicks;
        this.maxExecutions = builder.maxExecutions;
        this.tickHandler = builder.tickHandler;
        this.completionHandler = builder.completionHandler;
    }

    public void execute() {
        if (!shouldContinue) return;

        long currentCount = executionCount.incrementAndGet();
        executeTask();

        if (tickHandler != null) {
            tickHandler.accept(this);
        }

        // 检查是否达到最大执行次数
        if (maxExecutions > 0 && currentCount >= maxExecutions) {
            shouldContinue = false;
            status = TaskStatus.COMPLETED;
            if (completionHandler != null) {
                completionHandler.accept(this);
            }
        }
    }

    public long getDelayMillis() {
        return delayTicks * 50;
    }


    public boolean shouldContinue() {
        return shouldContinue && !cancelled &&
                (maxExecutions <= 0 || executionCount.get() < maxExecutions);
    }

    public long getExecutionCount() {
        return executionCount.get();
    }


    public static class Builder extends AbstractTask.Builder<Builder> {
        private long delayTicks = 0;
        private long periodTicks = 0;
        private int maxExecutions = 0; // 0表示无限次
        private Consumer<TimerAsyncTask> tickHandler;
        private Consumer<TimerAsyncTask> completionHandler;

        public Builder delay(long ticks) {
            this.delayTicks = ticks;
            return this;
        }

        public Builder period(long ticks) {
            this.periodTicks = ticks;
            return this;
        }

        public Builder maxExecutions(int max) {
            this.maxExecutions = max;
            return this;
        }

        public Builder tickHandler(Consumer<TimerAsyncTask> handler) {
            this.tickHandler = handler;
            return this;
        }

        public Builder completionHandler(Consumer<TimerAsyncTask> handler) {
            this.completionHandler = handler;
            return this;
        }

        @Override
        public TimerAsyncTask build() {
            if (task == null) {
                throw new IllegalArgumentException("任务不能为空");
            }
            if (periodTicks <= 0) {
                throw new IllegalArgumentException("对于定时器任务，周期必须大于 0");
            }
            return new TimerAsyncTask(this);
        }
    }
}
