package com.alan.cloud.controller;

import com.alan.cloud.feign.UserFeignClient;
import com.alan.cloud.feign.UserFeignHystrixClient;
import com.alan.cloud.pojo.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.loadbalancer.LoadBalancerClient;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class UserController {
    @Autowired
    private UserFeignClient userFeignClient;
    @Autowired
    private LoadBalancerClient loadBalancerClient;
    @Autowired
    private UserFeignHystrixClient userFeignHystrixClient;

    @RequestMapping("hystrix/user/{id}")
    @ResponseBody
    public User hystrixFindOne(@PathVariable Long id) {
        return userFeignHystrixClient.findOne(id);
    }

    @RequestMapping("user/{id}")
    @ResponseBody
    public User findOne(@PathVariable Long id) {
        return userFeignClient.findOne(id);
    }

    @RequestMapping("getIPAdd")
    @ResponseBody
    public String getIPAdd() {
        ServiceInstance instance = loadBalancerClient.choose("PROVIDER-USER");
        return instance.getHost() + " : " + instance.getPort() + " " + instance.getServiceId();
    }
}
