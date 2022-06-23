package com.yozosoft.license.service.system;

import com.yozosoft.license.common.constant.SysConstant;
import com.yozosoft.license.model.bo.SysLicenseBO;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Service;

import javax.annotation.PreDestroy;
import java.io.File;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;
import java.nio.file.StandardOpenOption;

/**
 * 初始化各应用授权信息
 *
 * @author zhouf
 */
@Service("licensePropService")
@Data
public class LicensePropService implements ApplicationRunner {

    private SysLicenseBO sysLicenseBO;

    private FileChannel fileChannel;

    @Autowired
    EniService eniService;

    @PreDestroy
    public void destroy() throws IOException {
        if (fileChannel != null) {
            fileChannel.close();
        }
    }

    @Override
    public void run(ApplicationArguments args) throws Exception {
        String rootPath = LicensePropService.class.getResource("/").getPath();
        File licenseFile = new File(rootPath, SysConstant.LICENSE_FILE_NAME);
        if (!licenseFile.isFile()) {
            System.out.println("授权文件不存在,授权中心启动失败");
            System.exit(0);
        }
        try {
            //确保单台服务器只有一个license实例节点
            checkUnique(licenseFile);
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("初始化授权中心服务失败,唯一性约束检查失败");
            System.exit(0);
        }
        //初始化获取授权eni文件内容,根据文件类型初始化各应用LicenseBO
        try {
            sysLicenseBO = eniService.parseEni(licenseFile);
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("解析授权文件失败,请检查授权文件");
            System.exit(0);
        }
    }

    private void checkUnique(File licenseFile) throws IOException {
        fileChannel = FileChannel.open(licenseFile.toPath(), StandardOpenOption.APPEND);
        FileLock fileLock = fileChannel.tryLock();
        if (fileLock == null) {
            //获取文件锁失败,表示本机已启动一个license实例
            System.out.println("license实例节点冲突,请勿重复启动");
            System.exit(0);
        }
    }
}
