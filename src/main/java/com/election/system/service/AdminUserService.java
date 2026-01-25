package com.election.system.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.election.system.entity.User;
import com.election.system.mapper.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

/**
 * 管理员-用户管理服务
 */
@Service
public class AdminUserService {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private PasswordEncoder passwordEncoder;

    /**
     * 获取用户列表（分页）
     */
    public Page<User> getUserList(String username, String role, Integer status, Integer current, Integer size) {
        Page<User> page = new Page<>(current, size);
        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
        
        if (username != null && !username.isEmpty()) {
            queryWrapper.like(User::getUsername, username)
                    .or().like(User::getNickname, username);
        }
        if (role != null && !role.isEmpty()) {
            queryWrapper.eq(User::getRole, role);
        }
        if (status != null) {
            queryWrapper.eq(User::getStatus, status);
        }
        queryWrapper.orderByDesc(User::getCreateTime);

        return userMapper.selectPage(page, queryWrapper);
    }

    /**
     * 创建用户
     */
    public boolean createUser(User user) {
        // 检查用户名是否已存在
        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(User::getUsername, user.getUsername());
        Long count = userMapper.selectCount(queryWrapper);
        if (count > 0) {
            return false;
        }

        // 加密密码
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        return userMapper.insert(user) > 0;
    }

    /**
     * 更新用户信息
     */
    public boolean updateUser(User user) {
        // 如果修改了密码，需要加密
        if (user.getPassword() != null && !user.getPassword().isEmpty()) {
            user.setPassword(passwordEncoder.encode(user.getPassword()));
        }
        return userMapper.updateById(user) > 0;
    }

    /**
     * 删除用户
     */
    public boolean deleteUser(Long userId) {
        return userMapper.deleteById(userId) > 0;
    }

    /**
     * 重置密码
     */
    public boolean resetPassword(Long userId, String newPassword) {
        User user = userMapper.selectById(userId);
        if (user == null) {
            return false;
        }
        user.setPassword(passwordEncoder.encode(newPassword));
        return userMapper.updateById(user) > 0;
    }

    /**
     * 启用/禁用用户
     */
    public boolean toggleUserStatus(Long userId) {
        User user = userMapper.selectById(userId);
        if (user == null) {
            return false;
        }
        user.setStatus(user.getStatus() == 1 ? 0 : 1);
        return userMapper.updateById(user) > 0;
    }
}
