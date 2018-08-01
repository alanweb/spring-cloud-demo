package com.cmsz.collection.servlet;

import com.cmsz.collection.bean.BossMessage;
import com.cmsz.collection.cache.MessageCache;
import com.cmsz.collection.constant.Common;
import com.cmsz.collection.util.DateUtil;
import com.cmsz.collection.util.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Lazy;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.handler.annotation.Headers;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Kafka 消费 获取日志消息
 * earliest 有提交的offset时，从提交的offset开始消费；无提交的offset时，从头开始消费
 *
 * @author weibin
 * @date 2018年6月1日14:59:56
 */
@Component
@Lazy
public class KafkaReciever {

    private static final Logger logger = LoggerFactory.getLogger(KafkaReciever.class);

    @PostConstruct
    private void init() {
        logger.info("KafkaReciever init is ok!");
    }

    /**
     * 存储key:Session-Id, value:Trade-Session
     */
    private static Map<String, String> sessionIDReqIDs = new ConcurrentHashMap<String, String>();

    /**
     * 存储key:Session-Id, value:Result-Code
     */
    private static Map<String, String> sessionIDResultCodes = new ConcurrentHashMap<String, String>();

    @KafkaListener(topics = "${topic.name}")
    public void takeMessage(@Payload String message, @Headers MessageHeaders headers) {
//    public void takeMessage(String message) {
        logger.info("boosMessage size {}", MessageCache.bossMessageMap.size());
        if (StringUtils.isBlank(message)) {
            return;
        }
        try {
            //过滤非指定日期的日志 前一天之前的
            String dateStr = message.substring(0, 10);
            String beforeDate = DateUtil.getDateyyyyMMdd(1);
            if (beforeDate.compareTo(dateStr) > 0) {
                return;
            }
            // 去除报文开始的打印时间+描述信息
            message = StringUtils.deleteChar(message, "#<", 1);
            String reqTransID = StringUtils.findFieldValue(message, "<ReqTransID>", "</ReqTransID>");
            //如果没有reqTransID 解析的是dcc报文 否则 解析的是font 报文
            if (StringUtils.isBlank(reqTransID)) {
                analysisDccBoss(message);
            } else {
                analysisFontBoss(message, reqTransID);
                //区分这两天的message报文消息
                Set<String> set = MessageCache.dateTradeSessionMap.get(dateStr);
                if (set == null) {
                    set = new HashSet<>();
                }
                set.add(reqTransID);
                MessageCache.dateTradeSessionMap.put(dateStr, set);
            }
        } catch (Exception e) {
            logger.error("队列处理出错：" + e.getMessage());
        }
    }

    private void analysisDccBoss(String message) {
        // 计费或退费省发送报文 DCC报文
        if (message != null && message.contains("avp") && message.contains(Common.TRADE_SESSION)) {
            //获取Session-Id以及Result-Code的值
            String sessionId = getValueFromAvp(message, Common.SESSION_ID);
            String reqTransId = getValueFromAvp(message, Common.TRADE_SESSION);
            sessionIDReqIDs.put(sessionId, reqTransId);
        }
        //计费或退费省返回结果
        else if (message != null && message.contains("avp") && message.contains(Common.RESULT_CODE)) {
            //获取Session-Id以及Result-Code的值
            String sessionId = getValueFromAvp(message, Common.SESSION_ID);
            String resultCode = getValueFromAvp(message, Common.RESULT_CODE);
            sessionIDResultCodes.put(sessionId, resultCode);
        }
    }

