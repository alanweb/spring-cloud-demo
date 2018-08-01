package com.cmsz.collection.kerberos;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

/**
 * @author weibin
 */
@Component
public class KafkaConsumerKerberos {
    private static final Logger logger = LoggerFactory.getLogger(KafkaConsumerKerberos.class);
    @Value("${krb5.conf}")
    private String krb5Conf;
    @Value("${jaasCache.conf}")
    private String jaasCacheConf;
    @Autowired
    BeanFactory beanFactory;

    @PostConstruct
    private void init() {
        /**********Begin kerberos环境配置需要************************************/
        //设置jaas-cache.conf文件，此文件由用户创建
//        System.setProperty("java.security.auth.login.config", krb5Conf);
//        System.setProperty("java.security.krb5.conf", jaasCacheConf);
        /**********End  kerberos环境配置需要************************************/
        logger.info("KafkaConsumerKerberos init ok!");
        beanFactory.getBean("kafkaReciever");
    }
}