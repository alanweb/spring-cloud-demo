package com.alan.activemq.jms.listener;

import com.alan.activemq.constant.MsgConstant;
import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.command.ActiveMQMessage;
import org.apache.activemq.command.ActiveMQQueue;
import org.apache.activemq.command.ActiveMQTopic;
import org.apache.activemq.command.DestinationInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.SmartLifecycle;
import org.springframework.jms.connection.CachingConnectionFactory;
import org.springframework.jms.listener.DefaultMessageListenerContainer;

import javax.jms.ExceptionListener;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * GroupQueueMessageListenerContainer 可以同时对多个broker中有相同前缀的队列进行消费。 将有相同前缀的队列归为同一队列组，
 * 通过jmx获得同一组的所有队列的队列名，然后创建DefaultMessageListenerContainer进行消费。
 *
 * @author shuangzh
 */

public class GroupQueueMessageListenerContainer implements SmartLifecycle {

    private final Logger logger =
            LoggerFactory.getLogger(getClass());

    /**
     * activeMq broker的链接 url列表
     */
    private List<String> brokerURLs;

    /**
     * 用于处理消息的 messageListener;
     */
    private MessageListener messageListener;
    /**
     * 消息选择器
     */
    private String messageSelector;
    /**
     * 队列组名
     */
    private String GroupQueueName = "Test";

    /**
     * 每个DefaultMessageListenerContainer的并发消费者数
     */
    private int concurrentConsumers = 1;

    private int maxConcurrentConsumers = 1;
    private boolean topic = false;
    /**
     * 每个broker的最大链接数
     */
    @SuppressWarnings("unused")
    private int maxConnections = 1;

    private String jointMark = "-";

    /**
     * 制定配置 子队列的consumer数量， 配置字符串为如 beijin:3,hebei:10 此时 GroupName-beijin队列将配置3个consumer,
     * GroupName-hebei队列将配置10个consumer
     */
    private String subQueueConsumerCfg = null;

    private Map<String, Integer> consumerCfgMap = new HashMap<String, Integer>();
    private Map<String, Integer> maxConsumerCfgMap = new HashMap<String, Integer>();

    private volatile boolean isRunning = false;

    private final String ADVISORY_SUFFIX_QUEUE = "ActiveMQ.Advisory.Queue";
    private final String ADVISORY_SUFFIX_TOPIC = "ActiveMQ.Advisory.Topic";
    private String advisoryTopic = "";
    @SuppressWarnings("unused")
    private String advisoryTopic_Topic = "ActiveMQ.Advisory.Topic";
    private AdvisoryListener advisoryListener = new AdvisoryListener();

    // ZXJF_SCDT-146
    private Map<String, CachingConnectionFactory> pooledConnectionFactoryMap = new HashMap<>();
    private List<String> AllQueueNames = new ArrayList<String>();
    private List<String> AllQueueTypes = new ArrayList<String>();
    private Map<String, DefaultMessageListenerContainer> containerMap =
            new HashMap<String, DefaultMessageListenerContainer>();
    private JMSReportListener jmsReportListener = new JMSReportListener();

    public boolean isTopic() {
        return topic;
    }

    public void setTopic(boolean topic) {
        this.topic = topic;
    }

    public int getConcurrentConsumers() {
        return concurrentConsumers;
    }

    public void setConcurrentConsumers(int concurrentConsumers) {
        this.concurrentConsumers = concurrentConsumers;
    }

    public String getMessageSelector() {
        return messageSelector;
    }

    public void setMessageSelector(String messageSelector) {
        this.messageSelector = messageSelector;
    }

    public MessageListener getMessageListener() {
        return messageListener;
    }

    public void setMessageListener(MessageListener messageListener) {
        this.messageListener = messageListener;
    }

    public List<String> getBrokerURLs() {
        return brokerURLs;
    }

    public void setBrokerURLs(List<String> brokerURLs) {
        this.brokerURLs = brokerURLs;
    }

    public String getGroupQueueName() {
        return GroupQueueName;
    }

    public void setGroupQueueName(String groupQueueName) {
        GroupQueueName = groupQueueName;
    }

