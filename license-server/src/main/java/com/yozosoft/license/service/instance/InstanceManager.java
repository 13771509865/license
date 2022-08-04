package com.yozosoft.license.service.instance;

import com.yozosoft.license.common.constant.SysConstant;
import com.yozosoft.license.common.util.SpringUtils;
import com.yozosoft.license.constant.ResultCodeEnum;
import com.yozosoft.license.exception.LicenseException;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service("instanceManager")
public class InstanceManager {

    private Map<String, InstanceService> instanceServiceMap = new ConcurrentHashMap<>();

    public InstanceService getIfAbsentInStanceService(String tenantName, String nameSpace) {
        String instanceKey = getInstanceKey(tenantName, nameSpace);
        InstanceService instanceService = instanceServiceMap.computeIfAbsent(instanceKey, key -> SpringUtils.getBean(InstanceService.class, tenantName, nameSpace));
        return instanceService;
    }

    public InstanceService getInstanceService(String tenantName, String nameSpace){
        String instanceKey = getInstanceKey(tenantName, nameSpace);
        InstanceService instanceService = instanceServiceMap.get(instanceKey);
        if(instanceService == null){
            throw new LicenseException(ResultCodeEnum.E_INSTANCE_SERVICE_NOT_EXIST);
        }
        return instanceService;
    }

    public InstanceService removeInstanceService(String tenantName, String nameSpace){
        String instanceKey = getInstanceKey(tenantName, nameSpace);
        InstanceService instanceService = instanceServiceMap.remove(instanceKey);
        if(instanceService == null){
            throw new LicenseException(ResultCodeEnum.E_INSTANCE_SERVICE_NOT_EXIST);
        }
        return instanceService;
    }

    private String getInstanceKey(String tenantName, String nameSpace) {
        return tenantName + SysConstant.SEPARATOR + nameSpace;
    }
}
