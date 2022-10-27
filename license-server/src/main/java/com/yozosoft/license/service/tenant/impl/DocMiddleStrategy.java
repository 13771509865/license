package com.yozosoft.license.service.tenant.impl;

import com.yozosoft.license.constant.ResultCodeEnum;
import com.yozosoft.license.exception.LicenseException;
import com.yozosoft.license.model.Instance;
import com.yozosoft.license.model.bo.DocLicenseBO;
import com.yozosoft.license.model.bo.SysLicenseBO;
import com.yozosoft.license.service.system.LicensePropService;
import com.yozosoft.license.service.tenant.Strategy;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Date;

/**
 * @author zhouf
 */
@Component("docMiddleStrategy")
public class DocMiddleStrategy implements Strategy {

    @Autowired
    private LicensePropService licensePropService;

    @Override
    public Boolean checkAddInstance(Instance instance) {
        //检查授权时间是否合法
        Boolean checkTime = checkLicenseTime();
        if (!checkTime) {
            throw new LicenseException(ResultCodeEnum.E_DOC_LICENSE_EXPIRED);
        }
        return true;
    }

    @Override
    public Boolean checkReduceInstance(Instance instance) {
        return true;
    }

    @Override
    public Object getClientResponse() {
        DocLicenseBO docLicense = licensePropService.getSysLicenseBO().getDocLicense();
        DocLicenseBO docLicenseResponse = new DocLicenseBO();
        BeanUtils.copyProperties(docLicense, docLicenseResponse);
        //去除授权时间相关信息
        docLicenseResponse.setStartTime(null);
        docLicenseResponse.setExpireTime(null);
        docLicenseResponse.setLicenseType(null);
        return docLicenseResponse;
    }

    @Override
    public Boolean checkLicenseTime() {
        SysLicenseBO sysLicenseBO = licensePropService.getSysLicenseBO();
        DocLicenseBO docLicense = sysLicenseBO.getDocLicense();
        Date startTime = docLicense.getStartTime();
        Date expireTime = docLicense.getExpireTime();
        Date now = new Date();
        if (now.after(startTime) && now.before(expireTime)) {
            //服务器当前时间在授权合法时间内
            return true;
        }
        return false;
    }
}
