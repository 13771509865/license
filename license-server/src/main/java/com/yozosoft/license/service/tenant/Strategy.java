package com.yozosoft.license.service.tenant;

import com.yozosoft.license.model.Instance;

/**
 * 子类命名参照 {@link com.yozosoft.license.constant.TenantEnum}的tenantName
 */
public interface Strategy {

    Boolean checkAddInstance(Instance instance);

    Boolean checkReduceInstance(Instance instance);

    /**
     * 获取各应用注册需返回响应内容
     */
    Object getClientResponse();

    /**
     * 检查授权时间是否过期
     */
    Boolean checkLicenseTime();
}
