package com.alan.common.ftp.impl;

import org.apache.commons.net.ftp.FTP;

/**
 * 
 * ftp client pool 基本信息
 * @Description: ftp client pool 基本信息
 * @author yaoQingCan
 * @date 2018年1月22日 下午5:09:57
 */
public class FtpClientPoolBaseInfo {
	
	/**
	 * ftp client 资源池的大小，即最大能启动的ftp连接数，如果已达到最大值，则会阻塞，等待其他线程释放资源
	 */
	private int poolsize = 20;

	/**
	 * 资源池空闲ftp连接阈值，当资源池中的空闲连接大于此值时，资源池再回收ftp连接时，将会断开连接。
	 */
	private int threshold = 2;

	/**
	 * ftp服务器主机名，ip地址
	 */
	private String hostname;
	
	/**
	 * ftp 端口号
	 */
	private int port;
	
	/**
	 * ftp 用户名
	 */
	private String username;
	/**
	 * ftp 用户密码
	 */
	private String password;


	/**
	 * ftp 文件模式
	 */
	private int fileType = FTP.BINARY_FILE_TYPE;
	/**
	 * ftp 文件传输模式
	 */
	private int fileTransferMode = FTP.STREAM_TRANSFER_MODE;
	
	/**
	 * 主被动模式设置,默认主动模式
	 * 可以配置的值为：
	 * 主动模块：PORT 或 active 忽略大小写
	 * 被动模块：PASV 或 passive 忽略大小写
	 */
	private String ftpLocalMode="PORT";
	
	/**
	 * 传输缓存大小，单位字节（byte）
	 * Set the internal buffer size for buffered data streams.
	 * 
	 */
	private int bufferSize=10240;
	
	/**
	 * The timeout in milliseconds to use for the socket connection
	 */
	private int defaultTimeout=60000;
	
	/**
	 * Sets the timeout in milliseconds to use when reading from the data connection.
	 */
	private int dataTimeout=60000;
	
	/**
	 * The connection timeout to use (in ms)
	 */
	private int connectTimeout=60000;

	public int getBufferSize() {
		return bufferSize;
	}

	public void setBufferSize(int bufferSize) {
		this.bufferSize = bufferSize;
	}

	public int getFileTransferMode() {
		return fileTransferMode;
	}

	public void setFileTransferMode(int fileTransferMode) {
		this.fileTransferMode = fileTransferMode;
	}

	public int getFileType() {
		return fileType;
	}

	public void setFileType(int fileType) {
		this.fileType = fileType;
	}


	public int getPoolsize() {
		return poolsize;
	}

	public void setPoolsize(int poolsize) {
		this.poolsize = poolsize;
	}

	public int getThreshold() {
		return threshold;
	}

	public void setThreshold(int threshold) {
		this.threshold = threshold;
	}

	public String getHostname() {
		return hostname;
	}

	public void setHostname(String hostname) {
		this.hostname = hostname;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}

	public String getFtpLocalMode() {
		return ftpLocalMode;
	}

	public void setFtpLocalMode(String ftpLocalMode) {
		this.ftpLocalMode = ftpLocalMode;
	}

	public int getDefaultTimeout() {
		return defaultTimeout;
	}

	public void setDefaultTimeout(int defaultTimeout) {
		this.defaultTimeout = defaultTimeout;
	}

	public int getDataTimeout() {
		return dataTimeout;
	}

	public void setDataTimeout(int dataTimeout) {
		this.dataTimeout = dataTimeout;
	}

	public int getConnectTimeout() {
		return connectTimeout;
	}

	public void setConnectTimeout(int connectTimeout) {
		this.connectTimeout = connectTimeout;
	}
	
	
	

}
