package com.yozosoft.license.exception;

import com.yozosoft.license.constant.ResultCodeEnum;
import lombok.Data;
import org.springframework.http.HttpStatus;

@Data
public class LicenseException extends RuntimeException{

    private Integer errorCode;

    private String errorMessage;

    private Object data;

    private HttpStatus httpStatus;

    public LicenseException(Integer errorCode, String errorMessage){
        this.errorCode = errorCode;
        this.errorMessage = errorMessage;
        this.httpStatus = HttpStatus.EXPECTATION_FAILED;
    }

    public LicenseException(Integer errorCode, String errorMessage, Object data){
        this.errorCode = errorCode;
        this.errorMessage = errorMessage;
        this.data = data;
        this.httpStatus = HttpStatus.EXPECTATION_FAILED;
    }

    public LicenseException(ResultCodeEnum resultCodeEnum){
        this.errorCode = resultCodeEnum.getValue();
        this.errorMessage = resultCodeEnum.getInfo();
    }

    public LicenseException(Integer errorCode, String errorMessage, Object data, HttpStatus httpStatus){
        this.errorCode = errorCode;
        this.errorMessage = errorMessage;
        this.data = data;
        this.httpStatus = httpStatus;
    }

    public LicenseException(Integer errorCode, String errorMessage, HttpStatus httpStatus){
        this.errorCode = errorCode;
        this.errorMessage = errorMessage;
        this.httpStatus = httpStatus;
    }
}