    @Override
    public void start() {
        if (!(brokerURLs != null && brokerURLs.size() > 0)) {
            return;
        }
        if (!(messageListener != null)) {
            return;
        }
        logger.info("start GroupQueueMessageListenerContainer ...");

        this.advisoryTopic = isTopic() ? ADVISORY_SUFFIX_TOPIC : ADVISORY_SUFFIX_QUEUE;

        /**
         * 配置subQueue 的consumer 数量
         */
        if (subQueueConsumerCfg != null) {
            String[] cfgs = subQueueConsumerCfg.split(",");
            for (String cfg : cfgs) {
                cfg = cfg.trim();
                String[] ca = cfg.split(":");
                if (ca.length != 3) {
                    logger.error("subqueue consumer number configuration error. " + cfg);
                } else {
                    try {
                        ca[0] = ca[0].trim();
                        ca[1] = ca[1].trim();
                        ca[2] = ca[2].trim();
                        Integer n = Integer.parseInt(ca[1]);
                        Integer m = Integer.parseInt(ca[2]);
                        consumerCfgMap.put(ca[0], n);
                        maxConsumerCfgMap.put(ca[0], m);
                    } catch (Exception e) {
                        logger.error("subqueue consumer number is not a number . " + cfg, e);
                    }
                }
            }
        }

        initConnectionFactory();

        /**
         * 采用 advisory Message 获取broker上的所有队列
         */
        startAdvisoryContainer();

        isRunning = true;
    }

    private void startAdvisoryContainer() {
        for (String brokerUrl : brokerURLs) {
            String key = "";
            if (isTopic()) {
                key = brokerUrl + "," + GroupQueueName + jointMark
                        + MsgConstant.SUB_QUEUE_NAME_DEFALT;
            } else {
                key = brokerUrl + "," + advisoryTopic;
            }
            // ZXJF_SCDT-133
            if (!containerMap.containsKey(key)) {
                DefaultMessageListenerContainer container = new DefaultMessageListenerContainer();
                container.setConnectionFactory(pooledConnectionFactoryMap.get(brokerUrl));
                container.setConcurrentConsumers(1);
                container.setExceptionListener(jmsReportListener);

                if (isTopic()) {
                    container.setDestination(new ActiveMQTopic(
                            GroupQueueName + jointMark + MsgConstant.SUB_QUEUE_NAME_DEFALT));
                    container.setMessageListener(messageListener);
                } else {
                    container.setDestination(new ActiveMQTopic(advisoryTopic));
                    container.setMessageListener(advisoryListener);
                }
                container.setRecoveryInterval(30000);
                container.initialize();
                container.start();
                containerMap.put(key, container);
            }
            logger.info("container for advisory queue topic [" + key + "] had created");

        }
    }

    class AdvisoryListener implements MessageListener {

        @Override
        public void onMessage(Message message) {

            logger.info("AdvisoryListener receive message");
            if (message instanceof ActiveMQMessage) {
                ActiveMQMessage aMsg = (ActiveMQMessage) message;
                DestinationInfo desInf = (DestinationInfo) aMsg.getDataStructure();

                String queueName = desInf.getDestination().getPhysicalName();
                if (queueName != null && desInf.isAddOperation()) {
                    addContainer(queueName, aMsg.getDestination().getPhysicalName());
                }
            } else {
                logger.error(
                        "AdvisoryListener recieve message is not ActiveMQMessage, that is abnormal.");
            }

        }

        // synchronized public void addContainer(String queueName)
        // ZXJF_SCDT-128
        synchronized public void addContainer(String queueName, String advisoryName) {
            String prefix = GroupQueueName;
            if (queueName.startsWith(prefix)) {
                if (!AllQueueNames.contains(queueName)) {
                    AllQueueNames.add(queueName);
                    AllQueueTypes.add(advisoryName);
                    logger.info("discover new queue " + queueName);

                }
            }
            updateContainers();
        }
    }

    private void initConnectionFactory() {
        for (String url : brokerURLs) {
            // ZXJF_SCDT-133
            if (!pooledConnectionFactoryMap.containsKey(url)) {
                ActiveMQConnectionFactory activeMQConnectionFactory =
                        new ActiveMQConnectionFactory();
                activeMQConnectionFactory.setBrokerURL(url);
                // ZXJF_SCDT-146
                CachingConnectionFactory pooledConnectionFactory =
                        new CachingConnectionFactory(activeMQConnectionFactory);
                pooledConnectionFactory.setSessionCacheSize(100);
                pooledConnectionFactoryMap.put(url, pooledConnectionFactory);
            }
        }
    }

