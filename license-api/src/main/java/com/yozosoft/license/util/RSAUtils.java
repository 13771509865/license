package com.yozosoft.license.util;

import javax.crypto.Cipher;
import java.io.ByteArrayOutputStream;
import java.nio.charset.StandardCharsets;
import java.security.*;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Arrays;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

public class RSAUtils {
    /**
     * 密钥长度 于原文长度对应 以及越长速度越慢
     */
    private final static int KEY_SIZE = 1024;

    private static final int MAX_ENCRYPT_BLOCK = 117;

    private static final int MAX_DECRYPT_BLOCK = 128;

    private static Map<String, String> secret;

    /**
     * 随机生成密钥对
     */
    public static Map<String,String> genKeyPair() throws NoSuchAlgorithmException {
        // KeyPairGenerator类用于生成公钥和私钥对，基于RSA算法生成对象
        KeyPairGenerator keyPairGen = KeyPairGenerator.getInstance("RSA");
        // 初始化密钥对生成器
        keyPairGen.initialize(KEY_SIZE, new SecureRandom());
        // 生成一个密钥对，保存在keyPair中
        KeyPair keyPair = keyPairGen.generateKeyPair();
        // 得到私钥
        RSAPrivateKey privateKey = (RSAPrivateKey) keyPair.getPrivate();
        // 得到公钥
        RSAPublicKey publicKey = (RSAPublicKey) keyPair.getPublic();
        String publicKeyString = Base64.getEncoder().encodeToString(publicKey.getEncoded());
        // 得到私钥字符串
        String privateKeyString = Base64.getEncoder().encodeToString(privateKey.getEncoded());
        // 将公钥和私钥保存到Map
        secret = new HashMap<>(2);
        secret.put("publicKey", publicKeyString);
        secret.put("privateKey", privateKeyString);
        return secret;
    }

    public static void main(String[] args) throws Exception {
//        genKeyPair();
        String publics = "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQCfW+PLSJNE03k5D33CPgUngSpHcXGbIeoRNyzyJmQF2Ocpo/0cw7uXMXDPp46YaDNzRgxonObt5ddQMsNv2DhAZj1o2tfOubUbvTCYEnAKD9Rt0IpZxDMXt4R3Jxw7JAROALWpUrGCfrJiuq8h13ZgIBqohzmccl0j545IBk8wiQIDAQAB";
//        String privates = "MIICdgIBADANBgkqhkiG9w0BAQEFAASCAmAwggJcAgEAAoGBAJ9b48tIk0TTeTkPfcI+BSeBKkdxcZsh6hE3LPImZAXY5ymj/RzDu5cxcM+njphoM3NGDGic5u3l11Ayw2/YOEBmPWja1865tRu9MJgScAoP1G3QilnEMxe3hHcnHDskBE4AtalSsYJ+smK6ryHXdmAgGqiHOZxyXSPnjkgGTzCJAgMBAAECgYAXbGlKOwuyhSb/VSCWCYm2aczuHWWmeNCv4R1RJoVzOpOX0kvlC3wqWBEN5MIX8tEFM5mlUtK6yxrf5eZGLVDvIt/YFwP8a9TDaxwR89WVM4DXg5cT8NTeP9ncLk9EqFs3jvSWzrT4gI+LUzkikpRBAGn8ypiOCscg8uVc+VcSxQJBANWPUuYk4qa6Ei6hmGpsUnRfgc9jNQrkKsK+y/B9c2HUJdWRHesorgfGYZIYKQLAU/yYysQMwIQFmJ4ojTMR35sCQQC/ByP9ySClHQVBSon7vTZDTiXmj9YJaVAQHX/WgxAo4OKQLZzvr4tr5FPJkh04WLnez3Xxl9WY5dXo10ogi7yrAkEArkpQT7+eso99M01yxLgu+wbPPGAs8/yO4W0xp83akua/EfNjRX5nubSwALlzDunEIYzZPvNhUt32Vm2l/x4BLQJAUF67uMnHD1DPZjHrLdvkmZqmfYOkpJ8HTVBr+Z94zAoZqFlYfstXmFQfIF52Jr/Fq8WTNMsR1dtVDTqO+HRyMwJAWOAHOVEUdo2U/smt2vJpURMcMeqIg7bu6TwDn1P8JHEkQFJNcE3df59TUMFYgVSm/CrNxqAVIDXVW4H9I6Byew==";
//        String s = encryptByPrivate("123", privates);
//        System.out.println(s);
        String sss = "JQgSf4R8cZWdgzaMkAQi1MZSH8WpW47oA12ad7Ybna4i6KdwRT6Bs6o9tXLC3cHMB3ap2IHSHMV1De2hpYuZRXG/TgCHfrg2DremFQR4m7dDiVnqqxscZ0iSwd7wqftsO58D3ERb3YRrKG3epBGBUdqrz/Rn7H3M4aIUQVfeVqQ=";
        String s1 = decryptByPublic(sss, publics);
        System.out.println(s1);
        System.out.println("end");
    }

