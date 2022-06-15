package com.yozosoft.license.model.bo;

import lombok.Data;

import java.util.Date;

@Data
public class BaseLicenseBO {

    private Date startDate;

    private Date expireDate;
}
