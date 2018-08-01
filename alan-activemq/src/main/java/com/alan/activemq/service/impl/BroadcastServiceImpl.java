package com.alan.activemq.service.impl;

import com.alan.activemq.service.IService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author weibin
 */
public class BroadcastServiceImpl implements IService {
    private Logger logger = LoggerFactory.getLogger(getClass());

    @Override
    public void doService(String jsonStr, String systemId) {
        try {
            // 1)接收消息
            // 2)消息解析
            logger.info("商户核心-广播消息-核心处理成功{}", jsonStr);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
