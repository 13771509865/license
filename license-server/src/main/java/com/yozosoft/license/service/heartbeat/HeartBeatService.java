package com.yozosoft.license.service.heartbeat;

import com.yozosoft.license.model.HeartBeatDTO;

public interface HeartBeatService {

    Boolean heartBeat(HeartBeatDTO heartBeatDTO);
}
