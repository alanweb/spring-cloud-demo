package com.cmsz.collection.kafka;

import com.cmsz.collection.util.ConfigUtil;
import kafka.javaapi.producer.Producer;
import kafka.producer.KeyedMessage;
import kafka.producer.ProducerConfig;

import java.util.Properties;


public class KafkaProducer {
    private final Producer<String, String> producer;

    public KafkaProducer() {
        System.setProperty("java.security.auth.login.config", ConfigUtil.getValue("kafka.jaasCache.path"));
        System.setProperty("java.security.krb5.conf", ConfigUtil.getValue("kafka.krb5.path"));
        Properties props = new Properties();
        props.put("metadata.broker.list", ConfigUtil.getValue("kafka.broker.list"));
        props.put("serializer.class", "kafka.serializer.StringEncoder");
        props.put("key.serializer.class", "kafka.serializer.StringEncoder");
        props.put("security.protocol", "PLAINTEXTSASL");
        props.put("request.required.acks", "-1");
        producer = new Producer<String, String>(new ProducerConfig(props));
    }

    public void produce(String message) {
        KeyedMessage km = new KeyedMessage<String, String>(ConfigUtil.getValue("kafka.dataCollection.topicName"), message);
        producer.send(km);
    }

    public static void main(String[] args) {
        KafkaProducer producer = new KafkaProducer();
        producer.produce("hello world!!!");
    }
}