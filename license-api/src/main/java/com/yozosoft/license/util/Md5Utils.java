package com.yozosoft.license.util;

import cn.hutool.crypto.digest.MD5;

public class Md5Utils {

    public static String getMd5(String str){
        String md5 = MD5.create().digestHex(str);
        return md5;
    }
}
