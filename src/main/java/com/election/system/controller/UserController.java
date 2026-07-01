package com.election.system.controller;

import com.election.system.common.JwtUtil;
import com.election.system.common.Result;
import com.election.system.entity.User;
import com.election.system.mapper.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

/**
 * 用户个人中心控制器
 */
@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtUtil jwtUtil;

    /**
     * 获取当前用户信息
     */
    @GetMapping("/profile")
    public Result<User> getProfile(HttpServletRequest request) {
        String token = request.getHeader("Authorization");
        if (token != null && token.startsWith("Bearer ")) {
            token = token.substring(7);
        }
        Long userId = jwtUtil.getUserIdFromToken(token);
        User user = userMapper.selectById(userId);
        if (user != null) {
            user.setPassword(null); // 不返回密码
            return Result.success(user);
        }
        return Result.error("用户不存在");
    }

    /**
     * 修改密码
     */
    @PostMapping("/change-password")
    public Result<String> changePassword(@RequestBody Map<String, String> pwdData, HttpServletRequest request) {
        String token = request.getHeader("Authorization");
        if (token != null && token.startsWith("Bearer ")) {
            token = token.substring(7);
        }
        Long userId = jwtUtil.getUserIdFromToken(token);

        String oldPassword = pwdData.get("oldPassword");
        String newPassword = pwdData.get("newPassword");

        if (oldPassword == null || newPassword == null) {
            return Result.error("参数不完整");
        }

        if (newPassword.length() < 6) {
            return Result.error("新密码长度至少6位");
        }

        User user = userMapper.selectById(userId);
        if (user == null) {
            return Result.error("用户不存在");
        }

        // 验证旧密码
        if (!passwordEncoder.matches(oldPassword, user.getPassword())) {
            return Result.error("原密码错误");
        }

        // 更新密码
        user.setPassword(passwordEncoder.encode(newPassword));
        int rows = userMapper.updateById(user);

        if (rows > 0) {
            return Result.success("密码修改成功");
        } else {
            return Result.error("密码修改失败");
        }
    }

    /**
     * 更新个人信息
     */
    @PutMapping("/profile")
    public Result<String> updateProfile(@RequestBody User user, HttpServletRequest request) {
        String token = request.getHeader("Authorization");
        if (token != null && token.startsWith("Bearer ")) {
            token = token.substring(7);
        }
        Long userId = jwtUtil.getUserIdFromToken(token);

        User existingUser = userMapper.selectById(userId);
        if (existingUser == null) {
            return Result.error("用户不存在");
        }

        // 只允许更新部分字段
        if (user.getNickname() != null) {
            existingUser.setNickname(user.getNickname());
        }
        if (user.getPhone() != null) {
            existingUser.setPhone(user.getPhone());
        }
        if (user.getEmail() != null) {
            existingUser.setEmail(user.getEmail());
        }

        int rows = userMapper.updateById(existingUser);
        if (rows > 0) {
            return Result.success("信息更新成功");
        } else {
            return Result.error("信息更新失败");
        }
    }
}
