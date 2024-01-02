package com.yozosoft.license.exception;

import com.yozosoft.license.common.util.ResultUtils;
import com.yozosoft.license.constant.ResultCodeEnum;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.BindException;
import org.springframework.validation.ObjectError;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * @author zhoufeng
 * @description 全局异常处理,处理各种异常
 * @create 2021-10-21 14:53
 **/
@ControllerAdvice
@Slf4j
public class GlobalDefaultExceptionHandler {

    @ExceptionHandler(BindException.class)
    @ResponseBody
    public ResponseEntity argValidExceptionHandler(BindException e) {
        List<ObjectError> allErrors = e.getAllErrors();
        List<String> message = new ArrayList<>();
        for (ObjectError error : allErrors) {
            message.add(error.getDefaultMessage());
        }
        return new ResponseEntity(ResultUtils.buildErrorResult(ResultCodeEnum.E_INVALID_PARAM.getValue(), StringUtils.join(message, ",")), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(LicenseException.class)
    @ResponseBody
    public ResponseEntity LicenseExceptionHandler(LicenseException e) {
        HttpStatus httpStatus = e.getHttpStatus();
        System.out.println("httpStatus:"+httpStatus);
        httpStatus = Optional.ofNullable(httpStatus).orElse(HttpStatus.EXPECTATION_FAILED);
        return new ResponseEntity(ResultUtils.buildErrorResult(e.getErrorCode(), e.getErrorMessage(), null), httpStatus);
    }


    @ExceptionHandler(Throwable.class)
    @ResponseBody
    public ResponseEntity defaultExceptionHandler(Throwable ex) {
        if (ex instanceof HttpRequestMethodNotSupportedException) {
            return new ResponseEntity(ResultUtils.buildErrorResult(HttpStatus.METHOD_NOT_ALLOWED), HttpStatus.METHOD_NOT_ALLOWED);
        } else if (ex instanceof HttpMediaTypeNotSupportedException) {
            return new ResponseEntity<>(ResultUtils.buildErrorResult(HttpStatus.UNSUPPORTED_MEDIA_TYPE), HttpStatus.UNSUPPORTED_MEDIA_TYPE);
        } else if (ex instanceof HttpMessageNotReadableException || ex instanceof MissingServletRequestParameterException) {
            return new ResponseEntity<>(ResultUtils.buildErrorResult(ResultCodeEnum.E_INVALID_PARAM), HttpStatus.BAD_REQUEST);
        }
        log.error("全局异常处理器捕获异常,请检查", ex);
        return new ResponseEntity(ResultUtils.buildErrorResult(ResultCodeEnum.E_SERVER_UNKNOWN_ERROR), HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
