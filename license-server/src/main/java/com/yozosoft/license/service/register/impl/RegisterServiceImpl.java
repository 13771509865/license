package com.yozosoft.license.service.register.impl;

import com.yozosoft.license.constant.ResultCodeEnum;
import com.yozosoft.license.exception.LicenseException;
import com.yozosoft.license.model.Instance;
import com.yozosoft.license.model.RegisterDTO;
import com.yozosoft.license.service.instance.InstanceManager;
import com.yozosoft.license.service.instance.InstanceService;
import com.yozosoft.license.service.register.RegisterService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service("registerServiceImpl")
public class RegisterServiceImpl implements RegisterService {

    @Autowired
    InstanceManager instanceManager;

    @Override
    public String register(RegisterDTO registerDTO) {
        String tenantName = registerDTO.getTenantName();
        String nameSpace = registerDTO.getNameSpace();
        Instance instance = registerDTO.getInstance();
        checkInstanceAndHealth(instance);
        InstanceService instanceService = instanceManager.getIfAbsentInStanceService(tenantName, nameSpace);
        Instance innerInstance = instanceService.getInstance(instance.getInstanceId());
        checkExistInstance(innerInstance, instance.getIp(), instance.getPort());
        instanceService.addInstance(instance);
        return instance.getInstanceId().toString();
    }

    private void checkExistInstance(Instance instance, String ip, Integer port) {
        if (instance != null) {
            String instanceIp = instance.getIp();
            Integer instancePort = instance.getPort();
            if (!instanceIp.equals(ip) || !instancePort.equals(port)) {
                throw new LicenseException(ResultCodeEnum.E_REGISTER_INSTANCE_MATCH_FAIL);
            }
        }
    }

    /**
     * 检查参数同时修改instance的健康状态,主要为了修改lastBeat时间
     */
    private void checkInstanceAndHealth(Instance instance) {
        long nowMillis = System.currentTimeMillis();
        instance.setRegisterMillis(nowMillis);
        instance.setHealthy(true);
        instance.setLastBeatMillis(nowMillis);
        //目前写死了超时时间为心跳间隔的10倍
        instance.setBeatTimeOut(instance.getBeatPeriod() * 10);
    }
}
