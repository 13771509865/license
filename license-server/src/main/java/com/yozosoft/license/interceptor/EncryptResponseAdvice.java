package com.yozosoft.license.interceptor;

import com.yozosoft.license.service.security.SecurityService;
import com.yozosoft.license.util.AESUtils;
import com.yozosoft.license.web.InstanceController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.MethodParameter;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

import java.util.List;

/**
 * response加密处理
 *
 * @author zhouf
 */
@ControllerAdvice(assignableTypes  = {InstanceController.class})
public class EncryptResponseAdvice implements ResponseBodyAdvice<Object> {

    @Autowired
    SecurityService securityService;

    @Override
    public boolean supports(MethodParameter returnType, Class<? extends HttpMessageConverter<?>> converterType) {
        return true;
    }

    @Override
    public Object beforeBodyWrite(Object body, MethodParameter returnType, MediaType selectedContentType, Class<? extends HttpMessageConverter<?>> selectedConverterType, ServerHttpRequest request, ServerHttpResponse response) {
        HttpHeaders headers = request.getHeaders();
        List<String> uuids = headers.get("Uuid");
        String uuid = uuids.get(0);
        String uuidSecret = securityService.getUuidSecret(uuid);
        String encryptBody = AESUtils.encrypt(body, uuidSecret);
        return encryptBody;
    }
}
