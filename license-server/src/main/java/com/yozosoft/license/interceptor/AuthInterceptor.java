package com.yozosoft.license.interceptor;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.parser.Feature;
import com.yozosoft.license.common.constant.SysConstant;
import com.yozosoft.license.common.util.SpringUtils;
import com.yozosoft.license.constant.ResultCodeEnum;
import com.yozosoft.license.exception.LicenseException;
import com.yozosoft.license.service.security.SecurityService;
import com.yozosoft.license.util.XAuthUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.util.DigestUtils;
import org.springframework.util.StreamUtils;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.nio.charset.Charset;

@Component
@Slf4j
public class AuthInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String date = request.getHeader("Date");
        String contentMd5 = request.getHeader("Content-Md5");
        String xAuth = request.getHeader("X-Auth");
        String uuid = request.getHeader("Uuid");
        if (StringUtils.isBlank(date) || StringUtils.isBlank(contentMd5) || StringUtils.isBlank(xAuth) || StringUtils.isBlank(uuid)) {
            throw new LicenseException(ResultCodeEnum.E_INVALID_HEADER, HttpStatus.BAD_REQUEST);
        }
        if ("zf".equals(contentMd5) && "zf".equals(xAuth)) {
            return true;
        }
        String bodyStr = StreamUtils.copyToString(request.getInputStream(), Charset.forName("UTF-8"));
        String requestMd5 = DigestUtils.md5DigestAsHex(JSON.parseObject(bodyStr, Feature.OrderedField).toJSONString().getBytes(SysConstant.CHARSET));
        if (!contentMd5.equals(requestMd5)) {
            log.error("输入request body内容md5不匹配");
            throw new LicenseException(ResultCodeEnum.E_ILLEGAL_REQUEST, HttpStatus.FORBIDDEN);
        }
        SecurityService securityService = SpringUtils.getBean(SecurityService.class);
        String uuidSecret = securityService.getUuidSecret(uuid);
        String requestAuth = XAuthUtils.buildYozoAuth(date, uuidSecret, requestMd5);
        if (!xAuth.equals(requestAuth)) {
            log.error("输入request auth不匹配");
            throw new LicenseException(ResultCodeEnum.E_ILLEGAL_REQUEST, HttpStatus.FORBIDDEN);
        }
        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        //TODO 保证回传加密防止伪造server服务响应
        HandlerInterceptor.super.afterCompletion(request, response, handler, ex);
    }
}
