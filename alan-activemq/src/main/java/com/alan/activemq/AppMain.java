package com.alan.activemq;

import com.alan.activemq.constant.MsgConstant;
import com.alan.activemq.jms.listener.GroupQueueMessageListenerContainer;
import com.alan.activemq.jms.listener.OcMsgListener;
import com.alan.activemq.jms.sender.AdvancedGroupQueueSender;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.ArrayList;
import java.util.List;

@SpringBootApplication
public class AppMain {
    private static List<String> brokerURLs = new ArrayList<>();

    public static void main(String[] args) {
        SpringApplication.run(AppMain.class, args);
        brokerURLs.add("tcp://119.119.10.129:61616");
        brokerURLs.add("tcp://119.119.10.129:62626");
        startConsumer();
        startSender();
    }
    private static void startConsumer() {
        GroupQueueMessageListenerContainer consumer = new GroupQueueMessageListenerContainer();
        consumer.setTopic(true);
        consumer.setConcurrentConsumers(1);
        consumer.setMaxConcurrentConsumers(2);
        consumer.setGroupQueueName("oc.broadcast.2core");
        consumer.setBrokerURLs(brokerURLs);
        consumer.setMessageListener(new OcMsgListener());
        consumer.start();
    }
    private static void startSender() {
        AdvancedGroupQueueSender sender = new AdvancedGroupQueueSender();
        sender.setTopic(true);
        sender.setBrokerURLs(brokerURLs);
        sender.setMaxConnection(1);
        sender.setGroupQueueName("oc.broadcast.2core");
        sender.start();
        for (int i = 0; i < 4; i++) {
            String jsonStr = "{name:\"zhangsan\",age:" + i + "}";
            sender.sendMsg(jsonStr, MsgConstant.SUB_QUEUE_NAME_DEFALT);
        }
    }
}
