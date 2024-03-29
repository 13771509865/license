package com.yozosoft.license.service.register.impl;

import com.yozosoft.license.constant.ResultCodeEnum;
import com.yozosoft.license.exception.LicenseException;
import com.yozosoft.license.model.CancelDTO;
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
        if (innerInstance != null) {
            throw new LicenseException(ResultCodeEnum.E_REGISTER_INSTANCE_ALREADY_EXIST);
        }
        instanceService.addInstance(instance);
        String licenseInfo = instanceService.getLicenseInfo();
        return licenseInfo;
    }

    /**
     * 检查参数同时修改instance的健康状态,主要为了修改lastBeat时间
     */
    private void checkInstanceAndHealth(Instance instance) {
        long nowMillis = System.currentTimeMillis();
        instance.setRegisterMillis(nowMillis);
//        instance.setHealthy(true);
        instance.setLastBeatMillis(nowMillis);
        //目前写死了超时时间为心跳间隔的10倍
       instance.setBeatTimeOut(instance.getBeatPeriod() * 10);
    }

    /**
     * 注销
     */
    @Override
    public Boolean cancel(CancelDTO cancelDTO) {
        InstanceService instanceService = instanceManager.getIfAbsentInStanceService(cancelDTO.getTenantName(), cancelDTO.getNameSpace());
        Instance instance = instanceService.getInstance(cancelDTO.getInstanceId());
        if (instance != null) {
            String ip = instance.getIp();
            Integer port = instance.getPort();
            String cancelIp = cancelDTO.getIp();
            Integer cancelPort = cancelDTO.getPort();
            if (ip.equals(cancelIp) && port.equals(cancelPort)) {
                instanceService.removeInstance(instance);
            } else {
                throw new LicenseException(ResultCodeEnum.E_CANCEL_INSTANCE_MATCH_FAIL);
            }
        }
        //instance 为null时 已经请求过cancel 或者已超时被剔除
        return true;
    }

}
