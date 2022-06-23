package com.yozosoft.license.web;

import com.yozosoft.license.constant.ResultCodeEnum;
import com.yozosoft.license.exception.LicenseException;
import com.yozosoft.license.util.RSAUtils;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.NoSuchAlgorithmException;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/util")
public class UtilController {

    @GetMapping("/secret")
    public ResponseEntity randomRSASecret() {
        try {
            Map<String, String> secretMap = RSAUtils.genKeyPair();
            return ResponseEntity.ok(secretMap);
        } catch (NoSuchAlgorithmException e) {
            throw new LicenseException(ResultCodeEnum.E_RANDOM_RSA_SECRET_FAIL);
        }
    }
}
