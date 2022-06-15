package com.yozosoft.license.service.heartbeat;

import com.yozosoft.license.model.Instance;
import com.yozosoft.license.service.instance.InstanceService;

import java.util.Map;

/**
 * 处理心跳接口时的异步更新任务
 * @author zhouf
 */
public class ClientBeatProcessorTask implements Runnable{

    private InstanceService instanceService;

    private Long instanceId;

    @Override
    public void run() {
        Map<Long, Instance> instances = instanceService.getInstances();
        for(Instance instance: instances.values()){
            if(instance.getInstanceId().equals(instanceId)){
                instance.setLastBeatMillis(System.currentTimeMillis());
            }
        }
    }

    public void setInstanceService(InstanceService instanceService){
        this.instanceService = instanceService;
    }

    public void setInstanceId(Long instanceId){
        this.instanceId = instanceId;
    }
}
