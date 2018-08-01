package com.alan.activemq.code;

import org.apache.activemq.ActiveMQConnectionFactory;

import javax.jms.*;

/**
 * @author alan
 * @date 2018/6/21
 */
public class ConsumerTool implements MessageListener {
    /**
     * 用户名
     */
    private String user = ActiveMQConnectionFactory.DEFAULT_USER;
    /**
     * 密码
     */
    private String password = ActiveMQConnectionFactory.DEFAULT_PASSWORD;
    /**
     * 监听服务器地址 默认是 tcp://localhost:61616
     */
    private String brokerUrl = ActiveMQConnectionFactory.DEFAULT_BROKER_URL;

    /**
     * 消息目的地名称
     */
    private String subject = "TOOL.DEFAULT";
    /**
     * 在点对点（PTP）消息传递域中，目的地被成为队列（queue）
     */
    private Destination destination = null;

    /**
     * 是否是订阅
     */
    private boolean isTopic = false;
    /**
     * 在发布/订阅（PUB/SUB）消息传递域中，目的地被成为主题（topic）。
     */
    private Topic topic = null;

    /**
     * 初始化 一个JMS客户端到JMS Provider的连接
     */
    private Connection connection = null;

    /**
     * 初始化  一个发送消息的进程
     */
    private Session session = null;
    /**
     * 初始化 消息消费者 (它是由session 创建的)
     */
    private MessageConsumer consumer = null;

    /**
     * 初始化
     */
    private void initialize() throws JMSException {
        ActiveMQConnectionFactory connectionFactory = new ActiveMQConnectionFactory(user, password, brokerUrl);
        connection = connectionFactory.createConnection();
        //false 参数表示 为非事务型消息，后面的参数表示消息的确认类型
        session = connection.createSession(Boolean.FALSE, Session.AUTO_ACKNOWLEDGE);
        if (!isTopic) {
            //PTP 队列
            destination = session.createQueue(subject);
            consumer = session.createConsumer(destination);
        } else {
            //主题订阅
            topic = session.createTopic(subject);
            consumer = session.createConsumer(topic);
        }
    }

    /**
     * 消息的异步接收
     *
     * @throws JMSException
     */
    public void consumeMessage() throws JMSException {
        initialize();
        connection.start();
        System.out.println("Consumer:->Begin listening...");
        // 监听 实现MessageListener接口，每当消息到达时，ActiveMQ会调用MessageListener中的onMessage函数。
        consumer.setMessageListener(this);

    }

    /**
     * 消息的同步
     */
    public void receiveMessage() throws JMSException {
        initialize();
        connection.start();
        Message message = consumer.receive();
        if (null != message) {
            if (message instanceof TextMessage) {
                System.out.println("Consumer:->Received: " + ((TextMessage) message).getText());
            } else {
                System.out.println("Consumer:->Received: " + message);
            }
        }
    }

    /**
     * 关闭connection
     *
     * @throws JMSException
     */
    public void close() throws JMSException {
        if (consumer != null) {
            consumer.close();
        }
        if (session != null) {
            session.close();
        }
        if (connection != null) {
            connection.close();
        }
    }

    @Override
    public void onMessage(Message message) {
        try {
            if (message instanceof TextMessage) {
                TextMessage txtMsg = (TextMessage) message;
                String msg = txtMsg.getText();
                System.out.println("Consumer:->Received: " + msg);
            } else {
                System.out.println("Consumer:->Received: " + message);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getBrokerUrl() {
        return brokerUrl;
    }

    public void setBrokerUrl(String brokerUrl) {
        this.brokerUrl = brokerUrl;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public boolean isTopic() {
        return isTopic;
    }

    public void setTopic(boolean topic) {
        isTopic = topic;
    }
}
