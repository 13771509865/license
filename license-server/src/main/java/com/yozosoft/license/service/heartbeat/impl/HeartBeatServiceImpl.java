package com.yozosoft.license.service.heartbeat.impl;

import com.yozosoft.license.constant.ResultCodeEnum;
import com.yozosoft.license.exception.LicenseException;
import com.yozosoft.license.model.HeartBeatDTO;
import com.yozosoft.license.model.Instance;
import com.yozosoft.license.service.heartbeat.HeartBeatService;
import com.yozosoft.license.service.instance.InstanceManager;
import com.yozosoft.license.service.instance.InstanceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service("heartBeatServiceImpl")
public class HeartBeatServiceImpl implements HeartBeatService {

    @Autowired
    InstanceManager instanceManager;

    @Override
    public Boolean heartBeat(HeartBeatDTO heartBeatDTO) {
        InstanceService instanceService = instanceManager.getInstanceService(heartBeatDTO.getTenantName(), heartBeatDTO.getNameSpace());
        Instance instance = instanceService.getInstance(heartBeatDTO.getInstanceId());
        if (instance == null) {
            throw new LicenseException(ResultCodeEnum.E_HEARTBEAT_INSTANCE_NOT_EXIST);
        }
        String ip = instance.getIp();
        Integer port = instance.getPort();
        String beatIp = heartBeatDTO.getIp();
        Integer beatPort = heartBeatDTO.getPort();
        if (ip.equals(beatIp) && port.equals(beatPort)) {
            //正确节点更新instance最新心跳时间
            instanceService.processClientBeat(instance.getInstanceId());
            return true;
        } else {
            throw new LicenseException(ResultCodeEnum.E_HEARTBEAT_INSTANCE_MATCH_FAIL);
        }
    }
}
