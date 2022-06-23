package com.yozosoft.license.service.security;

import com.yozosoft.license.config.LicenseConfig;
import com.yozosoft.license.util.RSAUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 密码管理service
 *
 * @author zhouf
 */
@Service("secretService")
public class SecretService {

    @Autowired
    LicenseConfig licenseConfig;

    private Map<Long, String> secrets = new ConcurrentHashMap<>();

    private String publicRSAKey;

    private String privateRSAKey;

    @PostConstruct
    public void init() {
        publicRSAKey = licenseConfig.getChannelPubKey();
        privateRSAKey = licenseConfig.getChannelPriKey();
        if(StringUtils.isBlank(publicRSAKey) || StringUtils.isBlank(privateRSAKey)){
            System.out.println("channel密钥初始化失败,请检查配置");
        }
    }

    public String getPublicRSAKey() {
        return publicRSAKey;
    }

    public String decryptSecret(String channelSecret) throws Exception {
        String decrypt = RSAUtils.decryptByPrivate(channelSecret, privateRSAKey);
        return decrypt;
    }

    public void saveSecret(Long uuid, String secret) {
        secrets.put(uuid, secret);
    }

    public String getSecretByUuid(Long uuid) {
        String secret = secrets.get(uuid);
        return secret;
    }
}
