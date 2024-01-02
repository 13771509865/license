package com.yozosoft.license.util;

import com.google.common.hash.Hashing;

import java.nio.charset.StandardCharsets;

/**
 * 摘要算法工具类
 */
public class HashUtils {

    public static String hmacSha1(String str, String secret) {
        String hmacSha1 = Hashing.hmacSha1(secret.getBytes(StandardCharsets.UTF_8)).hashString(str, StandardCharsets.UTF_8).toString();
        if(hmacSha1 ==""){
            System.out.println("hmacSha1 is blank");
        }
        return hmacSha1;
    }
}
