package com.yozosoft.license.common.util;

import cn.hutool.core.lang.Snowflake;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.net.InetAddress;

@Component
@Slf4j
public class SnowflakeUtils {

    private Long workerId;

    private Snowflake snowflake;

    @PostConstruct
    public void init() {
        Long workerIdTemp = getWorkerId();
        workerId = workerIdTemp % 32;
        snowflake = new Snowflake(workerId);
    }

    public long snowflakeId() {
        return snowflake.nextId();
    }

    private Long getWorkerId() {
        long workerId = 0L;
        InetAddress address;
        try {
            address = InetAddress.getLocalHost();
            byte[] ipAddressByteArray = address.getAddress();
            //如果是IPV4，计算方式是遍历byte[]，然后把每个IP段数值相加得到的结果就是workerId
            if (ipAddressByteArray.length == 4) {
                for (byte byteNum : ipAddressByteArray) {
                    workerId += byteNum & 0xFF;
                }
                //如果是IPV6，计算方式是遍历byte[]，然后把每个IP段后6位（& 0B111111 就是得到后6位）数值相加得到的结果就是workerId
            } else if (ipAddressByteArray.length == 16) {
                for (byte byteNum : ipAddressByteArray) {
                    workerId += byteNum & 0B111111;
                }
            } else {
                throw new IllegalStateException("初始化获取workId失败,错误的ip地址!");
            }
        } catch (Exception e) {
            log.error("初始化获取workId失败", e);
            throw new IllegalStateException("初始化获取workId失败!");
        }
        return workerId;
    }
}
