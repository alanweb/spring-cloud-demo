package com.alan.cloud.controller;

import com.alan.cloud.command.UserServiceCommand;
import com.alan.cloud.pojo.User;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixProperty;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.client.RestTemplate;

@Controller
public class UserController {

    @Autowired
    private RestTemplate restTemplate;

    @HystrixCommand(fallbackMethod = "fallbackFindOne",
            groupKey = "userGrop1",
            threadPoolKey = "userThreadPool",
            threadPoolProperties = {
                    @HystrixProperty(name = "coreSize",value = "2"),
                    @HystrixProperty(name = "maxQueueSize",value = "1")
    })
    @RequestMapping("user/{id}")
    @ResponseBody
    public User findOne(@PathVariable Long id) {
        return restTemplate.getForObject("http://PROVIDER-USER/user/" + id, User.class);
    }

    private User fallbackFindOne(Long id) {
        User user = new User();
        user.setId(-1L);
        user.setName("未知用户");
        return user;
    }

    @RequestMapping("user-customizing/{id}")
    @ResponseBody
    public User findOneTwo(@PathVariable Long id) {
        UserServiceCommand command = new UserServiceCommand("orederGroup", restTemplate, id);
        return command.execute();
    }
}
