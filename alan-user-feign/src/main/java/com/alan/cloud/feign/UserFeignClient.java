package com.alan.cloud.feign;

import com.alan.cloud.pojo.User;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 * @author alan
 */
@FeignClient(name = "PROVIDER-USER")
public interface UserFeignClient {
    @RequestMapping(value = "user/{id}", method = RequestMethod.GET)
    User findOne(@PathVariable("id") Long id);
}
