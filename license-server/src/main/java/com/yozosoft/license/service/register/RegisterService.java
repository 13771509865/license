package com.yozosoft.license.service.register;

import com.yozosoft.license.model.CancelDTO;
import com.yozosoft.license.model.RegisterDTO;

public interface RegisterService {

    String register(RegisterDTO registerDTO);

    /**
     * 注销
     */
    Boolean cancel(CancelDTO cancelDTO);
}
