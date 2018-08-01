package com.alan.activemq.jms.listener;

import com.alan.activemq.service.IService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.TextMessage;

/**
 * XPayMessage消息处理类
 *
 * @author chenqiang
 */

public class OcMsgListener implements MessageListener {
    private Logger logger = LoggerFactory.getLogger(this.getClass());
    private IService iService;

    @Override
    @SuppressWarnings("unchecked")
    public void onMessage(final Message msg) {
        if (!(msg instanceof TextMessage)) {
            logger.warn("接收到的消息不是TextMessage类型的！");
            return;
        }
        TextMessage textMsg = (TextMessage) msg;
        String text;
        String systemId;
        try {
            text = textMsg.getText();
            systemId = textMsg.getStringProperty("systemcode");
            System.out.println("获取的消息:" + text);
        } catch (JMSException e) {
            logger.error("从消息中获取Json报文出错！", e);
            return;
        }
        //iService.doService(text, systemId);
    }

    public IService getIService() {
        return iService;
    }

    public void setIService(IService iService) {
        this.iService = iService;
    }

}
