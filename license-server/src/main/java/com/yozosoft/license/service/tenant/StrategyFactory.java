//package com.yozosoft.license.service.tenant;
//
//import com.yozosoft.license.common.util.SpringUtils;
//import com.yozosoft.license.constant.ResultCodeEnum;
//import com.yozosoft.license.constant.TenantEnum;
//import com.yozosoft.license.exception.LicenseException;
//import com.yozosoft.license.model.bo.DcsLicenseBO;
//import com.yozosoft.license.service.system.LicensePropService;
//import com.yozosoft.license.service.tenant.impl.DcsStrategy;
//import org.springframework.data.redis.core.RedisTemplate;
//
//public class StrategyFactory {
//
//    public static Strategy getStrategy(String tenantName) {
//        Strategy strategy = null;
//        LicensePropService licensePropService = SpringUtils.getBean(LicensePropService.class);
//        TenantEnum tenant = TenantEnum.getTenantByName(tenantName);
//        if (tenant != null) {
//            switch (tenant) {
//                case E_DCS:
//                    DcsStrategy dcsStrategy = DcsStrategy.getSingleton();
//                    DcsLicenseBO dcsLicenseBO = licensePropService.getSysLicenseBO().getDcsLicense();
//                    dcsStrategy.setLicenseBO(dcsLicenseBO);
//                    dcsStrategy.setRedisTemplate((RedisTemplate)SpringUtils.getBean("redisTemplate"));
//                    strategy = dcsStrategy;
//                    break;
//                default:
//                    break;
//            }
//        }
//        if (strategy == null) {
//            throw new LicenseException(ResultCodeEnum.E_STRATEGY_NULL);
//        }
//        return strategy;
//    }
//}
