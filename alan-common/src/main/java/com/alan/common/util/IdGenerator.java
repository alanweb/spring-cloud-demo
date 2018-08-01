package com.alan.common.util;

import java.util.Random;
import java.util.UUID;

/**
 * id生成的工具类
 *
 * @author ldl
 */

/**
 * @author lidelin
 *
 */
public final class IdGenerator {
    private IdGenerator() {
    }

    /**
     * 生成一个uuid
     * @return uuid
     */
    public static String getUUID() {
        return UUID.randomUUID().toString().replaceAll("-", "");

    }

    /**
     * 图片名生成
     */
    public static String genImageName() {
        //取当前时间的长整形值包含毫秒
        long millis = System.currentTimeMillis();
        //long millis = System.nanoTime();
        //加上三位随机数
        Random random = new Random();
        int end3 = random.nextInt(999);
        //如果不足三位前面补0
        String str = millis + String.format("%03d", end3);

        return str;
    }
}
