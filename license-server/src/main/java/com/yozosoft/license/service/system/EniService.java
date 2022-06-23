package com.yozosoft.license.service.system;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.yozosoft.license.common.constant.SysLicenseConstant;
import com.yozosoft.license.config.LicenseConfig;
import com.yozosoft.license.model.bo.DcsLicenseBO;
import com.yozosoft.license.model.bo.SysLicenseBO;
import com.yozosoft.license.util.RSAUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

import javax.annotation.PostConstruct;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.HashMap;
import java.util.Map;

/**
 * eni文件解析及验证
 *
 * @author zhouf
 */
@Service("eniService")
public class EniService {

    @Autowired
    LicenseConfig licenseConfig;

    private final static String SEPARATOR = "######";

    private final static String MACHINE_CODE_SEPARATOR = "&";

    private final static String MACHINE_CODE_VALUE_SEPARATOR = "=";

    private String publicRSAKey;

    private String privateRSAKey;

    @PostConstruct
    public void init() {
        publicRSAKey = licenseConfig.getEniPubKey();
//        privateRSAKey = "MIICdwIBADANBgkqhkiG9w0BAQEFAASCAmEwggJdAgEAAoGBAM89O73PVYuVgBy9fEsxagaVG23PRy/r9f9hweq3CNCv5LSWLDwu0Ar3MPEfAVCfo2olf4Pt3HYfFqL39Tle55Q6YMVJHLLghDwl+dSf+ll8OsjOG+RFqjg49UcDubfyzXOTrTdWp0O1Y4M7LVjHBQbrYXwHUS/bnZtV+ihHD7MTAgMBAAECgYEAlPQ9OWuJ4B8fiwjDCsTEoWCQ63d9gjtGgD3ChLL0WhLi/s2lk8hN7mPHwLd5WXq0mTnEko3n1Nr5iT9R+d918K73OXcoMTycmyt2Dmh/TFqY3XFcGeCFMq6TPVXTokMQLr/ZyEZomiSMD/RX8dSLg5KK3n6vV4teCKynjn/CKXECQQD9f/7bUevx5J7dvPBA6DjD5PXpE6lS390PizGJHCJU3UjxA5Ut20D/L9aiPdBUx9PL5lmcMKW2Zqn+4Rk9zm1LAkEA0UhxyYwlVVbtSRnKTyNRh4UWwZFnyVWbg9Cohj70ndfldnhwKjG9N8NhYSCsHH6T/uBDNU744NfmqxFTCdacWQJBAMWSaol48dGXg5ICXaXY19Ulr9s2bbOfsmUY8USRXtiRlvXw+uXAionZOQk98A8Dpzi1kYXADId6wGdb/RZEur0CQDp8f2gu5UQLLfbu4S3qld9En05rH1ERvotA5cfWkjAGZNcN43K3QcTysiUdp1uzOBLTg5TDRCQ50fnetpEaS8kCQAQ4L2/sCG2UJ4K8y8EcFI3pF/WrNFiT6SdRUgCq0IuE2O5eWcjqfyOzCPjAUGy1703ymjM6FVG4JMMz3CsESyM=";
    }

    public SysLicenseBO parseEni(File licenseFile) throws Exception {
        BufferedReader bufferedReader = new BufferedReader(new FileReader(licenseFile));
        StringBuilder stringBuilder = new StringBuilder();
        String line;
        while ((line = bufferedReader.readLine()) != null) {
            stringBuilder.append(line);
        }
        String resultByRSA = RSAUtils.decryptByPublic(stringBuilder.toString(), publicRSAKey);
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
                return buildSysLicenseBO(jsonObject);
            }
        }
        return null;
    }

    private SysLicenseBO buildSysLicenseBO(JSONObject jsonObject) {
        SysLicenseBO sysLicenseBO = new SysLicenseBO();
        sysLicenseBO.setBusinessName(jsonObject.getString(SysLicenseConstant.BUSINESS_NAME));
        sysLicenseBO.setFeatureCode(jsonObject.getString(SysLicenseConstant.FEATURE_CODE));
        sysLicenseBO.setMaker(jsonObject.getString(SysLicenseConstant.MAKER));
        sysLicenseBO.setCreateTime(jsonObject.getDate(SysLicenseConstant.CREATE_TIME));
        DcsLicenseBO dcsLicenseBO = jsonObject.getObject(SysLicenseConstant.DCS_LICENSE, DcsLicenseBO.class);
        sysLicenseBO.setDcsLicense(dcsLicenseBO);
        return sysLicenseBO;
    }

    private Boolean checkMd5(String authInfo, String infoMd5) {
        String md5 = DigestUtils.md5DigestAsHex(authInfo.getBytes());
        return infoMd5.equals(md5);
    }

    private Boolean checkParam(JSONObject jsonObject) {
        String businessName = jsonObject.getString(SysLicenseConstant.BUSINESS_NAME);
        String featureCode = jsonObject.getString(SysLicenseConstant.FEATURE_CODE);
        if (StringUtils.isNotBlank(businessName) && StringUtils.isNotBlank(featureCode)) {
            MachineCodeService machineCodeService = new MachineCodeService();
            String machineCode = machineCodeService.decryptMachineCode(featureCode);
            Map<String, String> machineCodeMap = parseMachineCode(machineCode);
            Boolean checkResult = checkMachineCode(machineCodeMap, machineCodeService);
            return checkResult;
        }
        return false;
    }

    private Map<String, String> parseMachineCode(String machineCode) {
        Map<String, String> machineMap = new HashMap<>();
        String[] split = machineCode.split(MACHINE_CODE_SEPARATOR);
        for (String info : split) {
            String[] value = info.split(MACHINE_CODE_VALUE_SEPARATOR);
            machineMap.put(value[0], value[1]);
        }
        return machineMap;
    }

    private Boolean checkMachineCode(Map<String, String> machineCodeMap, MachineCodeService machineCodeService) {
        String cpuid = machineCodeMap.get(SysLicenseConstant.MACHINE_CODE_CPUID);
        String ip = machineCodeMap.get(SysLicenseConstant.MACHINE_CODE_IP);
        String mac = machineCodeMap.get(SysLicenseConstant.MACHINE_CODE_MAC);
        String uuid = machineCodeMap.get(SysLicenseConstant.MACHINE_CODE_UUID);
        Boolean cpuIdFlag = StringUtils.isBlank(cpuid) || contains(cpuid, machineCodeService.getCPUSerial());
        Boolean ipFlag = StringUtils.isBlank(ip) || contains(ip, machineCodeService.getRealIp());
        Boolean macFlag = StringUtils.isBlank(mac) || contains(mac, machineCodeService.getMac());
        Boolean uuidFlag = StringUtils.isBlank(uuid) || contains(uuid, machineCodeService.getSystemUuid());
        if (cpuIdFlag && ipFlag && macFlag && uuidFlag) {
            return true;
        } else {
            return false;
        }
    }

    private Boolean contains(String str1, String str2) {
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
