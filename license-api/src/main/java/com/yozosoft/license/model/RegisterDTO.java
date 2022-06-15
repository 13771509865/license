package com.yozosoft.license.model;

import lombok.Data;

@Data
public class RegisterDTO {

    private String tenantName;

    private String nameSpace;

    private Instance instance;
}
