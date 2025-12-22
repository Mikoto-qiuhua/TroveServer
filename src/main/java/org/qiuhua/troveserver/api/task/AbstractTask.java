package org.qiuhua.troveserver.api.task;

import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Consumer;

public abstract class AbstractTask implements ITask {
    protected static final AtomicLong GLOBAL_TASK_ID = new AtomicLong(0);

    protected final long taskId;
    protected final String taskName;
    protected final Runnable task;
    protected final Consumer<Exception> exceptionHandler;
    protected final long creationTime;

    protected volatile TaskStatus status = TaskStatus.CREATED;
    protected volatile boolean cancelled = false;
    protected volatile long lastRunTime = 0;
    protected volatile long runCount = 0;
    protected volatile Throwable lastException;

    protected AbstractTask(Builder<?> builder) {
        this.taskId = GLOBAL_TASK_ID.incrementAndGet();
        this.taskName = builder.taskName != null ? builder.taskName : getClass().getSimpleName() + "-" + taskId;
        this.task = builder.task;
        this.exceptionHandler = builder.exceptionHandler;
        this.creationTime = System.currentTimeMillis();
    }

    protected void executeTask() {
        if (cancelled) return;

        status = TaskStatus.RUNNING;
        lastRunTime = System.currentTimeMillis();

        try {
            task.run();
            runCount++;
            status = TaskStatus.COMPLETED;
        } catch (Exception e) {
            lastException = e;
            status = TaskStatus.FAILED;
            if (exceptionHandler != null) {
                exceptionHandler.accept(e);
            }
        }
    }

    @Override
    public void cancel() {
        this.cancelled = true;
        this.status = TaskStatus.CANCELLED;
    }

    // Getters
    @Override public long getTaskId() { return taskId; }
    @Override public String getTaskName() { return taskName; }
    @Override public TaskStatus getStatus() { return status; }
    @Override public boolean isCancelled() { return cancelled; }
    @Override public long getCreationTime() { return creationTime; }
    @Override public long getLastRunTime() { return lastRunTime; }
    @Override public long getRunCount() { return runCount; }


    /**
     * 通用Builder基类
     */
    public abstract static class Builder<T extends Builder<T>> {
        protected String taskName;
        protected Runnable task;
        protected Consumer<Exception> exceptionHandler;

        @SuppressWarnings("unchecked")
        public T name(String name) {
            this.taskName = name;
            return (T) this;
        }

        @SuppressWarnings("unchecked")
        public T task(Runnable task) {
            this.task = task;
            return (T) this;
        }

        @SuppressWarnings("unchecked")
        public T exceptionHandler(Consumer<Exception> handler) {
            this.exceptionHandler = handler;
            return (T) this;
        }

        public abstract AbstractTask build();
    }
}
