package com.yozosoft.license.util;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.yozosoft.license.client.impl.MachineCodeService;
import com.yozosoft.license.constant.ResultCodeEnum;
import com.yozosoft.license.constant.SysLicenseConstant;
import com.yozosoft.license.exception.ClientException;
import org.apache.commons.lang3.StringUtils;

import java.io.*;
import java.util.HashMap;
import java.util.Map;

public class AuthorizationUtils {

    private final static String SEPARATOR = "######";

    private final static String MACHINE_CODE_SEPARATOR = "&";

    private final static String MACHINE_CODE_VALUE_SEPARATOR = "=";

    public static JSONObject parseAuthorization(InputStream inputStream, String pubKey) throws Exception {
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
        return parse(bufferedReader, pubKey);
    }

    public static JSONObject parseAuthorization(File licenseFile, String pubKey) throws Exception {
        BufferedReader bufferedReader = new BufferedReader(new FileReader(licenseFile));
        return parse(bufferedReader, pubKey);
    }

    private static JSONObject parse(BufferedReader bufferedReader, String pubKey) throws Exception {
        StringBuilder stringBuilder = new StringBuilder();
        String line;
        while ((line = bufferedReader.readLine()) != null) {
            stringBuilder.append(line);
        }
        String resultByRSA = RSAUtils.decryptByPublic(stringBuilder.toString(), pubKey);
        int sIndex = resultByRSA.lastIndexOf(SEPARATOR);
        if (sIndex < 0) {
            return null;
        }
        String authInfo = resultByRSA.substring(0, sIndex);
        String infoMd5 = resultByRSA.substring(sIndex + SEPARATOR.length());
        JSONObject jsonObject = JSON.parseObject(authInfo);
        //检查授权文件md5防篡改
        Boolean md5Result = checkMd5(authInfo, infoMd5);
        if (md5Result) {
            Boolean checkResult = checkParam(jsonObject);
            if (checkResult) {
                return jsonObject;
            }
        }
        return null;
    }

    private static Boolean checkMd5(String authInfo, String infoMd5) {
        String md5 = Md5Utils.getMd5(authInfo);
        return infoMd5.equals(md5);
    }

    private static Boolean checkParam(JSONObject jsonObject) {
        String businessName = jsonObject.getString(SysLicenseConstant.BUSINESS_NAME);
        String featureCode = jsonObject.getString(SysLicenseConstant.FEATURE_CODE);
        if (StringUtils.isNotBlank(businessName) && StringUtils.isNotBlank(featureCode)) {
            MachineCodeService machineCodeService = new MachineCodeService();
            String machineCode = machineCodeService.decryptMachineCode(featureCode);
            Map<String, String> machineCodeMap = parseMachineCode(machineCode);
            Boolean checkResult = checkMachineCode(machineCodeMap, machineCodeService);
            if(!checkResult){
                throw new ClientException(ResultCodeEnum.E_CLIENT_MACHINE_CODE_MISS_MATCH.getValue(), ResultCodeEnum.E_CLIENT_MACHINE_CODE_MISS_MATCH.getInfo());
            }
            return true;
        }
        return false;
    }

    private static Map<String, String> parseMachineCode(String machineCode) {
        Map<String, String> machineMap = new HashMap<>();
        String[] split = machineCode.split(MACHINE_CODE_SEPARATOR);
        for (String info : split) {
            String[] value = info.split(MACHINE_CODE_VALUE_SEPARATOR);
            machineMap.put(value[0], value[1]);
        }
        return machineMap;
    }

    private static Boolean checkMachineCode(Map<String, String> machineCodeMap, MachineCodeService machineCodeService) {
        String cpuid = machineCodeMap.get(SysLicenseConstant.MACHINE_CODE_CPUID);
        String ip = machineCodeMap.get(SysLicenseConstant.MACHINE_CODE_IP);
        String mac = machineCodeMap.get(SysLicenseConstant.MACHINE_CODE_MAC);
        String uuid = machineCodeMap.get(SysLicenseConstant.MACHINE_CODE_UUID);
        Boolean cpuIdFlag = StringUtils.isBlank(cpuid) || contains(cpuid, machineCodeService.getCPUSerial());
        Boolean ipFlag = true;
//        Boolean ipFlag = StringUtils.isBlank(ip) || contains(ip, machineCodeService.getRealIp());
        Boolean macFlag = true;
//        Boolean macFlag = StringUtils.isBlank(mac) || contains(mac, machineCodeService.getMac());
        Boolean uuidFlag = StringUtils.isBlank(uuid) || contains(uuid, machineCodeService.getSystemUuid());
        if (cpuIdFlag && ipFlag && macFlag && uuidFlag) {
            return true;
        } else {
            return false;
        }
    }

    private static Boolean contains(String str1, String str2) {
        if (StringUtils.isBlank(str1) || StringUtils.isBlank(str2)) {
            return false;
        }
        int str1Len = str1.length();
        int str2Len = str2.length();
        if (str1Len >= str2Len) {
            return str1.contains(str2);
        } else {
            return str2.contains(str1);
        }
    }
}
