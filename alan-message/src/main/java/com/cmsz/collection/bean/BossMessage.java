package com.cmsz.collection.bean;


import com.cmsz.collection.util.StringUtils;

import java.io.Serializable;

/**
 * 报文消息bean
 *
 * @author weibin
 * @date 2018年6月4日11:32:35
 */
public class BossMessage implements Serializable {

    private static final long serialVersionUID = 3602584290909033127L;
    /**
     * 交易代码
     */
    private String activityCode;
    /**
     * 发起方系统
     */
    private String reqSys;
    /**
     * 发起方交易日期 取值格式为YYYYMMDD。发起方交易日期（按照发起方的对账日，有可能与发起方时间戳中的日期不一致）
     */
    private String reqDate;
    /**
     * 统一支付流水号
     */
    private String upayBizSeq;
    /**
     * 发起方交易流水号
     */
    private String reqTransId;
    /**
     * DCC会话ID
     */
    private String sessionId;
    /**
     * 发起方时间戳
     */
    private String reqDateTime;
    /**
     * 交易动作代码 0：请求，1：应答
     */
    private String actionCode;

    /**
     * 接收方系统
     */
    private String rcvSys;

    /**
     * 接收方交易日期
     */
    private String rcvDate;
    /**
     * 接收方交易流水号
     */
    private String rcvTransId;
    /**
     * 接收方时间戳
     */
    private String rcvDateTime;
    /**
     * 计费平台标识
     */
    private String merId;
    /**
     * 咪咕业务平台类型
     */
    private String vasType;

    /**
     * 交易方式
     */
    private String payType;
    /**
     * 交易金额
     */
    private String amount;
    /**
     * 归属省代码
     */
    private String homeProv;

    /**
     * 交易的用户号码
     */
    private String chargeNumber;
    /**
     * 咪咕平台订单号
     */
    private String tradeSeqno;
    /**
     * 咪咕计费交易流水 同报文头ReqTransID
     */
    private String tradeSession;
    /**
     * 旧交易流水
     */
    private String oldTradeSession;
    /**
     * 对帐以此日期为准
     */
    private String settleDate;
    /**
     * 交易时间
     */
    private String tradeTime;
    /**
     * 旧交易对账日期YYYYMMDD
     */
    private String oldSettleDate;

    /**
     * 旧交易时间YYYYMMDDHHMIS
     */
    private String oldTradeTime;
    /**
     * SP企业代码
     */
    private String spCode;

    /**
     * 业务代码
     */
    private String serviceCode;

    /**
     * 结算比例
     */
    private String balProp;

    /**
     * 扣费类型
     */
    private String billFlag;

    /**
     * 内容编码
     */
    private String contentId;
    /**
     * 点数
     */
    private String pointCost;
    /**
     * 用户拜访地省代码
     */
    private String visitProv;
    /**
     * 操作来源
     */
    private String depositType;
    /**
     * 应用申请时间YYYYMMDDHHMISS
     */
    private String applyTime;

    /**
     * 结束时间YYYYMMDDHHMISS
     */
    private String finishTime;

    /**
     * 内容类型
     */
    private String readContentType;
    /**
     * 接收方MSISDN（赠送号码）
     */
    private String msisdnRec;
    /**
     * 业务时长
     */
    private String duration;
    /**
     * 下载内容的大小，单位为字节。
     */
    private String byteSize;
    /**
     * 0：非会员 1：咪咕普通会员 2：咪咕高级会员 3：咪咕特级会员
     */
    private String memberType;

    /**
     * 提供媒体内容的CP的企业代码
     */
    private String cpCode;
    /**
     * 计费号码用户类型 0：全球通 1：神州行
     */
    private String userType;
    /**
     * 交易应答代码
     */
    private String resultCode;
    /**
     * 交易结果描述
     */
    private String resultDesc;
    /**
     * 省返回码
     */
    private String crmResultCode;

