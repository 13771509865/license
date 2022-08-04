package com.yozosoft.license.service.instance;

import com.alibaba.fastjson2.JSON;
import com.yozosoft.license.common.util.SpringUtils;
import com.yozosoft.license.model.Instance;
import com.yozosoft.license.service.security.SecretService;
import com.yozosoft.license.service.tenant.Strategy;
import com.yozosoft.license.service.tenant.StrategyFactory;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class InstanceService {
    private String tenantName;

    private String nameSpace;

    private Strategy strategy;

    private InstanceHandler instanceHandler;

    private SecretService secretService;


    public InstanceService(String tenantName, String nameSpace) {

        this.tenantName = tenantName;
        this.nameSpace = nameSpace;
        init();
    }

    public void init() {
        /**
         * 根据类型判断策略
         */
        strategy = StrategyFactory.getStrategy(tenantName);
        instanceHandler = SpringUtils.getBean(InstanceHandler.class);
        secretService=SpringUtils.getBean(SecretService.class);
    }

    //TODO 销毁未实现
    public void destroy() {

    }

    public void addInstance(Instance instance) {
        strategy.checkAddInstance(instance);
        instanceHandler.saveInstance(tenantName, nameSpace, instance);
        log.error("新增instance成功 tenantName：{} nameSpace：{} instance：{}", tenantName, nameSpace, JSON.toJSONString(instance));
    }

    public void removeInstance(Instance instance) {
        strategy.checkReduceInstance(instance);
        instanceHandler.removeInstance(tenantName, nameSpace, instance.getInstanceId());
        secretService.removeSecretByUuid(instance.getInstanceId());
        log.error("删除instance成功 tenantName：{} nameSpace：{} instanceId：{}",tenantName, nameSpace, instance.getInstanceId());
    }

    public void clientBeat(Instance instance) {
        instance.setLastBeatMillis(System.currentTimeMillis());
        instanceHandler.saveInstance(tenantName, nameSpace, instance);
        log.info("心跳instance成功 tenantName：{} nameSpace：{} instance：{}", tenantName, nameSpace, JSON.toJSONString(instance));
    }

    public Instance getInstance(Long instanceId) {
        return instanceHandler.getInstance(tenantName, nameSpace, instanceId);
    }

    public String getLicenseInfo() {
        return JSON.toJSONString(strategy.getClientResponse());
    }
}
