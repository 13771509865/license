package com.yozosoft.license.service.system;

import com.yozosoft.license.model.bo.DcsLicenseBO;
import lombok.Data;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Service;

/**
 * 初始化各应用授权信息
 *
 * @author zhouf
 */
@Service("licensePropService")
@Data
public class LicensePropService implements ApplicationRunner {

    private DcsLicenseBO dcsLicenseBO;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        //初始化获取授权eni文件内容,根据文件类型初始化各应用LicenseBO
    }

    private void initEni() {

    }
}
