package com.yozosoft.license.model;

import lombok.Data;

import java.util.Map;

@Data
public class Instance extends InstanceHealth{

    private Long instanceId;

    private String ip;

    private Integer port;

    private Map<String, String> metadata;
}
