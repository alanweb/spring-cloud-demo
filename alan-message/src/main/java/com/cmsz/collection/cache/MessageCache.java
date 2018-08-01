package com.cmsz.collection.cache;

import com.cmsz.collection.bean.BossMessage;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author weibin
 * @date 2018/6/1
 */
public class MessageCache {
    /**
     * 存储key:Trade-Session, value:BossMessage
     */
    public static Map<String, BossMessage> bossMessageMap = new ConcurrentHashMap<>();

    /**
     * 存储key:日期, value: Trade-Sessions
     */
    public static Map<String, Set<String>> dateTradeSessionMap = new ConcurrentHashMap<>();

}