    /**
     * 通知类型：01、计费结果通知，02、退费结果通知
     */
    private String notifyType;


    public static long getSerialVersionUID() {
        return serialVersionUID;
    }

    public String getActivityCode() {
        return activityCode;
    }

    public void setActivityCode(String activityCode) {
        this.activityCode = activityCode;
    }

    public String getReqSys() {
        return reqSys;
    }

    public void setReqSys(String reqSys) {
        this.reqSys = reqSys;
    }

    public String getReqDate() {
        return reqDate;
    }

    public void setReqDate(String reqDate) {
        this.reqDate = reqDate;
    }

    public String getUpayBizSeq() {
        return upayBizSeq;
    }

    public void setUpayBizSeq(String upayBizSeq) {
        this.upayBizSeq = upayBizSeq;
    }

    public String getReqTransId() {
        return reqTransId;
    }

    public void setReqTransId(String reqTransId) {
        this.reqTransId = reqTransId;
    }

    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    public String getReqDateTime() {
        return reqDateTime;
    }

    public void setReqDateTime(String reqDateTime) {
        this.reqDateTime = reqDateTime;
    }

    public String getActionCode() {
        return actionCode;
    }

    public void setActionCode(String actionCode) {
        this.actionCode = actionCode;
    }

    public String getRcvSys() {
        return rcvSys;
    }

    public void setRcvSys(String rcvSys) {
        this.rcvSys = rcvSys;
    }

    public String getRcvDate() {
        return rcvDate;
    }

    public void setRcvDate(String rcvDate) {
        this.rcvDate = rcvDate;
    }

    public String getRcvTransId() {
        return rcvTransId;
    }

    public void setRcvTransId(String rcvTransId) {
        this.rcvTransId = rcvTransId;
    }

    public String getRcvDateTime() {
        return rcvDateTime;
    }

    public void setRcvDateTime(String rcvDateTime) {
        this.rcvDateTime = rcvDateTime;
    }

    public String getMerId() {
        return merId;
    }

    public void setMerId(String merId) {
        this.merId = merId;
    }

    public String getVasType() {
        return vasType;
    }

    public void setVasType(String vasType) {
        this.vasType = vasType;
    }

    public String getPayType() {
        return payType;
    }

