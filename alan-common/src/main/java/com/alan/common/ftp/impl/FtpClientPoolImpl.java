package com.alan.common.ftp.impl;

import java.io.IOException;
import java.util.Comparator;
import java.util.concurrent.PriorityBlockingQueue;
import java.util.concurrent.atomic.AtomicInteger;

import com.alan.common.ftp.FtpClientPool;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPReply;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * @author yaoQingCan
 * @Description: FtpClient连接池实现类
 * @date 2018年1月10日 上午10:57:01
 */
public class FtpClientPoolImpl implements FtpClientPool {

    private static final Logger LOGGER = LoggerFactory.getLogger(FtpClientPoolImpl.class);

    private static final String ON = "ON";

    private static final String OPTS_UTF8 = "OPTS UTF8";

    /**
     * 记录已生成的资源数量，生成的资源数量不能超过
     */
    private volatile AtomicInteger current = new AtomicInteger();

    /**
     * 资源阻塞队列
     */
    private PriorityBlockingQueue<FtpClientStamp> queue = null;

    /**
     * ftp client pool 基本信息
     */
    private FtpClientPoolBaseInfo ftpPoolBaseInfo;

    /**
     * 本地字符编码
     */
    private static String LOCAL_CHARSET = "GBK";

    /**
     * FTP协议里面，规定文件名编码为iso-8859-1,Ftp.java类 FTP.DEFAULT_CONTROL_ENCODING
     */
    //private static String SERVER_CHARSET = "ISO-8859-1";


    /**
     * 获取FTPClient对象， 如果queue的长度为0，并且已生成的总数量
     */
    @Override
    public FTPClient obtain() throws IOException {
        FTPClient client = null;
        if (queue.size() == 0 && current.get() + queue.size() < ftpPoolBaseInfo.getPoolsize()) {
            client = this.getFtpClient();
            current.incrementAndGet();
            LOGGER.debug("obtain -- current {} ftp client in using, {} ftp client in queue", current.get(), queue.size());
            return client;
        }

        try {
            LOGGER.debug("to obtain ftp client from queue");
            FtpClientStamp clientstamp = queue.take();
            client = clientstamp.getClient();
            LOGGER.debug("obtain ftpclientstamp {} ,ftpclient {} , stamp {}", clientstamp, client, clientstamp.getStamp());
            if (client.isConnected()) {
                try {
                    //boolean b = client.sendNoOp();
                    client.sendNoOp();
                    LOGGER.debug("sendNoOp() ReplyCode {}", client.getReplyCode());
                    if (FTPReply.isPositiveCompletion(client.getReplyCode())) {

                        current.incrementAndGet();
                        LOGGER.debug("obtain -- current {} ftp client in using, {} ftp client in queue", current.get(), queue.size());
                        return client;
                    } else {
                        client.disconnect();
                    }
                } catch (IOException e) {
                    LOGGER.warn("sendNoOp exception happened, the ftpclient may be can't use any more, new ftpclient will be created");
                    LOGGER.warn("ftp client sendNoOp exception message: ", e);
                }
            }

            client = this.getFtpClient();
            current.incrementAndGet();
            LOGGER.debug("obtain --- current {} ftp client in using, {} ftp client in queue", current.get(), queue.size());
            return client;
        } catch (Exception e) {
            LOGGER.debug("obtain ftp client blocking is interrupted, ftp client with null value will be return", e);
            throw new IOException("obtain ftp client blocking interrupted", e);
        }
    }

    @Override
    public void revert(FTPClient client) {
        if (client != null) {

            current.decrementAndGet();
            LOGGER.debug("revert --- current {} ftp client in using, {} ftp client in queue", current.get(), queue.size());

            if (queue.size() >= ftpPoolBaseInfo.getThreshold()) {
                if (client.isConnected()) {
                    try {
                        LOGGER.debug("queue size gt threshold, the client will disconnect");
                        client.disconnect();
                    } catch (IOException e) {
                        LOGGER.warn("when revert ftpclient, client disconnect exception happened", e);
                    }
                }
                this.queue.put(new FtpClientStamp(client, 0));
            } else {

                try {
                    LOGGER.debug("revert ftpclient, current path {}", client.printWorkingDirectory());
                } catch (IOException e) {
                    LOGGER.debug("revert ftpclient, exception happened", e);
                }

                try {
                    boolean b = client.changeWorkingDirectory("/");
                    if (b == false) {
                        LOGGER.warn("ftpclient change to root dir failed, replay code {}", client.getReplyCode());
                        client.disconnect();
                    }
                } catch (IOException e) {
                    LOGGER.warn("ftpclient change to root dir failed, exception happened:", e);
                }
                this.queue.put(new FtpClientStamp(client));
            }
        }
        LOGGER.debug("after revert  --- current {} ftp client in using, {} ftp client in queue", current.get(), queue.size());
    }