    private void analysisFontBoss(String message, String reqTransID) {
        //动作编码
        String actionCode = StringUtils.findFieldValue(message, "<ActionCode>", "</ActionCode>");
        BossMessage bossMessage = MessageCache.bossMessageMap.get(reqTransID);
        if (null == bossMessage) {
            bossMessage = new BossMessage();
        }
        bossMessage.setReqTransId(reqTransID);
        if (StringUtils.isBlank(bossMessage.getActivityCode())) {
            String activityCode = StringUtils.findFieldValue(message, "<ActivityCode>", "</ActivityCode>");
            bossMessage.setActivityCode(activityCode);
        }
        if (StringUtils.isBlank(bossMessage.getActionCode()) || "0".equals(bossMessage.getActionCode())) {
            bossMessage.setActionCode(actionCode);
        }
        if (StringUtils.isBlank(bossMessage.getReqDateTime())) {
            String reqDateTime = StringUtils.findFieldValue(message, "<ReqDateTime>", "</ReqDateTime>");
            bossMessage.setReqDateTime(reqDateTime);
        }
        if (StringUtils.isBlank(bossMessage.getReqDate())) {
            String reqDate = StringUtils.findFieldValue(message, "<ReqDate>", "</ReqDate>");
            bossMessage.setReqDate(reqDate);
        }
        if (StringUtils.isBlank(bossMessage.getRcvSys())) {
            String rcvSys = StringUtils.findFieldValue(message, "<RcvSys>", "</RcvSys>");
            bossMessage.setRcvSys(rcvSys);
        }
        if (StringUtils.isBlank(bossMessage.getReqSys())) {
            String reqSys = StringUtils.findFieldValue(message, "<ReqSys>", "</ReqSys>");
            bossMessage.setReqSys(reqSys);
        }
        if (StringUtils.isBlank(bossMessage.getMerId())) {
            String merId = StringUtils.findFieldValue(message, "<MerID>", "</MerID>");
            bossMessage.setMerId(merId);
        }
        if (StringUtils.isBlank(bossMessage.getVasType())) {
            String vasType = StringUtils.findFieldValue(message, "<VasType>", "</VasType>");
            bossMessage.setVasType(vasType);
        }
        if (StringUtils.isBlank(bossMessage.getPayType())) {
            String payType = StringUtils.findFieldValue(message, "<PayType>", "</PayType>");
            bossMessage.setPayType(payType);
        }
        if (StringUtils.isBlank(bossMessage.getAmount())) {
            String amount = StringUtils.findFieldValue(message, "<Amount>", "</Amount>");
            bossMessage.setAmount(amount);
        }
        if (StringUtils.isBlank(bossMessage.getHomeProv())) {
            String homeProv = StringUtils.findFieldValue(message, "<HomeProv>", "</HomeProv>");
            bossMessage.setHomeProv(homeProv);
        }
        if (StringUtils.isBlank(bossMessage.getChargeNumber())) {
            String chargeNumber = StringUtils.findFieldValue(message, "<ChargeNumber>", "</ChargeNumber>");
            bossMessage.setChargeNumber(chargeNumber);
        }
        if (StringUtils.isBlank(bossMessage.getTradeSeqno())) {
            String tradeSeqno = StringUtils.findFieldValue(message, "<TradeSeqno>", "</TradeSeqno>");
            bossMessage.setTradeSeqno(tradeSeqno);
        }
        if (StringUtils.isBlank(bossMessage.getTradeSession())) {
            String tradeSession = StringUtils.findFieldValue(message, "<TradeSession>", "</TradeSession>");
            bossMessage.setTradeSession(tradeSession);
        }

        if (StringUtils.isBlank(bossMessage.getOldTradeSession())) {
            String oldTradeSession = StringUtils.findFieldValue(message, "<OldTradeSession>", "</OldTradeSession>");
            bossMessage.setOldTradeSession(oldTradeSession);
        }
        if (StringUtils.isBlank(bossMessage.getOldSettleDate())) {
            String oldSettleDate = StringUtils.findFieldValue(message, "<OldSettleDate>", "</OldSettleDate>");
            bossMessage.setOldSettleDate(oldSettleDate);
        }
        if (StringUtils.isBlank(bossMessage.getOldTradeTime())) {
            String oldTradeTime = StringUtils.findFieldValue(message, "<OldTradeTime>", "</OldTradeTime>");
            bossMessage.setOldTradeTime(oldTradeTime);
        }

        if (StringUtils.isBlank(bossMessage.getSettleDate())) {
            String settleDate = StringUtils.findFieldValue(message, "<SettleDate>", "</SettleDate>");
            bossMessage.setSettleDate(settleDate);
        }
        if (StringUtils.isBlank(bossMessage.getTradeTime())) {
            String tradeTime = StringUtils.findFieldValue(message, "<TradeTime>", "</TradeTime>");
            bossMessage.setTradeTime(tradeTime);
        }
        if (StringUtils.isBlank(bossMessage.getSpCode())) {
            String spCode = StringUtils.findFieldValue(message, "<SPCode>", "</SPCode>");
            bossMessage.setSpCode(spCode);
        }
        if (StringUtils.isBlank(bossMessage.getServiceCode())) {
            String serviceCode = StringUtils.findFieldValue(message, "<ServiceCode>", "</ServiceCode>");
            bossMessage.setServiceCode(serviceCode);
        }
        if (StringUtils.isBlank(bossMessage.getBalProp())) {
            String balProp = StringUtils.findFieldValue(message, "<BalProp>", "</BalProp>");
            bossMessage.setBalProp(balProp);
        }
        if (StringUtils.isBlank(bossMessage.getBillFlag())) {
            String billFlag = StringUtils.findFieldValue(message, "<BillFlag>", "</BillFlag>");
            bossMessage.setBillFlag(billFlag);
        }
        if (StringUtils.isBlank(bossMessage.getContentId())) {
            String contentId = StringUtils.findFieldValue(message, "<ContentID>", "</ContentID>");
            bossMessage.setContentId(contentId);
        }
        if (StringUtils.isBlank(bossMessage.getMemberType())) {
            String memberType = StringUtils.findFieldValue(message, "<MemberType>", "</MemberType>");
            bossMessage.setMemberType(memberType);
        }
        if (StringUtils.isBlank(bossMessage.getDepositType())) {
            String depositType = StringUtils.findFieldValue(message, "<DepositType>", "</DepositType>");
            bossMessage.setDepositType(depositType);
        }

        if (StringUtils.isBlank(bossMessage.getPointCost())) {
            String pointCost = StringUtils.findFieldValue(message, "<PointCost>", "</PointCost>");
            bossMessage.setPointCost(pointCost);
        }
        if (StringUtils.isBlank(bossMessage.getVisitProv())) {
            String visitProv = StringUtils.findFieldValue(message, "<VisitProv>", "</VisitProv>");
            bossMessage.setVisitProv(visitProv);
        }

        if (StringUtils.isBlank(bossMessage.getApplyTime())) {
            String applyTime = StringUtils.findFieldValue(message, "<ApplyTime>", "</ApplyTime>");
            bossMessage.setApplyTime(applyTime);
        }
        if (StringUtils.isBlank(bossMessage.getFinishTime())) {
            String finishTime = StringUtils.findFieldValue(message, "<FinishTime>", "</FinishTime>");
            bossMessage.setFinishTime(finishTime);
        }
        if (StringUtils.isBlank(bossMessage.getReadContentType())) {
            String readContentType = StringUtils.findFieldValue(message, "<ReadContentType>", "</ReadContentType>");
            bossMessage.setReadContentType(readContentType);
        }
        if (StringUtils.isBlank(bossMessage.getMsisdnRec())) {
            String msisdnRec = StringUtils.findFieldValue(message, "<MsisdnRec>", "</MsisdnRec>");
            bossMessage.setMsisdnRec(msisdnRec);
        }
        if (StringUtils.isBlank(bossMessage.getDuration())) {
            String duration = StringUtils.findFieldValue(message, "<Duration>", "</Duration>");
            bossMessage.setDuration(duration);
        }
        if (StringUtils.isBlank(bossMessage.getByteSize())) {
            String byteSize = StringUtils.findFieldValue(message, "<ByteSize>", "</ByteSize>");
            bossMessage.setByteSize(byteSize);
        }
        if (StringUtils.isBlank(bossMessage.getCpCode())) {
            String cpCode = StringUtils.findFieldValue(message, "<CPCode>", "</CPCode>");
            bossMessage.setCpCode(cpCode);
        }
        if (StringUtils.isBlank(bossMessage.getUserType())) {
            String userType = StringUtils.findFieldValue(message, "<UserType>", "</UserType>");
            bossMessage.setUserType(userType);
        }

        if (StringUtils.isBlank(bossMessage.getResultCode())) {
            String resultCode = StringUtils.findFieldValue(message, "<ResultCode>", "</ResultCode>");
            bossMessage.setResultCode(resultCode);
        }
        if (StringUtils.isBlank(bossMessage.getResultDesc())) {
            String resultDesc = StringUtils.findFieldValue(message, "<ResultDesc>", "</ResultDesc>");
            bossMessage.setResultDesc(resultDesc);
        }
        if (StringUtils.isBlank(bossMessage.getNotifyType())) {
            String notifyType = StringUtils.findFieldValue(message, "<NotifyType>", "</NotifyType>");
            bossMessage.setNotifyType(notifyType);
        }
        if ("1".equals(actionCode)) {
            if (StringUtils.isBlank(bossMessage.getRcvDate())) {
                String rcvDate = StringUtils.findFieldValue(message, "<RcvDate>", "</RcvDate>");
                bossMessage.setRcvDate(rcvDate);
            }
            if (StringUtils.isBlank(bossMessage.getRcvDateTime())) {
                String rcvDateTime = StringUtils.findFieldValue(message, "<RcvDateTime>", "</RcvDateTime>");
                bossMessage.setRcvDateTime(rcvDateTime);
            }
            if (StringUtils.isBlank(bossMessage.getRcvTransId())) {
                String rcvTransId = StringUtils.findFieldValue(message, "<RcvTransID>", "</RcvTransID>");
                bossMessage.setRcvTransId(rcvTransId);
            }
        }
        MessageCache.bossMessageMap.put(reqTransID, bossMessage);
    }

