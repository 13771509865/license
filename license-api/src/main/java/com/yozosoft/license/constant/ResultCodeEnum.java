package com.yozosoft.license.constant;

import lombok.Getter;

@Getter
public enum ResultCodeEnum {
    /**
     * 结果枚举值
     */
    E_SUCCESS(0, "操作成功"),
    E_FAIL(1, "操作失败"),
    E_INVALID_PARAM(3, "参数校验失败,请检查参数合法性"),
    E_SERVER_UNKNOWN_ERROR(4, "服务未知错误"),
    ;


    private Integer value;

    private String info;

    ResultCodeEnum(Integer value, String info) {
        this.value = value;
        this.info = info;
    }
}
