package com.alan.cloud.feign;

import com.alan.cloud.pojo.User;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 * @author alan
 * @date 2018/6/8
 */
@FeignClient(name = "PROVIDER-USER", fallbackFactory = UserFeignHystrixFallback.class)
public interface UserFeignHystrixClient {
    @RequestMapping(value = "user/{id}", method = RequestMethod.GET)
    User findOne(@PathVariable("id") Long id);
}

class UserFeignHystrixFallback implements UserFeignHystrixClient {

    @Override
    public User findOne(Long id) {
        User user = new User();
        user.setId(-1L);
        user.setName("未知用户");
        return user;
    }
}