package com.yozosoft.license.service.tenant;

import com.yozosoft.license.model.Instance;
import com.yozosoft.license.model.bo.BaseLicenseBO;

import java.util.Map;

public interface Strategy {

    Boolean checkAddInstance(Map<Long, Instance> instances, Instance instance);

    Boolean checkReduceInstance(Map<Long, Instance> instances, Instance instance);

    void setLicenseBO(BaseLicenseBO baseLicenseBO);
}
