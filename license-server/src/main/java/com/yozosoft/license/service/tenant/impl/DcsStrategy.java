package com.yozosoft.license.service.tenant.impl;

import com.yozosoft.license.constant.MetaDataConstant;
import com.yozosoft.license.model.Instance;
import com.yozosoft.license.model.bo.BaseLicenseBO;
import com.yozosoft.license.model.bo.DcsLicenseBO;
import com.yozosoft.license.service.tenant.Strategy;

import java.util.Map;
import java.util.concurrent.atomic.LongAdder;

public class DcsStrategy implements Strategy {

    private LongAdder longAdder = new LongAdder();

    private DcsLicenseBO dcsLicenseBO;

    private static DcsStrategy dcsStrategy = new DcsStrategy();

    @Override
    public Boolean checkAddInstance(Map<Long, Instance> instances, Instance instance) {
        Map<String, String> metadata = instance.getMetadata();
        Long concurrencyNum = Long.valueOf(metadata.get(MetaDataConstant.CONCURRENCY_NUM));
        if (longAdder.longValue() + concurrencyNum <=dcsLicenseBO.getConcurrencyTotal()) {
            longAdder.add(concurrencyNum);
            return true;
        }
        return false;
    }

    @Override
    public Boolean checkReduceInstance(Map<Long, Instance> instances, Instance instance) {
        Map<String, String> metadata = instance.getMetadata();
        Long concurrencyNum = Long.valueOf(metadata.get(MetaDataConstant.CONCURRENCY_NUM));
        longAdder.add(-concurrencyNum);
        return true;
    }

    public static DcsStrategy getSingleton() {
        return dcsStrategy;
    }

    @Override
    public void setLicenseBO(BaseLicenseBO baseLicenseBO){
        this.dcsLicenseBO = (DcsLicenseBO)baseLicenseBO;
    }
}