    static {
        new Thread(new DealMap()).start();
    }

    /**
     * 将省dcc发送报文和仿真返回给dcc的报文
     *
     * @author psk
     * @date 2017年5月19日
     */
    static class DealMap implements Runnable {
        @Override
        public void run() {
            while (true) {
                for (String sessionId : sessionIDReqIDs.keySet()) {
                    BossMessage bossMessage = MessageCache.bossMessageMap.get(sessionIDReqIDs.get(sessionId));
                    if (null == bossMessage) {
                        continue;
                    }
                    if (sessionIDResultCodes.containsKey(sessionId)) {
                        String reqID = sessionIDReqIDs.get(sessionId);
                        bossMessage.setSessionId(sessionId);
                        bossMessage.setCrmResultCode(sessionIDResultCodes.get(sessionId));
                        MessageCache.bossMessageMap.put(reqID, bossMessage);
                        sessionIDReqIDs.remove(sessionId);
                        sessionIDResultCodes.remove(sessionId);
                    }
                }
            }
        }

    }

    /**
     * 从avp中获得相应的值
     *
     * @param message 报文
     * @param name    要取到的字段名	如：Result-Code，Session-Id
     * @return
     * @author psk
     * @date 2017年4月24日
     */
    public static String getValueFromAvp(String message, String name) {
        String[] strs = message.split("<avp");
        for (String str : strs) {
            if (str.contains(name)) {
                // 截取value="" 中的值
                int index = str.indexOf("value=\"") + 7;
                str = str.substring(index);
                index = str.indexOf("\"");
                str = str.substring(0, index);
                return str;
            }
        }
        return "";
    }

}
