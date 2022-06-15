package com.yozosoft.license.constant;

import lombok.Getter;

/**
 * 租户类型枚举
 *
 * @author zhouf
 */
@Getter
public enum TenantEnum {

    /**
     * 具体租户枚举值
     */
    E_DCS(1, "dcs", "Dcs服务"),
    E_EP(2, "ep", "云文档服务"),
    ;

    private Integer tenantId;

    private String tenantName;

    private String tenantInfo;

    TenantEnum(Integer tenantId, String tenantName, String tenantInfo) {
        this.tenantId = tenantId;
        this.tenantName = tenantName;
        this.tenantInfo = tenantInfo;
    }

    public static TenantEnum getTenantByName(String tenantName) {
        for (TenantEnum tenantEnum : values()) {
            if (tenantEnum.getTenantName().equals(tenantName)) {
                return tenantEnum;
            }
        }
        return null;
    }
}
