package com.yozosoft.license.common.constant;

public class SysConstant {

    public static final String SEPARATOR = "#";

    public static final Long HEALTH_INITIAL_DELAY = 10*1000L;

    public static final Long HEALTH_DELAY = 60*1000L;

    public static final Long BEAT_PROCESSOR_DELAY = 0L;

    public static final String CHARSET = "UTF-8";

    public static final String LICENSE_FILE_NAME = "license.eni";

    /**
     * 存在redis里秘钥的前缀
     */
    public static final String REDIS_SECRET_PREFIX = "secret" + SEPARATOR;
    /**
     * 存在redis里inst的前缀
     */
    public static final String REDIS_INSTANCE_PREFIX = "instance" + SEPARATOR;

    /**
     * 在线已有并发数key
     */
    public static final String ONLINE_CONCURRENCY_NUM = "onlineConcurrencyNum";

    /**
     * redis 分布式锁 key
     */
    public static final String CHECK_BEAT_TIMEOUT_LOCK_KEY = "checkBeatTimeoutLock";
}
