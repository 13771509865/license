package com.yozosoft.license.client.impl;

import com.alibaba.fastjson2.JSONObject;
import com.yozosoft.license.client.AuthorizationStub;
import com.yozosoft.license.constant.ResultCodeEnum;
import com.yozosoft.license.constant.SysLicenseConstant;
import com.yozosoft.license.constant.TenantEnum;
import com.yozosoft.license.exception.ClientException;
import com.yozosoft.license.util.AuthorizationUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.io.InputStream;
import java.util.Date;
import java.util.Optional;

/**
 * eni授权文件解密
 *
 * @author zhouf
 */
public class EniAuthorizationStub implements AuthorizationStub {

    private static final String ENI_FILENAME = "license.eni";

    public static void main(String[] args) {
        EniAuthorizationStub eniAuthorizationStub = new EniAuthorizationStub();
        String pubKey = "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQCfW+PLSJNE03k5D33CPgUngSpHcXGbIeoRNyzyJmQF2Ocpo/0cw7uXMXDPp46YaDNzRgxonObt5ddQMsNv2DhAZj1o2tfOubUbvTCYEnAKD9Rt0IpZxDMXt4R3Jxw7JAROALWpUrGCfrJiuq8h13ZgIBqohzmccl0j545IBk8wiQIDAQAB";
        eniAuthorizationStub.resolveAuthorization(pubKey, TenantEnum.E_DOC_MIDDLE, "D:/fcs/license.eni");
        System.out.println("end");
    }

    @Override
    public JSONObject resolveAuthorization(String pubKey, TenantEnum tenantEnum, String authorizationPath) throws ClientException {
        InputStream resourceAsStream = null;
        JSONObject jsonObject;
        JSONObject jsonResult = null;
        File authFile = null;
        if (StringUtils.isBlank(authorizationPath)) {
            //走默认路径
            String path = EniAuthorizationStub.class.getClassLoader().getResource(ENI_FILENAME).getPath();
            resourceAsStream = EniAuthorizationStub.class.getClassLoader().getResourceAsStream(ENI_FILENAME);
            if (resourceAsStream == null) {
                throw new ClientException(ResultCodeEnum.E_CLIENT_AUTHORIZATION_NOT_EXIST.getValue(), ResultCodeEnum.E_CLIENT_AUTHORIZATION_NOT_EXIST.getInfo());
            }
        } else {
            authFile = new File(authorizationPath);
            if (!authFile.isFile()) {
                throw new ClientException(ResultCodeEnum.E_CLIENT_AUTHORIZATION_NOT_EXIST.getValue(), ResultCodeEnum.E_CLIENT_AUTHORIZATION_NOT_EXIST.getInfo());
            }
        }
        try {
            if (resourceAsStream != null) {
                jsonObject = AuthorizationUtils.parseAuthorization(resourceAsStream, pubKey);
            } else {
                jsonObject = AuthorizationUtils.parseAuthorization(authFile, pubKey);
            }
            //检查非空
            Optional.of(jsonObject);
        } catch (ClientException ce) {
            throw ce;
        } catch (Exception e) {
            e.printStackTrace();
            throw new ClientException(ResultCodeEnum.E_CLIENT_RESOLVE_AUTHORIZATION_FAIL.getValue(), ResultCodeEnum.E_CLIENT_RESOLVE_AUTHORIZATION_FAIL.getInfo());
        }
        switch (tenantEnum) {
            case E_DCS:
                jsonResult = buildDcsResult(jsonObject);
                break;
            case E_DOC_MIDDLE:
                jsonResult = buildDocResult(jsonObject);
                break;
            default:
        }
        return jsonResult;
    }

    private JSONObject buildDcsResult(JSONObject jsonObject) {
        JSONObject dcsJson = jsonObject.getJSONObject(SysLicenseConstant.DCS_LICENSE);
        Date startTime = dcsJson.getDate(SysLicenseConstant.START_TIME);
        Date expireTime = dcsJson.getDate(SysLicenseConstant.EXPIRE_TIME);
        Date now = new Date();
        if (now.after(startTime) && now.before(expireTime)) {
            //服务器当前时间在授权合法时间内
            jsonObject.remove(SysLicenseConstant.DOC_LICENSE);
            return jsonObject;
        } else {
            throw new ClientException(ResultCodeEnum.E_CLIENT_AUTHORIZATION_EXPIRED.getValue(), ResultCodeEnum.E_CLIENT_AUTHORIZATION_EXPIRED.getInfo());
        }
    }

    private JSONObject buildDocResult(JSONObject jsonObject) {
        JSONObject dcsJson = jsonObject.getJSONObject(SysLicenseConstant.DOC_LICENSE);
        Date startTime = dcsJson.getDate(SysLicenseConstant.START_TIME);
        Date expireTime = dcsJson.getDate(SysLicenseConstant.EXPIRE_TIME);
        if (startTime == null || expireTime == null) {
            throw new ClientException(ResultCodeEnum.E_CLIENT_FORMAT_ERROR.getValue(), ResultCodeEnum.E_CLIENT_FORMAT_ERROR.getInfo());
        }
        Date now = new Date();
        if (now.after(startTime) && now.before(expireTime)) {
            //服务器当前时间在授权合法时间内
            jsonObject.remove(SysLicenseConstant.DCS_LICENSE);
            return jsonObject;
        } else {
            throw new ClientException(ResultCodeEnum.E_CLIENT_AUTHORIZATION_EXPIRED.getValue(), ResultCodeEnum.E_CLIENT_AUTHORIZATION_EXPIRED.getInfo());
        }
    }
}
