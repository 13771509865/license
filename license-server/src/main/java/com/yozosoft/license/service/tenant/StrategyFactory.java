package com.yozosoft.license.service.tenant;

import com.yozosoft.license.constant.ResultCodeEnum;
import com.yozosoft.license.constant.TenantEnum;
import com.yozosoft.license.exception.LicenseException;
import com.yozosoft.license.service.tenant.impl.DcsStrategy;

public class StrategyFactory {

    public static Strategy getStrategy(String tenantName) {
        Strategy strategy = null;
        TenantEnum tenant = TenantEnum.getTenantByName(tenantName);
        if (tenant != null) {
            switch (tenant) {
                case E_DCS:
                    strategy = DcsStrategy.getSingleton();
                    break;
                default:
                    break;
            }
        }
        if (strategy == null) {
            throw new LicenseException(ResultCodeEnum.E_STRATEGY_NULL);
        }
        return strategy;
    }
}
