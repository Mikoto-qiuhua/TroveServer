package org.qiuhua.troveserver.utils.task;

import org.qiuhua.troveserver.api.task.AbstractTask;

public class AsyncTask extends AbstractTask {
    private AsyncTask(Builder builder) {
        super(builder);
    }

    public void execute() {
        executeTask();
    }

    public static class Builder extends AbstractTask.Builder<Builder> {
        @Override
        public AsyncTask build() {
            if (task == null) {
                throw new IllegalArgumentException("任务不能为空");
            }
            return new AsyncTask(this);
        }
    }
}
