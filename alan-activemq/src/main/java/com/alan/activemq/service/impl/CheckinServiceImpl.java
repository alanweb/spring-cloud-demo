package com.alan.activemq.service.impl;


import com.alan.activemq.constant.MsgConstant;
import com.alan.activemq.jms.sender.AdvancedGroupQueueSender;
import com.alan.activemq.service.IService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * @author LDC
 * @ClassName SignInService
 * @Description 签到，签退类
 * @date 2016/02/26
 */
public class CheckinServiceImpl implements IService {
    private Logger logger = LoggerFactory.getLogger(this.getClass());

    /**
     * 发送MQ消息工具类
     **/
    private AdvancedGroupQueueSender broadcastSender;

    @Override
    public void doService(String jsonStr, String systemId) {
        jsonStr = "{name:\"zhangsan\"}";
        logger.info("商户核心-签到签退-发送签到签退消息广播", jsonStr);
        broadcastSender.sendMsg(jsonStr, MsgConstant.SUB_QUEUE_NAME_DEFALT);
    }

    public AdvancedGroupQueueSender getBroadcastSender() {
        return broadcastSender;
    }

    public void setBroadcastSender(AdvancedGroupQueueSender broadcastSender) {
        this.broadcastSender = broadcastSender;
    }
}
