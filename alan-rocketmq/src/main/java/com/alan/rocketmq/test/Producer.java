package com.alan.rocketmq.test;

import com.alibaba.rocketmq.client.exception.MQBrokerException;
import com.alibaba.rocketmq.client.exception.MQClientException;
import com.alibaba.rocketmq.client.producer.DefaultMQProducer;
import com.alibaba.rocketmq.client.producer.SendResult;
import com.alibaba.rocketmq.common.message.Message;
import com.alibaba.rocketmq.remoting.exception.RemotingException;

import java.io.UnsupportedEncodingException;

/**
 * @author alan
 * @date 2018/6/27
 */
public class Producer {
    public static void main(String[] args) throws MQClientException, InterruptedException {
        DefaultMQProducer producer = new DefaultMQProducer("ProducerGroup");
        producer.setNamesrvAddr("119.119.10.129:9876;119.119.10.130:9876");
        producer.start();
        for (int i = 1; i < 10; i++) {
            try {
                Message message = new Message("TOPIC-A", "tag-a", ("hello rocketmq" + i).getBytes("utf-8"));
                SendResult result = producer.send(message);
                System.out.println(result);
            } catch (Exception e) {
                e.printStackTrace();
                Thread.sleep(1000);
            }
        }
        producer.shutdown();
        System.out.println("Producer is Shutdown.");
    }
}
