package com.alan.elk.config;

import com.alan.common.util.StringUtils;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.elasticsearch.transport.client.PreBuiltTransportClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * @author alan
 * @date 2018/7/26
 */
@Component
public class ElasticSearchManager {
    private Logger logger = LoggerFactory.getLogger(getClass());
    public TransportClient client;
    @Value("${es.cluster.nodes}")
    private String nodes;

    @PostConstruct
    private void init() {
        //设置集群名称 (外网可以/内网需要指定ip地址)
        //client-transport-sniff 为 true自动扫描集群
        //Settings settings = Settings.builder().put("cluster.name", clusterName).put("client-transport-sniff",true).build();
        if (StringUtils.isBlank(nodes)) {
            logger.error("elasticSearch init error : es.hostNames is empty");
            return;
        }
        String[] hostNameArr = nodes.split(",");
        try {
            client = new PreBuiltTransportClient(Settings.EMPTY);
            for (String hostName : hostNameArr) {
                String[] arr = hostName.split(":");
                String ip = arr[0];
                int port = 9300;
                if (arr.length != 1) {
                    port = Integer.parseInt(arr[1]);
                }
                client.addTransportAddresses(new InetSocketTransportAddress(InetAddress.getByName(ip), port));
            }
        } catch (UnknownHostException e) {
            e.printStackTrace();
            logger.error("elasticSearch init error {}", e.getMessage());
        }
    }

    public void close() {
        if (client != null) {
            client.close();
        }
    }
}
