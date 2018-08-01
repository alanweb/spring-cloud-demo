package com.alan.cloud;


import org.jasypt.encryption.StringEncryptor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * jasypt加解密,自定义加解密算法
 *
 * @author yqc
 * @Description: jasypt加解密, 自定义加解密算法
 * @date 2018年1月22日 上午9:56:33
 */
public class CmupEncryptor implements StringEncryptor {
    Logger systemLogHandler = LoggerFactory.getLogger(getClass());
    /**
     * 加解密的摘要信息，是相当于加密算法的盐或称为密钥,在jasypt中称之为密码password
     * 不是需要加密的数据库或其他账号密码
     */
    private String securityKey;

    /**
     * 是否需要加解密
     */
    private boolean isEncry;

    /**
     * 加密
     */
    @Override
    public String encrypt(final String message) {

        systemLogHandler.debug("是否需要加密:" + isEncry);
        systemLogHandler.debug("加密密钥securityKey:" + securityKey);

        // 加密开关未打开，则直接返回，不用处理
        if (Boolean.valueOf(isEncry) == null || !Boolean.TRUE.equals(isEncry)) {
            systemLogHandler.debug("不需要加密" + message + "，直接返回原始信息");
            return message;
        }
        final String encryptMessage = Des.encrypt(message, Des.hex2byte(securityKey));
        systemLogHandler.debug("成功加密" + message + "，返回加密后的信息");
        return encryptMessage;
    }

    /**
     * 解密
     */
    @Override
    public String decrypt(final String encryptedMessage) {

        systemLogHandler.debug("是否需要解密:" + isEncry);
        systemLogHandler.debug("解密密钥securityKey:" + securityKey);


        // 加密开关未打开，则直接返回，不用处理
        if (Boolean.valueOf(isEncry) == null || !Boolean.TRUE.equals(isEncry)) {
            systemLogHandler.debug("不需要解密" + encryptedMessage + "，直接返回原始信息");
            return encryptedMessage;
        }
        final String decryptMessage = Des.decrypt(encryptedMessage, Des.hex2byte(securityKey));
        systemLogHandler.debug("成功解密" + encryptedMessage + "，返回加密后的信息");
        return decryptMessage;
    }

    public String getSecurityKey() {
        return securityKey;
    }

    public void setSecurityKey(String securityKey) {
        this.securityKey = securityKey;
    }

    public boolean isEncry() {
        return isEncry;
    }

    public void setEncry(boolean isEncry) {
        this.isEncry = isEncry;
    }


}
