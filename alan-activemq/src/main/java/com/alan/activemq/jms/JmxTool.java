package com.alan.activemq.jms;

import javax.management.MBeanServerConnection;
import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;
import javax.management.remote.JMXConnector;
import javax.management.remote.JMXConnectorFactory;
import javax.management.remote.JMXServiceURL;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * activemq Jmx工具
 * 
 * @author shuangzh
 *
 */
public class JmxTool
{

	/**
	 * 获取 activemq broker上所有队列的队列名
	 * 
	 * @param jxmUrl
	 * @return
	 * @throws IOException
	 * @throws MalformedObjectNameException
	 */
	public static List<String> getQueueNames(String jxmUrl) throws IOException, MalformedObjectNameException
	{

		List<String> list = new ArrayList<String>();
		JMXServiceURL url = new JMXServiceURL(jxmUrl);
		JMXConnector jmxc = JMXConnectorFactory.connect(url, null);
		MBeanServerConnection mbsc = jmxc.getMBeanServerConnection();
		ObjectName objectSelect = new ObjectName("org.apache.activemq:type=Broker,destinationType=Queue,*");
		Set<ObjectName> set = mbsc.queryNames(objectSelect, null);
		for (ObjectName it : set)
		{
			list.add(it.getKeyProperty("destinationName"));
		}
		jmxc.close();
		return list;
	}

	public static void main(String[] args) throws MalformedObjectNameException, IOException
	{
		List<String> list = JmxTool.getQueueNames("service:jmx:rmi:///jndi/rmi://127.0.0.1:1099/jmxrmi");
		for (String name : list)
		{
			System.out.println(name);
		}
	}

}
