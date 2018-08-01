package com.cmsz.collection.kafka;

import com.cmsz.collection.util.ConfigUtil;
import kafka.consumer.ConsumerConfig;
import kafka.consumer.ConsumerIterator;
import kafka.consumer.KafkaStream;
import kafka.javaapi.consumer.ConsumerConnector;
import kafka.serializer.StringDecoder;
import kafka.utils.VerifiableProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

public class KafkaConsumer {
    private Logger logger = LoggerFactory.getLogger(this.getClass());
    private ConsumerConnector consumer;

    public KafkaConsumer() {
        /**********Begin kerberos环境配置需要************************************/
        //设置jaas-cache.conf文件，此文件由用户创建
        System.setProperty("java.security.auth.login.config", ConfigUtil.getValue("kafka.jaasCache.path"));
        System.setProperty("java.security.krb5.conf", ConfigUtil.getValue("kafka.krb5.path"));
        /**********End  kerberos环境配置需要************************************/
        Properties props = new Properties();
        props.put("zookeeper.connect", ConfigUtil.getValue("kafka.zkServers"));
        props.put("group.id", ConfigUtil.getValue("kafka.group_id"));
        props.put("zookeeper.session.timeout.ms", "4000");
        props.put("zookeeper.sync.time.ms", "200");
        props.put("auto.commit.interval.ms", "1000");
        props.put("auto.offset.reset", "smallest");
        props.put("serializer.class", "kafka.serializer.StringEncoder");
        /**********Begin kerberos环境配置需要************************************/
        props.put("security.protocol", "PLAINTEXTSASL");
        props.put("sasl.mechanism", "GSSAPI");
        /**********End kerberos环境配置需要************************************/
        ConsumerConfig config = new ConsumerConfig(props);
        consumer = kafka.consumer.Consumer.createJavaConsumerConnector(config);
    }

    public void consume() {
        String topic = ConfigUtil.getValue("kafka.dataCollection.topicName");
        Map<String, Integer> topicCountMap = new HashMap<String, Integer>();
        topicCountMap.put(topic, new Integer(1));
        StringDecoder keyDecoder = new StringDecoder(new VerifiableProperties());
        StringDecoder valueDecoder = new StringDecoder(new VerifiableProperties());
        Map<String, List<KafkaStream<String, String>>> consumerMap = consumer.createMessageStreams(topicCountMap, keyDecoder, valueDecoder);
        KafkaStream<String, String> stream = consumerMap.get(topic).get(0);
        ConsumerIterator<String, String> it = stream.iterator();
        while (it.hasNext()) {
            String message = it.next().message();
        }
    }
}