package com.yozosoft.license.service.tenant.impl;

import com.yozosoft.license.common.constant.SysConstant;
import com.yozosoft.license.constant.MetaDataConstant;
import com.yozosoft.license.constant.ResultCodeEnum;
import com.yozosoft.license.exception.LicenseException;
import com.yozosoft.license.model.Instance;
import com.yozosoft.license.model.bo.BaseLicenseBO;
import com.yozosoft.license.model.bo.DcsLicenseBO;
import com.yozosoft.license.service.tenant.Strategy;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.redis.core.RedisTemplate;

import java.util.Map;
import java.util.concurrent.atomic.LongAdder;

public class DcsStrategy implements Strategy {

    private DcsLicenseBO dcsLicenseBO;

    private static DcsStrategy dcsStrategy = new DcsStrategy();

    private RedisTemplate<String, Integer> redisTemplate;

    @Override
    public Boolean checkAddInstance(Instance instance) {
        Map<String, String> metadata = instance.getMetadata();
        if (metadata == null || metadata.get(MetaDataConstant.CONCURRENCY_NUM) == null) {
            throw new LicenseException(ResultCodeEnum.E_INVALID_PARAM);
        }

        Long concurrencyNum = Long.valueOf(metadata.get(MetaDataConstant.CONCURRENCY_NUM));

        //获取当前已有并发数
        Integer onlineConcurrencyNum = redisTemplate.opsForValue().get(SysConstant.ONLINE_CONCURRENCY_NUM);
        if (onlineConcurrencyNum == null) {
            onlineConcurrencyNum = 0;
        }

        if (onlineConcurrencyNum + concurrencyNum <= dcsLicenseBO.getConcurrencyTotal()) {
            Long onlineConcurrencyNumIncrement = redisTemplate.opsForValue().increment(SysConstant.ONLINE_CONCURRENCY_NUM, concurrencyNum);
            if (onlineConcurrencyNumIncrement > dcsLicenseBO.getConcurrencyTotal()) {
                //并发时可能发生incr后的值大于并发限制 ,这里做补偿
                redisTemplate.opsForValue().increment(SysConstant.ONLINE_CONCURRENCY_NUM, -concurrencyNum);
            } else {
                return true;
            }
        }
        throw new LicenseException(ResultCodeEnum.E_DCS_ADD_STRATEGY_FAIL);
    }

    @Override
    public Boolean checkReduceInstance(Instance instance) {
        Map<String, String> metadata = instance.getMetadata();
        Long concurrencyNum = Long.valueOf(metadata.get(MetaDataConstant.CONCURRENCY_NUM));
        redisTemplate.opsForValue().increment(SysConstant.ONLINE_CONCURRENCY_NUM, -concurrencyNum);
        return true;
    }

    public static DcsStrategy getSingleton() {
        return dcsStrategy;
    }

    @Override
    public void setLicenseBO(BaseLicenseBO baseLicenseBO){
        this.dcsLicenseBO = (DcsLicenseBO)baseLicenseBO;
    }

    public void setRedisTemplate(RedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    @Override
    public Object getClientResponse() {
        return dcsLicenseBO.getAllowTypes();
    }
}
