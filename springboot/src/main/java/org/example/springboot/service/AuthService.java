package org.example.springboot.service;

import org.example.springboot.entity.User;

public interface AuthService {
    String login(String username, String password);
    void register(User user);
    void logout(String token);
    User getCurrentUser();
} 