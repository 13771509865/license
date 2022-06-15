package com.yozosoft.license.service.instance;

import com.yozosoft.license.common.constant.SysConstant;
import com.yozosoft.license.constant.ResultCodeEnum;
import com.yozosoft.license.exception.LicenseException;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service("instanceManager")
public class InstanceManager {

    private Map<String, InstanceService> instanceServices = new ConcurrentHashMap<>();

    public InstanceService getIfAbsentInStanceService(String tenantName, String nameSpace) {
        String instanceKey = tenantName + SysConstant.SEPARATOR + nameSpace;
        InstanceService instanceService = instanceServices.computeIfAbsent(instanceKey, key -> new InstanceService(tenantName));
        return instanceService;
    }

    public InstanceService getInstanceService(String tenantName, String nameSpace){
        String instanceKey = tenantName + SysConstant.SEPARATOR + nameSpace;
        InstanceService instanceService = instanceServices.get(instanceKey);
        if(instanceService == null){
            throw new LicenseException(ResultCodeEnum.E_INSTANCE_SERVICE_NOT_EXIST);
        }
        return instanceService;
    }
}
