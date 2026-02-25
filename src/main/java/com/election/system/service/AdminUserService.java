package com.election.system.service;

import com.alibaba.excel.EasyExcel;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.election.system.common.XssUtil;
import com.election.system.dto.UserImportDTO;
import com.election.system.entity.User;
import com.election.system.mapper.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
        // XSS过滤
        if (user.getNickname() != null) {
            user.setNickname(XssUtil.clean(user.getNickname()));
        }
        if (user.getPhone() != null) {
            user.setPhone(XssUtil.clean(user.getPhone()));
        }
        if (user.getEmail() != null) {
            user.setEmail(XssUtil.clean(user.getEmail()));
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

    /**
     * 批量导入用户
     */
    public Map<String, Object> importUsers(MultipartFile file) throws IOException {
        List<UserImportDTO> dataList = EasyExcel.read(file.getInputStream())
                .head(UserImportDTO.class)
                .sheet()
                .doReadSync();

        int successCount = 0;
        int failCount = 0;
        List<String> failDetails = new ArrayList<>();

        for (int i = 0; i < dataList.size(); i++) {
            UserImportDTO dto = dataList.get(i);
            int rowNum = i + 2; // Excel行号（第1行是表头）

            // 跳过空行
            if (isEmptyRow(dto)) {
                continue;
            }

            if (dto.getUsername() == null || dto.getUsername().trim().isEmpty()) {
                failCount++;
                failDetails.add("第" + rowNum + "行: 学号为空");
                continue;
            }

            String username = XssUtil.clean(dto.getUsername().trim());

            // 检查重复
            LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(User::getUsername, username);
            Long count = userMapper.selectCount(queryWrapper);
            if (count > 0) {
                failCount++;
                failDetails.add("第" + rowNum + "行: 学号" + username + "已存在");
                continue;
            }

            User user = new User();
            user.setUsername(username);
            user.setNickname(XssUtil.clean(dto.getNickname() != null ? dto.getNickname().trim() : username));
            user.setClassId(dto.getClassId());
            user.setPhone(XssUtil.clean(dto.getPhone() != null ? dto.getPhone().trim() : ""));
            user.setEmail(XssUtil.clean(dto.getEmail() != null ? dto.getEmail().trim() : ""));

            // 默认密码：学号后6位
            String defaultPwd = username.length() >= 6 ? username.substring(username.length() - 6) : username;
            user.setPassword(passwordEncoder.encode(defaultPwd));
            user.setRole("STUDENT");
            user.setStatus(1);

            try {
                userMapper.insert(user);
                successCount++;
            } catch (Exception e) {
                failCount++;
                failDetails.add("第" + rowNum + "行: 导入失败");
            }
        }

        Map<String, Object> result = new HashMap<>();
        result.put("successCount", successCount);
        result.put("failCount", failCount);
        result.put("failDetails", failDetails);
        return result;
    }

    private boolean isEmptyRow(UserImportDTO dto) {
        return (dto.getUsername() == null || dto.getUsername().trim().isEmpty())
                && (dto.getNickname() == null || dto.getNickname().trim().isEmpty())
                && dto.getClassId() == null
                && (dto.getPhone() == null || dto.getPhone().trim().isEmpty())
                && (dto.getEmail() == null || dto.getEmail().trim().isEmpty());
    }
}
