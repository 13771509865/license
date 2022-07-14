package com.yozosoft.license.exception;

/**
 * 客户端调用抛出异常
 * @author zhouf
 */
public class ClientException extends RuntimeException{

    private Integer errorCode;

    private String errorMessage;

    public ClientException(Integer errorCode, String errorMessage){
        this.errorCode = errorCode;
        this.errorMessage = errorMessage;
    }

    public Integer getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(Integer errorCode) {
        this.errorCode = errorCode;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }
}
