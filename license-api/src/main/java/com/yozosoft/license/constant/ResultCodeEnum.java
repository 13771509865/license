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

    E_INVALID_HEADER(5, "头信息验证失败"),

    E_ILLEGAL_REQUEST(6, "非法请求"),

    E_STRATEGY_NULL(1000, "限制策略初始化失败"),

    E_REGISTER_FAIL(1001, "当前服务注册授权中心失败"),

    E_INSTANCE_SERVICE_NOT_EXIST(1002, "实例集群不存在,请重新注册"),

    E_HEARTBEAT_INSTANCE_NOT_EXIST(1003, "心跳监测异常,实例不存在"),

    E_HEARTBEAT_INSTANCE_MATCH_FAIL(1004, "心跳监测异常,实例检查失败"),

    E_HEARTBEAT_ERROR(1005, "心跳监测失败"),

    E_REGISTER_INSTANCE_ALREADY_EXIST(1006, "服务注册异常,实例已存在"),

    E_HANDSHAKE_FAIL(1007, "握手失败"),

    E_EXCHANGE_SECRET_DECRYPT_ERROR(1008, "exchange密钥无效,请检查加密"),

    E_RANDOM_RSA_SECRET_FAIL(1009, "随机生成密钥失败"),

    E_CANCEL_INSTANCE_MATCH_FAIL(1014, "注销instance异常,实例检查失败"),

    E_CANCEL_ERROR(1015, "注销instance失败"),

    E_INSTANCE_NOT_EXIST(1016, "实例不存在，或已失效"),

    E_SECRET_NOT_EXIST(1017, "密钥不存在，或已失效"),
    E_HEARTBEAT_LICENSE_EXPIRED(1018, "心跳监测异常,授权时间不在有效范围内"),
    /**
     * Dcs相关错误码2开头
     */
    E_DCS_ADD_STRATEGY_FAIL(2000, "并发数超过限制,请联系永中进行授权升级"),
    E_DCS_REDUCE_STRATEGY_FAIL(2001, "移除Dcs实例节点时策略检查失败"),
    E_DCS_LICENSE_CONTENT_ERROR(2002, "Dcs授权文件异常,所需元数据校验不正确"),
    E_DCS_LICENSE_EXPIRED(2003, "Dcs授权时间不在有效范围内"),
    /**
     * 文档平台相关错误码3开头
     */
    E_DOC_LICENSE_EXPIRED(3000, "文档平台授权时间不在有效范围内"),
    /**
     * client调用错误9开头
     */
    E_CLIENT_HTTP_FAIL(9000, "客户端http调用通信失败"),
    E_CLIENT_AUTHORIZATION_NOT_EXIST(9001, "授权文件不存在"),
    E_CLIENT_RESOLVE_AUTHORIZATION_FAIL(9002, "解析授权文件失败"),
    E_CLIENT_AUTHORIZATION_EXPIRED(9003, "授权时间不在有效范围内"),
    E_CLIENT_FORMAT_ERROR(9004, "授权内容校验有误"),

    E_CLIENT_MACHINE_CODE_MISS_MATCH(9005, "授权机器码信息不匹配"),
    ;


    private Integer value;

    private String info;

    ResultCodeEnum(Integer value, String info) {
        this.value = value;
        this.info = info;
    }
}
