package org.qiuhua.troveserver.api.task;

public enum TaskStatus {
    CREATED,        // 已创建
    SCHEDULED,      // 已调度
    RUNNING,        // 运行中
    COMPLETED,      // 已完成
    CANCELLED,      // 已取消
    FAILED          // 执行失败
}
