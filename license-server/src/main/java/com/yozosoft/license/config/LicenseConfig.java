package com.yozosoft.license.config;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Data
@Component
public class LicenseConfig {

    @Value("${license.channel.publicKey}")
    private String channelPubKey;

    @Value("${license.channel.privateKey}")
    private String channelPriKey;

    @Value("${license.eni.publicKey}")
    private String eniPubKey;
}