    /**
     * 初始化资源池
     */
    @Override
    public void init() throws IOException {
        LOGGER.info("ftpclient pool inintial...");

        // 根据时间排序，时间值越大的，排在队列最前面。
        Comparator<FtpClientStamp> orderIsTime = new Comparator<FtpClientStamp>() {
            @Override
            public int compare(FtpClientStamp o1, FtpClientStamp o2) {
                return o1.getStamp() > o2.getStamp() ? -1 : 1;
            }
        };
        queue = new PriorityBlockingQueue<FtpClientStamp>(11, orderIsTime);

        FTPClient client = null;
        client = this.obtain();
        LOGGER.info("ftp client pool initailed success");
        this.revert(client);
    }

    /**
     * 生成新的FTPClient对象，完成连接服务器，登录，设置参数。
     *
     * @return
     * @throws IOException
     */
    private FTPClient getFtpClient() throws IOException {
        FTPClient f = new FTPClient();
        // setDefaultTimeout  The timeout in milliseconds to use for the socket connection.
        f.setDefaultTimeout(ftpPoolBaseInfo.getDefaultTimeout());
        //Sets the timeout in milliseconds to use when reading from the data connection.
        f.setDataTimeout(ftpPoolBaseInfo.getDataTimeout());
        // The connection timeout to use (in ms)
        f.setConnectTimeout(ftpPoolBaseInfo.getConnectTimeout());

        boolean b = false;
        int rcode;

        try {
            f.connect(ftpPoolBaseInfo.getHostname(), ftpPoolBaseInfo.getPort());
        } catch (IOException e) {
            LOGGER.error("ftpclient can not connect to host {} port {}", ftpPoolBaseInfo.getHostname(), ftpPoolBaseInfo.getPort());
            throw new IOException("ftpclient can not connect to host " + ftpPoolBaseInfo.getHostname() + " port " + ftpPoolBaseInfo.getPort(), e);
        }

        b = f.login(ftpPoolBaseInfo.getUsername(), ftpPoolBaseInfo.getPassword());
        f.setBufferSize(ftpPoolBaseInfo.getBufferSize());
        if (b == false) {
            LOGGER.error("ftp login on server {} failed with name {}, pwd {}", ftpPoolBaseInfo.getHostname(), ftpPoolBaseInfo.getUsername(), ftpPoolBaseInfo.getPassword());
            throw new IOException("ftp login failed with name " + ftpPoolBaseInfo.getUsername() + ", pwd  " + ftpPoolBaseInfo.getPassword());
        }
        rcode = f.getReplyCode();
        if (FTPReply.isPositiveCompletion(rcode)) {
            LOGGER.debug("new ftp client login success on ftp server {}, ftp client object{}", ftpPoolBaseInfo.getHostname(), f);

            f.setFileType(ftpPoolBaseInfo.getFileType());
            f.setFileTransferMode(ftpPoolBaseInfo.getFileTransferMode());

            // 开启服务器对UTF-8的支持，如果服务器支持就用UTF-8编码,解决中文文件名称乱码问题
            if (FTPReply.isPositiveCompletion(f.sendCommand(OPTS_UTF8, ON))) {
                LOCAL_CHARSET = "UTF-8";
                f.setControlEncoding(LOCAL_CHARSET);
                LOGGER.info("ftp client ControlEncoding is :" + LOCAL_CHARSET);
            }

            //主动模式
            if (ftpPoolBaseInfo.getFtpLocalMode().equalsIgnoreCase(FtpLocalMode.PORT) ||
                    ftpPoolBaseInfo.getFtpLocalMode().equalsIgnoreCase(FtpLocalMode.ACTIVE)) {
                f.enterLocalActiveMode();
            }
            //被动模式
            if (ftpPoolBaseInfo.getFtpLocalMode().equalsIgnoreCase(FtpLocalMode.PASV) ||
                    ftpPoolBaseInfo.getFtpLocalMode().equalsIgnoreCase(FtpLocalMode.PASSIVE)) {
                f.enterLocalPassiveMode();
            }

//			if (this.remotePassiveMode) {
//				f.enterRemotePassiveMode();
//			}

            return f;
        } else {
            LOGGER.error("new ftp client login ReplayCode {}", rcode);
            f.disconnect();
            throw new IOException("ftp login success, but the replycode " + rcode);
        }
    }

    /**
     * 内部类， 用于在FTPClient资源上增加个时间标记，在优先阻塞队列中使用，时间标记值越大的，放在队列最前面。
     *
     * @author hpcmsz
     */
    private class FtpClientStamp {

        FtpClientStamp(FTPClient client) {
            this.client = client;
            this.stamp = System.currentTimeMillis();
        }

        FtpClientStamp(FTPClient client, int timestamp) {
            this.client = client;
            this.stamp = timestamp;
        }

        private FTPClient client;
        private long stamp;

        public FTPClient getClient() {
            return client;
        }

        public long getStamp() {
            return stamp;
        }

        public void setStamp(long stamp) {
            this.stamp = stamp;
        }
    }


    public FtpClientPoolBaseInfo getFtpPoolBaseInfo() {
        return ftpPoolBaseInfo;
    }


    public void setFtpPoolBaseInfo(FtpClientPoolBaseInfo ftpPoolBaseInfo) {
        this.ftpPoolBaseInfo = ftpPoolBaseInfo;
    }
}
