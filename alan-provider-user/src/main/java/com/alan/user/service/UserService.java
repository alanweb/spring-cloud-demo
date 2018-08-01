package com.alan.user.service;

import com.alan.user.pojo.User;
import org.springframework.stereotype.Service;

public interface UserService {
    User findOne(Long id);
}
