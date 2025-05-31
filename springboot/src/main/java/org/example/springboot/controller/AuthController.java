package org.example.springboot.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.example.springboot.common.Result;
import org.example.springboot.entity.User;
import org.example.springboot.service.AuthService;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/auth")
@Tag(name = "认证管理", description = "用户认证相关的所有接口")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @Operation(summary = "用户登录", description = "使用用户名和密码进行登录")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "登录成功"),
        @ApiResponse(responseCode = "401", description = "用户名或密码错误")
    })
    @PostMapping("/login")
    public Result<User> login(@RequestBody Map<String, String> loginRequest) {
        String username = loginRequest.get("username");
        String password = loginRequest.get("password");
        User user = authService.login(username, password);
        return Result.success(user);
    }

    @Operation(summary = "用户注册", description = "注册新用户")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "注册成功"),
        @ApiResponse(responseCode = "400", description = "请求参数错误")
    })
    @PostMapping("/register")
    public Result<Void> register(@RequestBody User user) {
        authService.register(user);
        return Result.success();
    }

    @Operation(summary = "获取当前用户信息", description = "获取当前登录用户的信息")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "成功获取用户信息"),
        @ApiResponse(responseCode = "401", description = "未登录")
    })
    @GetMapping("/current-user")
    public Result<User> getCurrentUser() {
        return Result.success(authService.getCurrentUser());
    }
} 