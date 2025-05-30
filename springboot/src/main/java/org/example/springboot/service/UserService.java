package org.example.springboot.service;

import org.example.springboot.entity.User;
import java.util.List;

public interface UserService {
    User login(String username, String password);
    User register(User user);
    User findById(Long id);
    List<User> findByRole(String role);
    boolean update(User user);
    boolean deleteById(Long id);
} 