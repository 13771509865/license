package com.yozosoft.license.web;

import com.yozosoft.license.constant.ResultCodeEnum;
import com.yozosoft.license.exception.LicenseException;
import com.yozosoft.license.model.HandshakeResultDTO;
import com.yozosoft.license.model.SecretDTO;
import com.yozosoft.license.service.security.SecurityService;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/security")
public class SecurityController {

    @Autowired
    SecurityService securityService;

    @GetMapping("/handshake")
    public ResponseEntity handshake() {
        HandshakeResultDTO handshake = securityService.handshake();
        if(handshake == null){
            throw new LicenseException(ResultCodeEnum.E_HANDSHAKE_FAIL);
        }
        return ResponseEntity.ok(handshake);
    }

    @PostMapping("/secret")
    public ResponseEntity exchangeSecret(@RequestBody SecretDTO secretDTO){
        securityService.exchangeSecret(secretDTO);
        return ResponseEntity.ok(ResultCodeEnum.E_SUCCESS.getValue());
    }
}
