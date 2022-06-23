package com.yozosoft.license.model.bo;

import lombok.Data;

import java.util.List;

@Data
public class DcsLicenseBO extends BaseLicenseBO{

    private Long concurrencyTotal;

    private List<Integer> allowTypes;
}
