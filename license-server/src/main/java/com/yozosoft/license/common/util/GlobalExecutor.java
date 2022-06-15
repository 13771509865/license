package com.yozosoft.license.common.util;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

public class GlobalExecutor {

    /**
     * 后续待优化
     */
    public static final ScheduledExecutorService BEAT_PROCESSOR_EXECUTOR = Executors.newScheduledThreadPool(Runtime.getRuntime().availableProcessors());
}
