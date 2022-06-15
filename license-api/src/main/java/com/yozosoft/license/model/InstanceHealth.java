package com.yozosoft.license.model;

import lombok.Data;

@Data
public class InstanceHealth {

    private Long lastBeatMillis;

    private Long registerMillis;

    private Boolean healthy;

    private Long beatTimeOut;

    private Long beatPeriod;
}
