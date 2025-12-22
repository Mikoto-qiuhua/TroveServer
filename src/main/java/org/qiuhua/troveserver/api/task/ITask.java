package org.qiuhua.troveserver.api.task;

public interface ITask {
    long getTaskId();
    String getTaskName();
    TaskStatus getStatus();
    boolean isCancelled();
    void cancel();
    long getCreationTime();
    long getLastRunTime();
    long getRunCount();
}
