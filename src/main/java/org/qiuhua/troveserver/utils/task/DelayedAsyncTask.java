package org.qiuhua.troveserver.utils.task;

import lombok.Getter;
import org.qiuhua.troveserver.api.task.AbstractTask;
import org.qiuhua.troveserver.api.task.TaskStatus;

import java.util.function.Consumer;

public class DelayedAsyncTask extends AbstractTask {
    @Getter
    private final long delayTicks;
    private final Consumer<DelayedAsyncTask> completionHandler;

    private DelayedAsyncTask(Builder builder) {
        super(builder);
        this.delayTicks = builder.delayTicks;
        this.completionHandler = builder.completionHandler;
    }

    public void execute() {
        executeTask();
        if (completionHandler != null && !cancelled && status != TaskStatus.FAILED) {
            completionHandler.accept(this);
        }
    }

    public long getDelayMillis() {
        return delayTicks * 50;
    }

    public static class Builder extends AbstractTask.Builder<Builder> {
        private long delayTicks = 0;
        private Consumer<DelayedAsyncTask> completionHandler;

        public Builder delay(long ticks) {
            this.delayTicks = ticks;
            return this;
        }

        public Builder completionHandler(Consumer<DelayedAsyncTask> handler) {
            this.completionHandler = handler;
            return this;
        }

        @Override
        public DelayedAsyncTask build() {
            if (task == null) {
                throw new IllegalArgumentException("任务不能为空");
            }
            return new DelayedAsyncTask(this);
        }
    }
}
