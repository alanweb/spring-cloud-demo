package com.alan.user.controller;

import com.alan.user.pojo.User;
import com.alan.user.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class UserController {
    @Autowired
    private UserService userService;

    @RequestMapping("user/{id}")
    @ResponseBody
    public User findOne(@PathVariable Long id) {

        try {
            int sleep = (int) Math.floor(Math.random() * 2000);
            Thread.sleep(sleep);
            System.out.println("sleep " + sleep + "ms");
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return userService.findOne(id);
    }

    @RequestMapping("api/{id}")
    @ResponseBody
    public User api(@PathVariable Long id) {
        return userService.findOne(id);
    }
}
