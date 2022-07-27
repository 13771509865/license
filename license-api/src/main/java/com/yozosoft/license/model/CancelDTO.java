package com.yozosoft.license.model;

import lombok.Data;

/**
 * 注销实体
 */
@Data
public class CancelDTO {

    private String tenantName;

    private String nameSpace;

    private Long instanceId;

    private String ip;

    private Integer port;
}