    public void setPayType(String payType) {
        this.payType = payType;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public String getHomeProv() {
        return homeProv;
    }

    public void setHomeProv(String homeProv) {
        this.homeProv = homeProv;
    }

    public String getChargeNumber() {
        return chargeNumber;
    }

    public void setChargeNumber(String chargeNumber) {
        this.chargeNumber = chargeNumber;
    }

    public String getTradeSeqno() {
        return tradeSeqno;
    }

    public void setTradeSeqno(String tradeSeqno) {
        this.tradeSeqno = tradeSeqno;
    }

    public String getTradeSession() {
        return tradeSession;
    }

    public void setTradeSession(String tradeSession) {
        this.tradeSession = tradeSession;
    }

    public String getOldTradeSession() {
        return oldTradeSession;
    }

    public void setOldTradeSession(String oldTradeSession) {
        this.oldTradeSession = oldTradeSession;
    }

    public String getSettleDate() {
        return settleDate;
    }

    public void setSettleDate(String settleDate) {
        this.settleDate = settleDate;
    }

    public String getTradeTime() {
        return tradeTime;
    }

    public void setTradeTime(String tradeTime) {
        this.tradeTime = tradeTime;
    }

    public String getOldSettleDate() {
        return oldSettleDate;
    }

    public void setOldSettleDate(String oldSettleDate) {
        this.oldSettleDate = oldSettleDate;
    }

    public String getOldTradeTime() {
        return oldTradeTime;
    }

    public void setOldTradeTime(String oldTradeTime) {
        this.oldTradeTime = oldTradeTime;
    }

    public String getSpCode() {
        return spCode;
    }

    public void setSpCode(String spCode) {
        this.spCode = spCode;
    }

    public String getServiceCode() {
        return serviceCode;
    }

    public void setServiceCode(String serviceCode) {
        this.serviceCode = serviceCode;
    }

    public String getBalProp() {
        return balProp;
    }

    public void setBalProp(String balProp) {
        this.balProp = balProp;
    }

    public String getBillFlag() {
        return billFlag;
    }

    public void setBillFlag(String billFlag) {
        this.billFlag = billFlag;
    }

    public String getContentId() {
        return contentId;
    }

    public void setContentId(String contentId) {
        this.contentId = contentId;
    }

    public String getPointCost() {
        return pointCost;
    }

    public void setPointCost(String pointCost) {
        this.pointCost = pointCost;
    }

    public String getVisitProv() {
        return visitProv;
    }

    public void setVisitProv(String visitProv) {
        this.visitProv = visitProv;
    }

    public String getDepositType() {
        return depositType;
    }

    public void setDepositType(String depositType) {
        this.depositType = depositType;
    }

    public String getApplyTime() {
        return applyTime;
    }

    public void setApplyTime(String applyTime) {
        this.applyTime = applyTime;
    }

    public String getFinishTime() {
        return finishTime;
    }

    public void setFinishTime(String finishTime) {
        this.finishTime = finishTime;
    }

    public String getReadContentType() {
        return readContentType;
    }

    public void setReadContentType(String readContentType) {
        this.readContentType = readContentType;
    }

    public String getMsisdnRec() {
        return msisdnRec;
    }

    public void setMsisdnRec(String msisdnRec) {
        this.msisdnRec = msisdnRec;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public String getByteSize() {
        return byteSize;
    }

    public void setByteSize(String byteSize) {
        this.byteSize = byteSize;
    }

    public String getMemberType() {
        return memberType;
    }

    public void setMemberType(String memberType) {
        this.memberType = memberType;
    }

    public String getCpCode() {
        return cpCode;
    }

    public void setCpCode(String cpCode) {
        this.cpCode = cpCode;
    }

    public String getUserType() {
        return userType;
    }

    public void setUserType(String userType) {
        this.userType = userType;
    }

    public String getResultCode() {
        return resultCode;
    }

    public void setResultCode(String resultCode) {
        this.resultCode = resultCode;
    }

    public String getResultDesc() {
        return resultDesc;
    }

    public void setResultDesc(String resultDesc) {
        this.resultDesc = resultDesc;
    }

    public String getCrmResultCode() {
        return crmResultCode;
    }

    public void setCrmResultCode(String crmResultCode) {
        this.crmResultCode = crmResultCode;
    }

    public String getNotifyType() {
        return notifyType;
    }

    public void setNotifyType(String notifyType) {
        this.notifyType = notifyType;
    }

    public String joinAttribute(String delimiter) {
        StringBuffer sb = new StringBuffer();
        sb.append(StringUtils.isBlank(activityCode) ? "" : activityCode).append(delimiter);
        sb.append(StringUtils.isBlank(reqSys) ? "" : reqSys).append(delimiter);
        sb.append(StringUtils.isBlank(reqDate) ? "" : reqDate).append(delimiter);
        sb.append(StringUtils.isBlank(upayBizSeq) ? "" : upayBizSeq).append(delimiter);
        sb.append(StringUtils.isBlank(reqTransId) ? "" : reqTransId).append(delimiter);
        sb.append(StringUtils.isBlank(sessionId) ? "" : sessionId).append(delimiter);
        sb.append(StringUtils.isBlank(reqDateTime) ? "" : reqDateTime).append(delimiter);
        sb.append(StringUtils.isBlank(actionCode) ? "" : actionCode).append(delimiter);
        sb.append(StringUtils.isBlank(rcvSys) ? "" : rcvSys).append(delimiter);
        sb.append(StringUtils.isBlank(actionCode) ? "" : actionCode).append(delimiter);
        sb.append(StringUtils.isBlank(rcvDate) ? "" : rcvDate).append(delimiter);
        sb.append(StringUtils.isBlank(rcvTransId) ? "" : rcvTransId).append(delimiter);
        sb.append(StringUtils.isBlank(rcvDateTime) ? "" : rcvDateTime).append(delimiter);
        sb.append(StringUtils.isBlank(merId) ? "" : merId).append(delimiter);
        sb.append(StringUtils.isBlank(vasType) ? "" : vasType).append(delimiter);
        sb.append(StringUtils.isBlank(payType) ? "" : payType).append(delimiter);
        sb.append(StringUtils.isBlank(amount) ? "" : amount).append(delimiter);
        sb.append(StringUtils.isBlank(homeProv) ? "" : homeProv).append(delimiter);
        sb.append(StringUtils.isBlank(chargeNumber) ? "" : chargeNumber).append(delimiter);
        sb.append(StringUtils.isBlank(tradeSeqno) ? "" : tradeSeqno).append(delimiter);
        sb.append(StringUtils.isBlank(tradeSession) ? "" : tradeSession).append(delimiter);
        sb.append(StringUtils.isBlank(oldTradeSession) ? "" : oldTradeSession).append(delimiter);
        sb.append(StringUtils.isBlank(settleDate) ? "" : settleDate).append(delimiter);
        sb.append(StringUtils.isBlank(tradeTime) ? "" : tradeTime).append(delimiter);
        sb.append(StringUtils.isBlank(oldSettleDate) ? "" : oldSettleDate).append(delimiter);
        sb.append(StringUtils.isBlank(oldTradeTime) ? "" : oldTradeTime).append(delimiter);
        sb.append(StringUtils.isBlank(spCode) ? "" : spCode).append(delimiter);
        sb.append(StringUtils.isBlank(serviceCode) ? "" : serviceCode).append(delimiter);
        sb.append(StringUtils.isBlank(balProp) ? "" : balProp).append(delimiter);
        sb.append(StringUtils.isBlank(billFlag) ? "" : billFlag).append(delimiter);
        sb.append(StringUtils.isBlank(contentId) ? "" : contentId).append(delimiter);
        sb.append(StringUtils.isBlank(pointCost) ? "" : pointCost).append(delimiter);
        sb.append(StringUtils.isBlank(visitProv) ? "" : visitProv).append(delimiter);
        sb.append(StringUtils.isBlank(depositType) ? "" : depositType).append(delimiter);
        sb.append(StringUtils.isBlank(applyTime) ? "" : applyTime).append(delimiter);
        sb.append(StringUtils.isBlank(finishTime) ? "" : finishTime).append(delimiter);
        sb.append(StringUtils.isBlank(readContentType) ? "" : readContentType).append(delimiter);
        sb.append(StringUtils.isBlank(msisdnRec) ? "" : msisdnRec).append(delimiter);
        sb.append(StringUtils.isBlank(duration) ? "" : duration).append(delimiter);
        sb.append(StringUtils.isBlank(byteSize) ? "" : byteSize).append(delimiter);
        sb.append(StringUtils.isBlank(memberType) ? "" : memberType).append(delimiter);
        sb.append(StringUtils.isBlank(cpCode) ? "" : cpCode).append(delimiter);
        sb.append(StringUtils.isBlank(userType) ? "" : userType).append(delimiter);
        sb.append(StringUtils.isBlank(crmResultCode) ? "" : crmResultCode).append(delimiter);
        sb.append(StringUtils.isBlank(resultCode) ? "" : resultCode).append(delimiter);
        sb.append(StringUtils.isBlank(resultDesc) ? "" : resultDesc).append(delimiter);
        sb.append(StringUtils.isBlank(notifyType) ? "" : notifyType);
        return sb.toString();
    }
}
