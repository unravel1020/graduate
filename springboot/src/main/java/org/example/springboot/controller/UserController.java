package org.example.springboot.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.example.springboot.common.Result;
import org.example.springboot.entity.User;
import org.example.springboot.service.UserService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/users")
@Tag(name = "用户管理", description = "用户相关的所有接口")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @Operation(summary = "获取用户信息", description = "根据用户ID获取用户详细信息")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "成功获取用户信息"),
        @ApiResponse(responseCode = "404", description = "用户不存在")
    })
    @GetMapping("/{id}")
    public Result<User> getUserById(
            @Parameter(description = "用户ID", required = true)
            @PathVariable Long id) {
        return Result.success(userService.findById(id));
    }

    @Operation(summary = "更新用户信息", description = "更新指定用户的详细信息")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "成功更新用户信息"),
        @ApiResponse(responseCode = "404", description = "用户不存在")
    })
    @PutMapping("/{id}")
    public Result<Void> updateUser(
            @Parameter(description = "用户ID", required = true)
            @PathVariable Long id,
            @RequestBody User user) {
        user.setId(id);
        userService.update(user);
        return Result.success();
    }

    @Operation(summary = "删除用户", description = "删除指定ID的用户")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "成功删除用户"),
        @ApiResponse(responseCode = "404", description = "用户不存在")
    })
    @DeleteMapping("/{id}")
    public Result<Void> deleteUser(
            @Parameter(description = "用户ID", required = true)
            @PathVariable Long id) {
        userService.delete(id);
        return Result.success();
    }

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
    
    @GetMapping("/role/{role}")
    public Result<List<User>> findByRole(@PathVariable String role) {
        List<User> users = userService.findByRole(role);
        return Result.success(users);
    }
} 