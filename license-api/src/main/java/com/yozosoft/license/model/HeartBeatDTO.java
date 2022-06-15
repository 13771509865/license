package com.yozosoft.license.model;

import lombok.Data;

@Data
public class HeartBeatDTO {

    private String tenantName;

    private String nameSpace;

    private Long instanceId;

    private String ip;

    private Integer port;
}
