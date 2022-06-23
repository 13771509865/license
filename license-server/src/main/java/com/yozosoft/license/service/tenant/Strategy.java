package com.yozosoft.license.service.tenant;

import com.yozosoft.license.model.Instance;
import com.yozosoft.license.model.bo.BaseLicenseBO;

import java.util.Map;

public interface Strategy {

    Boolean checkAddInstance(Map<Long, Instance> instances, Instance instance);

    Boolean checkReduceInstance(Map<Long, Instance> instances, Instance instance);

    void setLicenseBO(BaseLicenseBO baseLicenseBO);

    /**
     * 获取各应用注册需返回响应内容
     */
    Object getClientResponse();
}