package com.yozosoft.license.model;

import lombok.Data;

@Data
public class InstanceHealth {

    private Long lastBeatMillis;

    private Long registerMillis;

//    private Boolean healthy;
//
//    private Long beatTimeOut;
//
//    /**
//     * 客户端心跳间隔,单位毫秒,不允许小于30s
//     */
//    private Long beatPeriod;
}
