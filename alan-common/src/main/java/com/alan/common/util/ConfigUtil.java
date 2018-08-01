package com.alan.common.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * @author weiBin
 */
public final class ConfigUtil {
    private static final Logger logger = LoggerFactory.getLogger(ConfigUtil.class);
    private static Properties configProp = null;

    /**
     * 初始化application.properties
     */
    static {
        InputStream is = null;
        try {
            configProp = new Properties();
            // 参数为空
            File directory = new File("");
            String courseFile = directory.getCanonicalPath();
            is = new FileInputStream(new File(courseFile + File.separator + "conf" + File.separator + "application.properties"));
            configProp.load(is);
        } catch (Exception e) {
            logger.error("加载application.properties文件异常", e);
        } finally {
            if (is != null) {
                try {
                    is.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * 获取config.properties对象
     */
    public static Properties getConfigProperties() {
        return configProp;
    }

    /**
     * 根据key查找value
     *
     * @param key
     * @return
     */
    public static String getValue(String key) {
        return configProp.getProperty(key);
    }

}
