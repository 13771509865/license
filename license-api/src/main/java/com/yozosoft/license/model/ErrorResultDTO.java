package com.yozosoft.license.model;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author zhouf
 */
@Data
@NoArgsConstructor
public class ErrorResultDTO {

    private Integer errorCode;

    private String errorMessage;

    private Object data;
}
