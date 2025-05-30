package org.example.springboot.controller;

import org.example.springboot.common.Result;
import org.example.springboot.entity.User;
import org.example.springboot.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/user")
public class UserController {
    
    @Autowired
    private UserService userService;
    
    @PostMapping("/login")
    public Result<User> login(@RequestParam String username, @RequestParam String password) {
        User user = userService.login(username, password);
        if (user != null) {
            return Result.success(user);
        }
        return Result.error("用户名或密码错误");
    }
    
    @PostMapping("/register")
    public Result<User> register(@RequestBody User user) {
        User registeredUser = userService.register(user);
        if (registeredUser != null) {
            return Result.success(registeredUser);
        }
        return Result.error("注册失败，用户名可能已存在");
    }
    
    @GetMapping("/{id}")
    public Result<User> findById(@PathVariable Long id) {
        User user = userService.findById(id);
        if (user != null) {
            return Result.success(user);
        }
        return Result.error("用户不存在");
    }
    
    @GetMapping("/role/{role}")
    public Result<List<User>> findByRole(@PathVariable String role) {
        List<User> users = userService.findByRole(role);
        return Result.success(users);
    }
    
    @PutMapping
    public Result<Boolean> update(@RequestBody User user) {
        boolean success = userService.update(user);
        if (success) {
            return Result.success(true);
        }
        return Result.error("更新失败");
    }
    
    @DeleteMapping("/{id}")
    public Result<Boolean> deleteById(@PathVariable Long id) {
        boolean success = userService.deleteById(id);
        if (success) {
            return Result.success(true);
        }
        return Result.error("删除失败");
    }
} 