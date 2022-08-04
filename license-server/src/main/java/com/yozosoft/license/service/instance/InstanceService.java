package com.yozosoft.license.service.instance;

import com.alibaba.fastjson2.JSON;
import com.yozosoft.license.common.util.SpringUtils;
import com.yozosoft.license.constant.ResultCodeEnum;
import com.yozosoft.license.constant.TenantEnum;
import com.yozosoft.license.exception.LicenseException;
import com.yozosoft.license.model.Instance;
import com.yozosoft.license.service.security.SecretService;
import com.yozosoft.license.service.tenant.Strategy;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;

@Slf4j
@Service
@Scope("prototype")
public class InstanceService {
    private String tenantName;

    private String nameSpace;

    private Strategy strategy;

    @Autowired
    private InstanceHandler instanceHandler;
    @Autowired
    private SecretService secretService;


    public InstanceService(String tenantName, String nameSpace) {
        this.tenantName = tenantName;
        this.nameSpace = nameSpace;
    }

    @PostConstruct
    public void init() {
        /**
         * 根据类型判断策略
         */
        TenantEnum tenantEnum = TenantEnum.getTenantByName(tenantName);
        if (tenantEnum == null) {
            throw new LicenseException(ResultCodeEnum.E_STRATEGY_NULL);
        }
        strategy = (Strategy) SpringUtils.getBean(tenantEnum.getTenantName() + Strategy.class.getSimpleName());
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
