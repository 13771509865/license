package com.yozosoft.license.client.impl;

import cn.hutool.crypto.SecureUtil;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.parser.Feature;
import com.yozosoft.license.client.ClientStub;
import com.yozosoft.license.constant.ResultCodeEnum;
import com.yozosoft.license.exception.ClientException;
import com.yozosoft.license.model.*;
import com.yozosoft.license.util.XAuthUtils;

import java.text.SimpleDateFormat;
import java.util.*;

/**
 * http实现客户端调用stub
 *
 * @author zhouf
 */
public class HttpClientStub implements ClientStub {

    private static final String REGISTER_URI = "/api/v1/instance/register";

    private static final String CANCEL_URI = "/api/v1/instance/cancel";

    private static final String BEAT_URI = "/api/v1/instance/beat";

    private static final String HANDSHAKE_URI = "/api/v1/security/handshake";

    private static final String EXCHANGE_SECRET_URI = "/api/v1/security/secret";

    @Override
    public Object clientRegister(String serverHost, RegisterDTO registerDTO, String secret, Long uuid) throws ClientException {
        HttpResponse response = HttpRequest.post(serverHost + REGISTER_URI).body(JSON.toJSONString(registerDTO))
                .header(buildHeaders(registerDTO, uuid, secret)).timeout(100 * 1000).execute();
        String body = response.body();
        ErrorResultDTO errorResultDTO = null;
        try {
            if (response.isOk()) {
                Object result = JSON.parse(body);
                return result;
            }
            errorResultDTO = JSON.parseObject(body, ErrorResultDTO.class);
        } catch (Exception e) {
            throw new ClientException(ResultCodeEnum.E_CLIENT_HTTP_FAIL.getValue(), ResultCodeEnum.E_CLIENT_HTTP_FAIL.getInfo());
        }
        throw new ClientException(errorResultDTO.getErrorCode(), errorResultDTO.getErrorMessage());
    }

    @Override
    public Object clientBeat(String serverHost, HeartBeatDTO heartBeatDTO, String secret, Long uuid) throws ClientException {
        HttpResponse response = HttpRequest.put(serverHost + BEAT_URI).body(JSON.toJSONString(heartBeatDTO))
                .header(buildHeaders(heartBeatDTO, uuid, secret)).timeout(100 * 1000).execute();
        String body = response.body();
        ErrorResultDTO errorResultDTO = null;
        try {
            if (response.isOk()) {
                Object result = JSON.parse(body);
                return result;
            }
            errorResultDTO = JSON.parseObject(body, ErrorResultDTO.class);
        } catch (Exception e) {
            throw new ClientException(ResultCodeEnum.E_CLIENT_HTTP_FAIL.getValue(), ResultCodeEnum.E_CLIENT_HTTP_FAIL.getInfo());
        }
        throw new ClientException(errorResultDTO.getErrorCode(), errorResultDTO.getErrorMessage());
    }

    @Override
    public HandshakeResultDTO clientHandshake(String serverHost) throws ClientException {
        HttpResponse response = HttpRequest.get(serverHost + HANDSHAKE_URI).timeout(100 * 1000).execute();
        String body = response.body();
        ErrorResultDTO errorResultDTO = null;
        try {
            if (response.isOk()) {
                HandshakeResultDTO handshakeResultDTO = JSON.parseObject(body, HandshakeResultDTO.class);
                return handshakeResultDTO;
            }
            errorResultDTO = JSON.parseObject(body, ErrorResultDTO.class);
        } catch (Exception e) {
            throw new ClientException(ResultCodeEnum.E_CLIENT_HTTP_FAIL.getValue(), ResultCodeEnum.E_CLIENT_HTTP_FAIL.getInfo());
        }
        throw new ClientException(errorResultDTO.getErrorCode(), errorResultDTO.getErrorMessage());
    }

    @Override
    public void clientExchangeSecret(String serverHost, SecretDTO secretDTO) throws ClientException {
        HttpResponse response = HttpRequest.post(serverHost + EXCHANGE_SECRET_URI).timeout(100 * 1000).execute();
        String body = response.body();
        ErrorResultDTO errorResultDTO = null;
        try {
            if (!response.isOk()) {
                errorResultDTO = JSON.parseObject(body, ErrorResultDTO.class);
                throw new ClientException(errorResultDTO.getErrorCode(), errorResultDTO.getErrorMessage());
            }
        } catch (Exception e) {
            throw new ClientException(ResultCodeEnum.E_CLIENT_HTTP_FAIL.getValue(), ResultCodeEnum.E_CLIENT_HTTP_FAIL.getInfo());
        }
    }

    @Override
    public Object clientCancel(String serverHost, CancelDTO cancelDTO, String secret, Long uuid) throws ClientException {
        HttpResponse response = HttpRequest.post(serverHost + CANCEL_URI).body(JSON.toJSONString(cancelDTO))
                .header(buildHeaders(cancelDTO, uuid, secret)).timeout(100 * 1000).execute();
        String body = response.body();
        ErrorResultDTO errorResultDTO = null;
        try {
            if (response.isOk()) {
                Object result = JSON.parse(body);
                return result;
            }
            errorResultDTO = JSON.parseObject(body, ErrorResultDTO.class);
        } catch (Exception e) {
            throw new ClientException(ResultCodeEnum.E_CLIENT_HTTP_FAIL.getValue(), ResultCodeEnum.E_CLIENT_HTTP_FAIL.getInfo());
        }
        throw new ClientException(errorResultDTO.getErrorCode(), errorResultDTO.getErrorMessage());
    }




    private static Map<String, List<String>> buildHeaders(Object obj, Long uuid, String secret) {
        Map<String, List<String>> headers = new HashMap<>(10);
        Date date = new Date();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("EEE, d MMM yyyy HH:mm:ss 'GMT'", Locale.US);
        simpleDateFormat.setTimeZone(TimeZone.getTimeZone("GMT"));
        String format = simpleDateFormat.format(date);
        headers.put("Date", Arrays.asList(format));
        String str = JSON.parseObject(JSON.toJSONString(obj), Feature.OrderedField).toJSONString();
        String md5 = SecureUtil.md5(str);
        headers.put("Content-Md5", Arrays.asList(md5));
        String xAuth = XAuthUtils.buildYozoAuth(format, secret, md5);
        headers.put("X-Auth", Arrays.asList(xAuth));
        headers.put("Uuid", Arrays.asList(uuid.toString()));
        return headers;
    }
}
