package com.yozosoft.license.client;

import com.yozosoft.license.exception.ClientException;
import com.yozosoft.license.model.HandshakeResultDTO;
import com.yozosoft.license.model.HeartBeatDTO;
import com.yozosoft.license.model.RegisterDTO;
import com.yozosoft.license.model.SecretDTO;

/**
 * 客户端调用凭证
 *
 * @author zhouf
 */
public interface ClientStub {

    Object clientRegister(String serverHost, RegisterDTO registerDTO, String secret, Long uuid) throws ClientException;

    Object clientBeat(String serverHost, HeartBeatDTO heartBeatDTO, String secret, Long uuid) throws ClientException;

    HandshakeResultDTO clientHandshake(String serverHost) throws ClientException;

    void clientExchangeSecret(String serverHost, SecretDTO secretDTO) throws ClientException;

}
