package com.yozosoft.license.task;

import com.alibaba.fastjson.JSON;
import com.yozosoft.license.common.constant.SysConstant;
import com.yozosoft.license.config.LicenseConfig;
import com.yozosoft.license.model.Instance;
import com.yozosoft.license.service.instance.InstanceManager;
import com.yozosoft.license.service.instance.InstanceService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.data.redis.core.Cursor;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ScanOptions;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

@Component
@Slf4j
public class CheckBeatTimeoutTask implements ApplicationRunner {

    @Autowired
    private LicenseConfig licenseConfig;

    @Autowired
    private RedisTemplate<String,String> redisTemplate;

    @Autowired
    private InstanceManager instanceManager;

    @Override
    public void run(ApplicationArguments args) throws Exception {

        ScheduledThreadPoolExecutor scheduledThreadPool =
                new ScheduledThreadPoolExecutor(1);
        //两个任务休息间隔CheckBeatPeriod
        Long checkBeatPeriod = licenseConfig.getCheckBeatPeriod();
        scheduledThreadPool.scheduleWithFixedDelay(() -> {

            //尝试获取锁
            Boolean flag = redisTemplate.opsForValue().setIfAbsent(SysConstant.CHECK_BEAT_TIMEOUT_LOCK_KEY, "", checkBeatPeriod,TimeUnit.MILLISECONDS);

            if (Boolean.FALSE.equals(flag)) {
                // 加锁失败
                return;
            }

            Set<String> scan = scan(SysConstant.REDIS_INSTANCE_PREFIX);
            scan.forEach(key -> {
                //instance#tenantName#nameSpace#1554661116183597056
                String[] split = key.split("#");
                //已超时,需要remove这个key
                InstanceService instanceService = instanceManager.getIfAbsentInStanceService(split[1], split[2]);
                Instance instance = instanceService.getInstance(Long.parseLong(split[3]));
                if (instance != null && instance.getLastBeatMillis() + licenseConfig.getBeatTimeOut() <= System.currentTimeMillis()) {
                    log.info("发现超时instance {}", key);
                    instanceService.removeInstance(instance);
                }
            });
            //释放锁
//            redisTemplate.delete(SysConstant.CHECK_BEAT_TIMEOUT_LOCK_KEY);
        }, checkBeatPeriod, checkBeatPeriod, TimeUnit.MILLISECONDS);
    }


    /**
     * scan 实现
     * @param pattern 表达式，找出所有以 pattern 开始的键
     */
    private Set<String> scan(String pattern) {

        Set<String> keys = redisTemplate.execute((RedisCallback<Set<String>>) connection -> {
            Set<String> keysTmp = new HashSet<>();
            Cursor<byte[]> cursor = connection.scan(ScanOptions.scanOptions().match(pattern + "*").count(100000).build());
            while (cursor.hasNext()) {
                keysTmp.add(new String(cursor.next()));
            }
            return keysTmp;
        });

        return keys;
    }

}