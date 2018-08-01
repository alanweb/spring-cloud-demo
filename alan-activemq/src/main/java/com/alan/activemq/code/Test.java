package com.alan.activemq.code;

import javax.jms.JMSException;

/**
 * @author alan
 * @date 2018/6/21
 */
public class Test {
    public static void main(String[] args) {
        String brokerUrl = "tcp://119.119.10.129:61616";
        ConsumerTool consumer = new ConsumerTool();
        ProducerTool producer = new ProducerTool();
        producer.setBrokerUrl(brokerUrl);
        consumer.setBrokerUrl(brokerUrl);
        try {
            //接受消息方式一：通过监听Listener 开始监听
            // consumer.consumeMessage();
            Thread.sleep(500);
            producer.sendMessage("张三");
            producer.sendMessage("历史");
            producer.sendMessage("王五");
            producer.close();
            //接受消息方式2：主动方式， 必须在producer产生消息后，去获取，否则获取不到
            consumer.receiveMessage();
            // 延时500毫秒之后停止接受消息
            Thread.sleep(500);
            consumer.close();
        } catch (JMSException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
