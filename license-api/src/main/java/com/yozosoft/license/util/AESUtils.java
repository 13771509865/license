package com.yozosoft.license.util;

import cn.hutool.crypto.Mode;
import cn.hutool.crypto.Padding;
import cn.hutool.crypto.symmetric.AES;
import com.alibaba.fastjson2.JSON;

import java.nio.charset.StandardCharsets;

public class AESUtils {

    public static String encrypt(Object obj, String secret) {
        return getAes(secret).encryptHex(obj.toString(), StandardCharsets.UTF_8);
    }

    public static String decrypt(String str, String secret) {
        return getAes(secret).decryptStr(str, StandardCharsets.UTF_8);
    }

    private static AES getAes(String secret) {
        int length = secret.length();
        if (length < 16) {
            int num = 16 - length;
            StringBuilder stringBuilder = new StringBuilder();
            for (int i = 0; i < num; i++) {
                stringBuilder.append(0);
            }
            secret = secret.concat(stringBuilder.toString());
        }
        return new AES(Mode.ECB, Padding.PKCS5Padding, secret.getBytes());
    }
}
