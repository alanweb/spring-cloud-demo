package com.alan.cloud.controller;

import com.alan.cloud.pojo.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.loadbalancer.LoadBalancerClient;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.client.RestTemplate;

@Controller
public class UserController {
    @Autowired
    private RestTemplate restTemplate;
    @Autowired
    private LoadBalancerClient loadBalancerClient;

    @RequestMapping("user/{id}")
    @ResponseBody
    public User findOne(@PathVariable Long id) {
        return restTemplate.getForObject("http://PROVIDER-USER/user/" + id, User.class);
    }

    @RequestMapping("getIPAdd")
    @ResponseBody
    public String getIPAdd() {
        ServiceInstance instance = loadBalancerClient.choose("PROVIDER-USER");
        return instance.getHost() + " : " + instance.getPort() + " " + instance.getServiceId();
    }
}
