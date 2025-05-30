package org.example.springboot.controller;

import org.example.springboot.common.Result;
import org.example.springboot.entity.User;
import org.example.springboot.service.AuthService;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/login")
    public Result<Map<String, String>> login(@RequestBody Map<String, String> loginRequest) {
        String username = loginRequest.get("username");
        String password = loginRequest.get("password");
        String token = authService.login(username, password);
        
        Map<String, String> response = new HashMap<>();
        response.put("token", token);
        return Result.success(response);
    }

    @PostMapping("/register")
    public Result<Void> register(@RequestBody User user) {
        authService.register(user);
        return Result.success();
    }

    @PostMapping("/logout")
    public Result<Void> logout(@RequestHeader("Authorization") String token) {
        authService.logout(token);
        return Result.success();
    }

    @GetMapping("/current-user")
    public Result<User> getCurrentUser() {
        return Result.success(authService.getCurrentUser());
    }
} 