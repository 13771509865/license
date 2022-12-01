package com.yozosoft.license.service.system;

import com.alibaba.fastjson2.JSONObject;
import com.yozosoft.license.config.LicenseConfig;
import com.yozosoft.license.constant.SysLicenseConstant;
import com.yozosoft.license.model.bo.DcsLicenseBO;
import com.yozosoft.license.model.bo.DocLicenseBO;
import com.yozosoft.license.model.bo.SysLicenseBO;
import com.yozosoft.license.util.AuthorizationUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.File;

/**
 * eni文件解析及验证
 *
 * @author zhouf
 */
@Service("eniService")
public class EniService {

    @Autowired
    LicenseConfig licenseConfig;

    private String publicRSAKey;

    private String privateRSAKey;

    @PostConstruct
    public void init() {
        publicRSAKey = licenseConfig.getEniPubKey();
//        privateRSAKey = "MIICdgIBADANBgkqhkiG9w0BAQEFAASCAmAwggJcAgEAAoGBAJ9b48tIk0TTeTkPfcI+BSeBKkdxcZsh6hE3LPImZAXY5ymj/RzDu5cxcM+njphoM3NGDGic5u3l11Ayw2/YOEBmPWja1865tRu9MJgScAoP1G3QilnEMxe3hHcnHDskBE4AtalSsYJ+smK6ryHXdmAgGqiHOZxyXSPnjkgGTzCJAgMBAAECgYAXbGlKOwuyhSb/VSCWCYm2aczuHWWmeNCv4R1RJoVzOpOX0kvlC3wqWBEN5MIX8tEFM5mlUtK6yxrf5eZGLVDvIt/YFwP8a9TDaxwR89WVM4DXg5cT8NTeP9ncLk9EqFs3jvSWzrT4gI+LUzkikpRBAGn8ypiOCscg8uVc+VcSxQJBANWPUuYk4qa6Ei6hmGpsUnRfgc9jNQrkKsK+y/B9c2HUJdWRHesorgfGYZIYKQLAU/yYysQMwIQFmJ4ojTMR35sCQQC/ByP9ySClHQVBSon7vTZDTiXmj9YJaVAQHX/WgxAo4OKQLZzvr4tr5FPJkh04WLnez3Xxl9WY5dXo10ogi7yrAkEArkpQT7+eso99M01yxLgu+wbPPGAs8/yO4W0xp83akua/EfNjRX5nubSwALlzDunEIYzZPvNhUt32Vm2l/x4BLQJAUF67uMnHD1DPZjHrLdvkmZqmfYOkpJ8HTVBr+Z94zAoZqFlYfstXmFQfIF52Jr/Fq8WTNMsR1dtVDTqO+HRyMwJAWOAHOVEUdo2U/smt2vJpURMcMeqIg7bu6TwDn1P8JHEkQFJNcE3df59TUMFYgVSm/CrNxqAVIDXVW4H9I6Byew==";
    }

    public SysLicenseBO parseEni(File licenseFile) throws Exception {
        JSONObject jsonObject = AuthorizationUtils.parseAuthorization(licenseFile, publicRSAKey);
        if (jsonObject != null) {
            SysLicenseBO sysLicenseBO = buildSysLicenseBO(jsonObject);
            return sysLicenseBO;
        }
        return null;
    }

    private SysLicenseBO buildSysLicenseBO(JSONObject jsonObject) {
        SysLicenseBO sysLicenseBO = new SysLicenseBO();
        sysLicenseBO.setBusinessName(jsonObject.getString(SysLicenseConstant.BUSINESS_NAME));
        sysLicenseBO.setFeatureCode(jsonObject.getString(SysLicenseConstant.FEATURE_CODE));
        sysLicenseBO.setMaker(jsonObject.getString(SysLicenseConstant.MAKER));
        sysLicenseBO.setCreateTime(jsonObject.getDate(SysLicenseConstant.CREATE_TIME));
        DcsLicenseBO dcsLicenseBO = jsonObject.getObject(SysLicenseConstant.DCS_LICENSE, DcsLicenseBO.class);
        sysLicenseBO.setDcsLicense(dcsLicenseBO);
        DocLicenseBO docLicenseBO = jsonObject.getObject(SysLicenseConstant.DOC_LICENSE, DocLicenseBO.class);
        sysLicenseBO.setDocLicense(docLicenseBO);
        return sysLicenseBO;
    }
}
