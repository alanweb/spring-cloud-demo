package com.alan.cloud.command;

import com.alan.cloud.pojo.User;
import com.netflix.hystrix.HystrixCommand;
import com.netflix.hystrix.HystrixCommandGroupKey;
import org.springframework.web.client.RestTemplate;

/**
 * @author alan
 * @date 2018/6/6
 */
public class UserServiceCommand extends HystrixCommand<User> {
    private RestTemplate restTemplate;
    private Long id;

    public UserServiceCommand(String commandGroupKey, RestTemplate restTemplate, Long id) {
        super(HystrixCommandGroupKey.Factory.asKey(commandGroupKey));
        this.restTemplate = restTemplate;
        this.id = id;
    }

    @Override
    protected User run() throws Exception {
        return restTemplate.getForObject("http://PROVIDER-USER/user/" + id, User.class);
    }

    @Override
    protected User getFallback() {
        User user = new User();
        user.setId(-1L);
        user.setName("未知用户");
        return user;
    }
}
