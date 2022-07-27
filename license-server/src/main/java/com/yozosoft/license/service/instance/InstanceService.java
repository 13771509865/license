package com.yozosoft.license.service.instance;

import com.alibaba.fastjson2.JSON;
import com.yozosoft.license.common.constant.SysConstant;
import com.yozosoft.license.common.util.GlobalExecutor;
import com.yozosoft.license.constant.ResultCodeEnum;
import com.yozosoft.license.exception.LicenseException;
import com.yozosoft.license.model.Instance;
import com.yozosoft.license.service.heartbeat.ClientBeatCheckTask;
import com.yozosoft.license.service.heartbeat.ClientBeatProcessorTask;
import com.yozosoft.license.service.tenant.Strategy;
import com.yozosoft.license.service.tenant.StrategyFactory;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class InstanceService {

    private ClientBeatCheckTask clientBeatCheckTask = new ClientBeatCheckTask(this);

    //TODO 需要持久化
    private Map<Long, Instance> instances = new ConcurrentHashMap<>();

    private String tenantName;

    private Strategy strategy;

    private ScheduledExecutorService scheduledExecutorService = Executors.newScheduledThreadPool(Runtime.getRuntime().availableProcessors() * 2);

    public InstanceService(String tenantName) {
        this.tenantName = tenantName;
        init();
    }

    public void init() {
        strategy = StrategyFactory.getStrategy(tenantName);
        //开启定时任务检查当前集群健康状态
        scheduledExecutorService.scheduleWithFixedDelay(clientBeatCheckTask, SysConstant.HEALTH_INITIAL_DELAY, SysConstant.HEALTH_DELAY, TimeUnit.MILLISECONDS);
    }

    //TODO 销毁未实现
    public void destroy() {
        scheduledExecutorService.shutdown();
    }

    public void addInstance(Instance instance) {
        /**
         * 根据类型判断策略
         */
        Boolean checkResult = strategy.checkAddInstance(instances, instance);
        if (!checkResult) {
            //TODO 报错信息不对
            throw new LicenseException(ResultCodeEnum.E_DCS_ADD_STRATEGY_FAIL);
        }
        instances.put(instance.getInstanceId(), instance);
    }

    public void removeInstance(Instance instance) {
        Boolean checkResult = strategy.checkReduceInstance(instances, instance);
        if (!checkResult) {
            //TODO 报错信息不对
            throw new LicenseException(ResultCodeEnum.E_DCS_REDUCE_STRATEGY_FAIL);
        }
        instances.remove(instance.getInstanceId());
    }

    public void processClientBeat(Long instanceId) {
        ClientBeatProcessorTask clientBeatProcessorTask = new ClientBeatProcessorTask();
        clientBeatProcessorTask.setInstanceId(instanceId);
        clientBeatProcessorTask.setInstanceService(this);
        //异步处理
        GlobalExecutor.BEAT_PROCESSOR_EXECUTOR.schedule(clientBeatProcessorTask, SysConstant.BEAT_PROCESSOR_DELAY, TimeUnit.MILLISECONDS);
    }

    public Map<Long, Instance> getInstances() {
        return instances;
    }

    public Instance getInstance(Long instanceId) {
        Instance instance = instances.get(instanceId);
        return instance;
    }

    public String getLicenseInfo() {
        return JSON.toJSONString(strategy.getClientResponse());
    }
}
