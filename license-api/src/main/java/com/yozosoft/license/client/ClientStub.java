package com.yozosoft.license.client;

import com.yozosoft.license.exception.ClientException;
import com.yozosoft.license.model.*;

/**
 * 客户端调用凭证
 *
 * @author zhouf
 */
public interface ClientStub {

    /**
     * 注册
     */
    Object clientRegister(String serverHost, RegisterDTO registerDTO, String secret, Long uuid) throws ClientException;

    /**
     * 心跳
     */
    Object clientBeat(String serverHost, HeartBeatDTO heartBeatDTO, String secret, Long uuid) throws ClientException;

    /**
     * 握手
     */
    HandshakeResultDTO clientHandshake(String serverHost) throws ClientException;

    /**
     * 生成秘钥
     */
    void clientExchangeSecret(String serverHost, SecretDTO secretDTO) throws ClientException;

    /**
     * 注销
     */
    Object clientCancel(String serverHost, CancelDTO cancelDTO, String secret, Long uuid) throws ClientException;
}
