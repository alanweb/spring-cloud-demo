package com.alan.common.util;

import java.io.IOException;
import java.net.Authenticator;
import java.net.PasswordAuthentication;
import java.net.URL;

public class ProxyUtil {
    /**
     * 对代理进行初始设置
     *
     * @param host     代理服务器的IP
     * @param port     代理服务器的端口
     * @param username 连接代理服务器的用户名
     * @param password 连接代理服务器的密码
     */
    public static void initProxy(String host, int port, final String username,

                                 final String password) {
        //设置一个默认的验证器
        Authenticator.setDefault(new Authenticator() {

            @Override
            protected PasswordAuthentication getPasswordAuthentication() {

                return new PasswordAuthentication(username,

                        new String(password).toCharArray());

            }

        });
        //设置对HTTP进行代理,key可以写成http.proxyXxxx或proxyXxxx两种形式
        System.setProperty("http.proxyType", "4");

        System.setProperty("http.proxyPort", Integer.toString(port));

        System.setProperty("http.proxyHost", host);

        System.setProperty("http.proxySet", "true");

        //设置对FTP进行代理
        System.setProperty("ftpProxyPort", Integer.toString(port));

        System.setProperty("ftpProxyHost", host);

        System.setProperty("ftpProxySet", "true");

    }

    /**
     * 移动代理
     */
    public static void ocProxy() {
        //代理服务器IP
        String proxy = "192.168.105.71";
        //代理服务器端口
        int port = 80;
        //连接代理服务器的用户名
        String username = "";
        //连接代理服务器的密码
        String password = "";
        initProxy(proxy, port, username, password);
    }
}