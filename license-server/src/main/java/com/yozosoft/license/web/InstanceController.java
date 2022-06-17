package com.yozosoft.license.web;

import com.yozosoft.license.constant.ResultCodeEnum;
import com.yozosoft.license.exception.LicenseException;
import com.yozosoft.license.model.HeartBeatDTO;
import com.yozosoft.license.model.RegisterDTO;
import com.yozosoft.license.service.heartbeat.HeartBeatService;
import com.yozosoft.license.service.register.RegisterService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/instance")
public class InstanceController {

    @Autowired
    RegisterService registerService;

    @Autowired
    HeartBeatService heartBeatService;

    @PutMapping("/beat")
    public ResponseEntity beat(@RequestBody HeartBeatDTO heartBeatDTO) {
        /**
         * 暂不支持心跳监测时自动重新注册
         */
        Boolean heartBeatResult = heartBeatService.heartBeat(heartBeatDTO);
        if (!heartBeatResult) {
            throw new LicenseException(ResultCodeEnum.E_HEARTBEAT_ERROR);
        }
        return ResponseEntity.ok(System.currentTimeMillis());
    }

    @PostMapping("/register")
    public ResponseEntity register(@RequestBody RegisterDTO registerDTO) {
        String register = registerService.register(registerDTO);
        if (StringUtils.isBlank(register)) {
            throw new LicenseException(ResultCodeEnum.E_REGISTER_FAIL);
        }
        return ResponseEntity.ok(register);
    }
}
