/**
 *
 */
package com.alan.activemq.service;

/**
 * 消息处理Service接口 所有Service的异常需要在Service内部处理
 */
public interface IService {

    /**
     * 业务方法调用
     */
    public void doService(String jsonStr, String systemId);

}
