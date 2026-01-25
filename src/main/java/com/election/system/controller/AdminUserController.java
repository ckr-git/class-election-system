package com.election.system.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.election.system.common.Result;
import com.election.system.entity.User;
import com.election.system.service.AdminUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * 管理员-用户管理控制器
 */
@RestController
@RequestMapping("/admin/user")
@PreAuthorize("hasRole('ADMIN')")
public class AdminUserController {

    @Autowired
    private AdminUserService adminUserService;

    /**
     * 获取用户列表
     */
    @GetMapping("/list")
    public Result<Page<User>> getUserList(
            @RequestParam(required = false) String username,
            @RequestParam(required = false) String role,
            @RequestParam(required = false) Integer status,
            @RequestParam(defaultValue = "1") Integer current,
            @RequestParam(defaultValue = "10") Integer size) {
        Page<User> page = adminUserService.getUserList(username, role, status, current, size);
        return Result.success(page);
    }

    /**
     * 创建用户
     */
    @PostMapping("/create")
    public Result<String> createUser(@RequestBody User user) {
        boolean success = adminUserService.createUser(user);
        if (success) {
            return Result.success("创建成功");
        } else {
            return Result.error("用户名已存在");
        }
    }

    /**
     * 更新用户信息
     */
    @PutMapping("/update")
    public Result<String> updateUser(@RequestBody User user) {
        boolean success = adminUserService.updateUser(user);
        if (success) {
            return Result.success("更新成功");
        } else {
            return Result.error("更新失败");
        }
    }

    /**
     * 删除用户
     */
    @DeleteMapping("/{id}")
    public Result<String> deleteUser(@PathVariable Long id) {
        boolean success = adminUserService.deleteUser(id);
        if (success) {
            return Result.success("删除成功");
        } else {
            return Result.error("删除失败");
        }
    }

    /**
     * 重置密码
     */
    @PostMapping("/reset-password")
    public Result<String> resetPassword(@RequestBody Map<String, Object> data) {
        Long userId = Long.valueOf(data.get("userId").toString());
        String newPassword = data.get("newPassword").toString();
        
        boolean success = adminUserService.resetPassword(userId, newPassword);
        if (success) {
            return Result.success("重置成功");
        } else {
            return Result.error("重置失败");
        }
    }

    /**
     * 启用/禁用用户
     */
    @PostMapping("/toggle-status/{id}")
    public Result<String> toggleUserStatus(@PathVariable Long id) {
        boolean success = adminUserService.toggleUserStatus(id);
        if (success) {
            return Result.success("操作成功");
        } else {
            return Result.error("操作失败");
        }
    }
}
