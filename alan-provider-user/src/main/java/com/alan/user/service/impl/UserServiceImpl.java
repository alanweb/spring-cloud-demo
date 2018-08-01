package com.alan.user.service.impl;

import com.alan.user.pojo.User;
import com.alan.user.repository.UserRepository;
import com.alan.user.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements UserService {
    @Autowired
    private UserRepository userRepository;

    @Override
    public User findOne(Long id) {
        return userRepository.findOne(id);
    }
}
