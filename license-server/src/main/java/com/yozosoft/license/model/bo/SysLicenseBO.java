package com.yozosoft.license.model.bo;

import lombok.Data;

import java.util.Date;

@Data
public class SysLicenseBO {

    private String businessName;

    private String featureCode;

    private String maker;

    private Date createTime;

    private DcsLicenseBO dcsLicense;
}
