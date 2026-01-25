package com.election.system.controller;

import com.election.system.common.Result;
import com.election.system.entity.User;
import com.election.system.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * 认证控制器
 */
@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private AuthService authService;

    /**
     * 用户注册
     */
    @PostMapping("/register")
    public Result<String> register(@RequestBody User user) {
        boolean success = authService.register(user);
        if (success) {
            return Result.success("注册成功");
        } else {
            return Result.error("用户名已存在");
        }
    }

    /**
     * 用户登录
     */
    @PostMapping("/login")
    public Result<Map<String, Object>> login(@RequestBody Map<String, String> loginData) {
        String username = loginData.get("username");
        String password = loginData.get("password");

        Map<String, Object> result = authService.login(username, password);
        if (result != null) {
            return Result.success("登录成功", result);
        } else {
            return Result.error("用户名或密码错误");
        }
    }

    /**
     * 用户登出
     */
    @PostMapping("/logout")
    public Result<String> logout() {
        return Result.success("登出成功");
    }
}
