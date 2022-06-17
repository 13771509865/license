package com.yozosoft.license.util;

/**
 * X-Auth签名头信息工具类
 *
 * @author zhouf
 */
public class XAuthUtils {

    public static String buildYozoAuth(String date, String secret, String contentMd5) {
        String str = contentMd5 + date;
        String hmacSha1 = HashUtils.hmacSha1(str, secret);
        return hmacSha1;
    }
}
