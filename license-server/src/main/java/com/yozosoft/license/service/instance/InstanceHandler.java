package com.yozosoft.license.service.instance;

import com.alibaba.fastjson.JSON;
import com.yozosoft.license.common.constant.SysConstant;
import com.yozosoft.license.model.Instance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

/**
 * @author zxs
 * @date 2022/8/3 14:45
 * @desc
 */
@Service
public class InstanceHandler {
    @Autowired
    private RedisTemplate redisTemplate;

    /**
     * 新增instance
     */
    public void saveInstance(String tenantName, String nameSpace, Instance instance) {
        String instanceKey = generateInstanceKey(tenantName, nameSpace, instance.getInstanceId());
        redisTemplate.opsForValue().set(instanceKey, JSON.toJSONString(instance));
    }

    /**
     * 删除instance
     */
    public void removeInstance(String tenantName, String nameSpace, Long instanceId) {
        String instanceKey = generateInstanceKey(tenantName, nameSpace, instanceId);
        redisTemplate.delete(instanceKey);
    }

    /**
     * 获取instance
     */
    public Instance getInstance(String tenantName, String nameSpace, Long instanceId) {
        String instanceKey = generateInstanceKey(tenantName, nameSpace, instanceId);
        String instanceStr = (String) redisTemplate.opsForValue().get(instanceKey);
        return JSON.parseObject(instanceStr, Instance.class);
    }

    private String generateInstanceKey(String tenantName, String nameSpace,Long instanceId) {
        return SysConstant.REDIS_INSTANCE_PREFIX + tenantName + SysConstant.SEPARATOR + nameSpace + SysConstant.SEPARATOR + instanceId;
    }


}