    synchronized private void updateContainers() {
        for (String brokerUrl : brokerURLs) {
            for (int i = 0; i < AllQueueNames.size(); i++) {
                String name = AllQueueNames.get(i);
                String type = AllQueueTypes.get(i);
                String key = brokerUrl + "," + name;
                DefaultMessageListenerContainer container = containerMap.get(key);
                if (container == null) {
                    logger.info("there is no DefaultMessageListenerContainer for queue " + key
                            + ", new containner will be created");
                    container = new DefaultMessageListenerContainer();
                    container.setConnectionFactory(pooledConnectionFactoryMap.get(brokerUrl));
                    container.setExceptionListener(jmsReportListener);
                    container.setRecoveryInterval(30000);
                    /**
                     * 设置subQueue的consumers数量
                     */
                    String subname = name.replaceFirst(GroupQueueName + jointMark, "");
                    subname = subname.trim();
                    Integer n = consumerCfgMap.get(subname);
                    if (n != null) {
                        container.setConcurrentConsumers(n);
                    } else {
                        container.setConcurrentConsumers(concurrentConsumers);
                    }
                    Integer m = maxConsumerCfgMap.get(subname);
                    if (m != null) {
                        container.setMaxConcurrentConsumers(n);
                    } else {
                        container.setMaxConcurrentConsumers(maxConcurrentConsumers);
                    }

                    container.setMessageSelector(messageSelector);
                    if (ADVISORY_SUFFIX_QUEUE.equals(type)) {
                        container.setDestination(new ActiveMQQueue(name));
                    } else {
                        container.setDestination(new ActiveMQTopic(name));
                    }
                    container.setMessageListener(messageListener);
                    container.initialize();
                    container.start();

                    containerMap.put(key, container);
                    logger.info(
                            "DefaultMessageListenerContainer for queue " + key + " had created");
                }
            }
        }
    }

    @Override
    synchronized public void stop() {
        for (DefaultMessageListenerContainer container : containerMap.values()) {
            container.destroy();
            container.stop();
        }
        // ZXJF_SCDT-146
        for (CachingConnectionFactory factory : pooledConnectionFactoryMap.values()) {
            factory.destroy();
        }
        isRunning = false;
    }

    @Override
    public boolean isRunning() {
        return this.isRunning;
    }

    @Override
    public int getPhase() {
        return 0;
    }

    @Override
    public boolean isAutoStartup() {
        return true;
    }

    @Override
    public void stop(Runnable callback) {
        // ZXJF_SCDT-133
        stop();
        callback.run();
    }

    /**
     * 获取maxConcurrentConsumers
     *
     * @return the maxConcurrentConsumers
     */
    public int getMaxConcurrentConsumers() {
        return maxConcurrentConsumers;
    }

    /**
     * 设置 maxConcurrentConsumers
     *
     * @param maxConcurrentConsumers the maxConcurrentConsumers to set
     */
    public void setMaxConcurrentConsumers(int maxConcurrentConsumers) {
        this.maxConcurrentConsumers = maxConcurrentConsumers;
    }

    public String getSubQueueConsumerCfg() {
        return subQueueConsumerCfg;
    }

    public void setSubQueueConsumerCfg(String subQueueConsumerCfg) {
        this.subQueueConsumerCfg = subQueueConsumerCfg;
    }

    public Map<String, Integer> getConsumerCfgMap() {
        return consumerCfgMap;
    }

    public void setConsumerCfgMap(Map<String, Integer> consumerCfgMap) {
        this.consumerCfgMap = consumerCfgMap;
    }

    /**
     * 获取maxConsumerCfgMap
     *
     * @return the maxConsumerCfgMap
     */
    public Map<String, Integer> getMaxConsumerCfgMap() {
        return maxConsumerCfgMap;
    }

    /**
     * 设置 maxConsumerCfgMap
     *
     * @param maxConsumerCfgMap the maxConsumerCfgMap to set
     */
    public void setMaxConsumerCfgMap(Map<String, Integer> maxConsumerCfgMap) {
        this.maxConsumerCfgMap = maxConsumerCfgMap;
    }

    private class JMSReportListener implements ExceptionListener {

        @Override
        public void onException(JMSException e) {
            logger.error("mq连接失败,尝试重连..." + e.getMessage(), e);
        }
    }
}
