package org.example.springboot.service;

import org.example.springboot.entity.User;

public interface AuthService {
    User login(String username, String password);
    void register(User user);
    User getCurrentUser();
} 