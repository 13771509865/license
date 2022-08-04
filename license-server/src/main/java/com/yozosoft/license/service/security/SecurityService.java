package com.yozosoft.license.service.security;

import com.yozosoft.license.common.util.SnowflakeUtils;
import com.yozosoft.license.constant.ResultCodeEnum;
import com.yozosoft.license.exception.LicenseException;
import com.yozosoft.license.model.HandshakeResultDTO;
import com.yozosoft.license.model.SecretDTO;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 管理各端临时密码,后期需放入redis中实现集群支持
 *
 * @author zhouf
 */
@Service("securityService")
public class SecurityService {

    @Autowired
    SnowflakeUtils snowflakeUtils;

    @Autowired
    SecretService secretService;

    public String getUuidSecret(String uuid) {

        Long uuidL = Long.valueOf(uuid);
        String secret = secretService.getSecretByUuid(uuidL);
        if (StringUtils.isBlank(secret)) {
            throw new LicenseException(ResultCodeEnum.E_SECRET_NOT_EXIST);
        }
        return secret;
    }

    public HandshakeResultDTO handshake() {
        HandshakeResultDTO handshakeResultDTO = new HandshakeResultDTO();
        handshakeResultDTO.setPublicKey(secretService.getPublicRSAKey());
        handshakeResultDTO.setUuid(generateUuid());
        return handshakeResultDTO;
    }

    public void exchangeSecret(SecretDTO secretDTO) {
        Long uuid = secretDTO.getUuid();
        String channelSecret = secretDTO.getChannelSecret();
        String realSecret;
        try {
            realSecret = secretService.decryptSecret(channelSecret);
        } catch (Exception e) {
            throw new LicenseException(ResultCodeEnum.E_EXCHANGE_SECRET_DECRYPT_ERROR);
        }
        secretService.saveSecret(uuid, realSecret);
    }

    /**
     * 生成终端唯一id
     */
    private Long generateUuid() {
        return snowflakeUtils.snowflakeId();
    }
}
