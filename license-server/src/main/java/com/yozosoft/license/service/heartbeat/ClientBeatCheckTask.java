package com.yozosoft.license.service.heartbeat;

import com.yozosoft.license.model.Instance;
import com.yozosoft.license.service.instance.InstanceService;

import java.util.Map;

/**
 * 实现实例集群的心跳检查
 * @author zhouf
 */
public class ClientBeatCheckTask implements Runnable{

    private InstanceService instanceService;

    public ClientBeatCheckTask(InstanceService instanceService){
        this.instanceService = instanceService;
    }

    @Override
    public void run() {
        Map<Long, Instance> instances = instanceService.getInstances();
        //超过监测间隔5倍时长标记为不健康
        instances.values().stream().filter(i->{
            Boolean bool = System.currentTimeMillis() - i.getLastBeatMillis() > (i.getBeatPeriod()*5);
            return bool;
        }).forEach(i->{
            i.setHealthy(false);
        });
        //超过超时时间的进行节点移除
        instances.values().stream().filter(i->{
            Boolean bool = System.currentTimeMillis() - i.getLastBeatMillis() > i.getBeatTimeOut();
            return bool;
        }).forEach(i->{
            instanceService.removeInstance(i);
        });
    }
}
