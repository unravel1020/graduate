package org.example.springboot.service.impl;

import org.example.springboot.entity.User;
import org.example.springboot.mapper.UserMapper;
import org.example.springboot.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class UserServiceImpl implements UserService {
    
    @Autowired
    private UserMapper userMapper;
    
    @Override
    public User login(String username, String password) {
        User user = userMapper.findByUsername(username);
        if (user != null && user.getPassword().equals(password)) {
            return user;
        }
        return null;
    }
    
    @Override
    public User register(User user) {
        // 检查用户名是否已存在
        if (userMapper.findByUsername(user.getUsername()) != null) {
            return null;
        }
        // 设置默认状态为正常
        user.setStatus(1);
        userMapper.insert(user);
        return user;
    }
    
    @Override
    public User findById(Long id) {
        return userMapper.findById(id);
    }
    
    @Override
    public List<User> findByRole(String role) {
        return userMapper.findByRole(role);
    }
    
    @Override
    public boolean update(User user) {
        return userMapper.update(user) > 0;
    }
    
    @Override
    public boolean deleteById(Long id) {
        return userMapper.deleteById(id) > 0;
    }
} 