    /**
     * RSA公钥加密
     */
    public static String encryptByPublic(String str, String publicKey) throws Exception {
        //base64编码的公钥
        byte[] decoded = Base64.getDecoder().decode(publicKey);
        RSAPublicKey pubKey = (RSAPublicKey) KeyFactory.getInstance("RSA").generatePublic(new X509EncodedKeySpec(decoded));
        //RSA加密
        Cipher cipher = Cipher.getInstance("RSA");
        cipher.init(Cipher.ENCRYPT_MODE, pubKey);
        byte[] data = str.getBytes("UTF-8");
        int inputLen = data.length;
        int offSet = 0;
        byte[] cache;
        byte[] result = {};
        // 对数据分段加密
        while (inputLen - offSet > 0) {
            if (inputLen - offSet > MAX_ENCRYPT_BLOCK) {
                cache = cipher.doFinal(data, offSet, MAX_ENCRYPT_BLOCK);
                offSet += MAX_ENCRYPT_BLOCK;
            } else {
                cache = cipher.doFinal(data, offSet, inputLen - offSet);
                offSet = inputLen;
            }
            result = Arrays.copyOf(result, result.length+cache.length);
            System.arraycopy(cache, 0, result, result.length - cache.length, cache.length);
        }
        String outStr = Base64.getEncoder().encodeToString(result);
        return outStr;
    }

    /**
     * RSA私钥解密
     */
    public static String decryptByPrivate(String str, String privateKey) throws Exception {
        //64位解码加密后的字符串
        byte[] inputByte = Base64.getDecoder().decode(str);
        //base64编码的私钥
        byte[] decoded = Base64.getDecoder().decode(privateKey);
        RSAPrivateKey priKey = (RSAPrivateKey) KeyFactory.getInstance("RSA").generatePrivate(new PKCS8EncodedKeySpec(decoded));
        //RSA解密
        Cipher cipher = Cipher.getInstance("RSA");
        cipher.init(Cipher.DECRYPT_MODE, priKey);
        int inputLen = inputByte.length;
        int offSet = 0;
        byte[] cache;
        byte[] result = {};
        // 对数据分段加密
        while (inputLen - offSet > 0) {
            if (inputLen - offSet > MAX_DECRYPT_BLOCK) {
                cache = cipher.doFinal(inputByte, offSet, MAX_DECRYPT_BLOCK);
                offSet += MAX_DECRYPT_BLOCK;
            } else {
                cache = cipher.doFinal(inputByte, offSet, inputLen - offSet);
                offSet = inputLen;
            }
            result = Arrays.copyOf(result, result.length+cache.length);
            System.arraycopy(cache, 0, result, result.length - cache.length, cache.length);
        }
        String outStr = new String(result, StandardCharsets.UTF_8);
        return outStr;
    }

    /**
     * RSA 私钥加密
     */
    public static String encryptByPrivate(String str, String privateKey) throws Exception {
        //base64编码的公钥
        byte[] decoded = Base64.getDecoder().decode(privateKey);
        RSAPrivateKey priKey = (RSAPrivateKey) KeyFactory.getInstance("RSA").generatePrivate(new PKCS8EncodedKeySpec(decoded));
        //RSA加密
        Cipher cipher = Cipher.getInstance("RSA");
        cipher.init(Cipher.ENCRYPT_MODE, priKey);
        byte[] data = str.getBytes("UTF-8");
        int inputLen = data.length;
        int offSet = 0;
        byte[] cache;
        byte[] result = {};
        // 对数据分段加密
        while (inputLen - offSet > 0) {
            if (inputLen - offSet > MAX_ENCRYPT_BLOCK) {
                cache = cipher.doFinal(data, offSet, MAX_ENCRYPT_BLOCK);
                offSet += MAX_ENCRYPT_BLOCK;
            } else {
                cache = cipher.doFinal(data, offSet, inputLen - offSet);
                offSet = inputLen;
            }
            result = Arrays.copyOf(result, result.length+cache.length);
            System.arraycopy(cache, 0, result, result.length - cache.length, cache.length);
        }
        String outStr = Base64.getEncoder().encodeToString(result);
        return outStr;
    }

    /**
     * RSA 公钥解密
     */
    public static String decryptByPublic(String str, String publicKey) throws Exception {
        //64位解码加密后的字符串
        byte[] inputByte = Base64.getDecoder().decode(str);
        //base64编码的私钥
        byte[] decoded = Base64.getDecoder().decode(publicKey);
        RSAPublicKey pubKey = (RSAPublicKey) KeyFactory.getInstance("RSA").generatePublic(new X509EncodedKeySpec(decoded));
        //RSA解密
        Cipher cipher = Cipher.getInstance("RSA");
        cipher.init(Cipher.DECRYPT_MODE, pubKey);
        int inputLen = inputByte.length;
        int offSet = 0;
        byte[] cache;
        byte[] result = {};
        // 对数据分段加密
        while (inputLen - offSet > 0) {
            if (inputLen - offSet > MAX_DECRYPT_BLOCK) {
                cache = cipher.doFinal(inputByte, offSet, MAX_DECRYPT_BLOCK);
                offSet += MAX_DECRYPT_BLOCK;
            } else {
                cache = cipher.doFinal(inputByte, offSet, inputLen - offSet);
                offSet = inputLen;
            }
            result = Arrays.copyOf(result, result.length+cache.length);
            System.arraycopy(cache, 0, result, result.length - cache.length, cache.length);
        }
        String outStr = new String(result, StandardCharsets.UTF_8);
        return outStr;
    }
}
