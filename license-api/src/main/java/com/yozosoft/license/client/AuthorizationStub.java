package com.yozosoft.license.client;

import com.alibaba.fastjson2.JSONObject;
import com.yozosoft.license.constant.TenantEnum;
import com.yozosoft.license.exception.ClientException;

/**
 * 授权文件解密
 * @author zhouf
 */
public interface AuthorizationStub {

    /**
     * 解析授权文件
     */
    JSONObject resolveAuthorization(String pubKey, TenantEnum tenantEnum, String authorizationPath) throws ClientException;
}
