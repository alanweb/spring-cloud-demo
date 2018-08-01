package com.cmsz.collection.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StringUtils {
	public static boolean isBlank(String s){
		return null == s || "".equals(s);
	}
	public static String replaceBlank(String str){
		String dest = "";
		if (str!=null) {
			Pattern p = Pattern.compile("\\s*|\t|\r|\n");
			Matcher m = p.matcher(str);
			dest = m.replaceAll("");
		}
		return dest;

	}
	
	/**
	 * 刪除注释代码
	 * @param s 源字符串
	 * @param endString 终止字符
	 * @return
	 */
	public static String deleteChar(String s,String endString,int i){
		int len = s.indexOf(endString);
		if (len > 0) {
			s = s.substring(len + i, s.length());
		}
		return s;
	}
	
	/**
	 * 替换节点之间的数据
	 * @param src
	 * @param startNode
	 * @param endNode
	 * @param dest
	 * @return
	 */
	public static String replaceValue(String src, String startNode, String endNode, String dest){
		if(src != null){
			int start = src.indexOf(startNode);
			int end = src.indexOf(endNode);
			if (start > 0 && end > 0) {
				String srcValue = src
						.substring(start + startNode.length(), end);
				src = src.replaceAll(srcValue, dest);
			}
		}
		return src;
	}
	
	
   /**
    * 获取报文中对应节点的值
    * @param s 报文字符串
    * @param startNode 开始节点名
    * @param endNode   结束节点名
    * @return
    */
	public static String findFieldValue(String s,String startNode,String endNode) {
		String dest = "";
		if (s != null ) {
            int start = s.indexOf(startNode);
            int end = s.indexOf(endNode);
			if (start > 0 && end > 0 && start < end) {
				dest = s.substring(start + startNode.length(), end);
			}
		}
		return dest;

	}
	
	public static void main(String[] args) {
		String xString="2017-03-02 15:33:57.817#前置接收到浙江运营中心请求报文#<?xml version=\"1.0\" encoding=\"UTF-8\"?><GPay><Header><ActivityCode>012003</ActivityCode><ReqSys>0051</ReqSys><ReqChannel>81</ReqChannel><ReqDate>20170302</ReqDate><ReqTransID>rechar201703021533410863362775</ReqTransID><ReqDateTime>20170302153341098</ReqDateTime><ActionCode>0</ActionCode><RcvSys>0001</RcvSys></Header><Body><OrderID>rechar201703021533410993349555</OrderID><PayTransID>1</PayTransID><IDType>01</IDType><IDValue>13539409084</IDValue><HomeProv>200</HomeProv><Payment>4321</Payment><ChargeMoney>4321</ChargeMoney><ProdCnt>1</ProdCnt><ProdID>67JFFC4AN</ProdID><Commision>25</Commision><RebateFee>4</RebateFee><ProdDiscount></ProdDiscount><CreditCardFee></CreditCardFee><ServiceFee>0</ServiceFee><PayedType>01</PayedType><ActivityNO></ActivityNO><ProdShelfNO></ProdShelfNO><Reserve1>0</Reserve1><Reserve2>0</Reserve2><Reserve3>0</Reserve3><Reserve4>0</Reserve4><FeedBackURL>http://192.168.122.41:8090/CMUPayImitator/ReceiveMsgAutoResponServletForTmall</FeedBackURL></Body><Sign><SignFlag>1</SignFlag><CerID>1</CerID><SignValue></SignValue></Sign></GPay>";
		String x1="2017-03-03 15:57:39.529#省前置接收到省BOSS的请求报文头#<?xml version=\"1.0\" encoding=\"UTF-8\"?><InterBOSS><Version>0100</Version><TestFlag>1</TestFlag><BIPType><BIPCode>BIP1A173</BIPCode><ActivityCode>T1000169</ActivityCode><ActionCode>0</ActionCode></BIPType><RoutingInfo><OrigDomain>BOSS</OrigDomain><RouteType>00</RouteType><Routing><HomeDomain>UPSS</HomeDomain><RouteValue>997</RouteValue></Routing></RoutingInfo><TransInfo><SessionID>20170303155722856782</SessionID><TransIDO>T10001692017030315572284277442</TransIDO><TransIDOTime>20170303155722</TransIDOTime></TransInfo><SNReserve><TransIDC>12312323132</TransIDC><ConvID>1231231</ConvID><CutOffDay>20170303</CutOffDay><OSNTime>20170303155722</OSNTime><OSNDUNS>2000</OSNDUNS><HSNDUNS>9970</HSNDUNS><MsgSender>2001</MsgSender><MsgReceiver>0233</MsgReceiver><Priority></Priority><ServiceLevel></ServiceLevel><SvcContType></SvcContType></SNReserve></InterBOSS>2017-03-03 15:57:39.530#省前置接收到省BOSS的请求报文体#<?xml version=\"1.0\"  encoding=\"UTF-8\"?><InterBOSS><SvcCont><![CDATA[<?xml version=\"1.0\" encoding=\"UTF-8\"?><SignReq> <SignFlag>0</SignFlag> <Reserved1></Reserved1><Reserved2></Reserved2> </SignReq>]]></SvcCont></InterBOSS>";
//		String s = StringUtils.findFieldValue(xString, "<ActivityCode>", "</ActivityCode>");
		String x2="<?xml version=\"1.0\" encoding=\"UTF-8\"?><PayStateReq><OriReqSys>0001</OriReqSys><OriActionDate>20170315</OriActionDate><OriTransactionID>10999201703150723024653720643975</OriTransactionID><OriActivityCode>T1000167</OriActivityCode></PayStateReq>";
//		System.out.println(s);
		String s3=StringUtils.findFieldValue(x2, "<OriTransactionID>", "</OriTransactionID>"); // 省操作流水号
		System.out.println("s3===="+s3);
		String s1 = StringUtils.deleteChar(x1, "#<?",1);
		System.out.println("s1===="+s1);
		String s2 = StringUtils.replaceValue(xString, "<FeedBackURL>", "</FeedBackURL>", "http://www.baidu.com");
		System.out.println(s2);
		String s4="<?xml version=\"1.0\" encoding=\"UTF-8\"?><GPay><Header><ActivityCode>022003</ActivityCode><ActionCode>0</ActionCode><ReqSys>0001</ReqSys><ReqTransID>102017030905423639937234021407</ReqTransID><ReqDate>20170309</ReqDate><ReqDateTime>20170309184156635</ReqDateTime><RcvSys>0051</RcvSys><ReqChannel>00</ReqChannel></Header><Body><OriReqTransID>rechar201703091457378183673400</OriReqTransID><OriReqDate>20170309</OriReqDate><OrderID>rechar201703091457378203681662</OrderID><ResultCode>010A00</ResultCode><ResultDesc>成功</ResultDesc><ResultTime>20170309145749</ResultTime></Body><Sign><SignFlag>0</SignFlag><CerID></CerID><SignValue></SignValue></Sign></GPay>";
        String x4 = StringUtils.deleteChar(s4, "?>",2);
        System.out.println("x4==="+x4);
	}
}
