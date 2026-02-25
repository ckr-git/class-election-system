package com.election.system.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.election.system.common.JwtUtil;
import com.election.system.common.Result;
import com.election.system.entity.Election;
import com.election.system.service.AdminElectionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

/**
 * 管理员-选举管理控制器
 */
@RestController
@RequestMapping("/admin/election")
@PreAuthorize("hasRole('ADMIN')")
public class AdminElectionController {

    @Autowired
    private AdminElectionService adminElectionService;

    @Autowired
    private JwtUtil jwtUtil;

    /**
     * 获取选举列表
     */
    @GetMapping("/list")
    public Result<Page<Election>> getElectionList(
            @RequestParam(required = false) Integer status,
            @RequestParam(required = false) String keyword,
            @RequestParam(defaultValue = "1") Integer current,
            @RequestParam(defaultValue = "10") Integer size) {
        Page<Election> page = adminElectionService.getElectionList(status, keyword, current, size);
        return Result.success(page);
    }

    /**
     * 创建选举
     */
    @PostMapping("/create")
    public Result<String> createElection(@RequestBody Election election, HttpServletRequest request) {
        String token = getTokenFromRequest(request);
        if (token == null) {
            return Result.error("未授权");
        }
        Long creatorId = jwtUtil.getUserIdFromToken(token);
        if (creatorId == null) {
            return Result.error("无效的Token");
        }

        boolean success = adminElectionService.createElection(election, creatorId);
        if (success) {
            return Result.success("创建成功");
        } else {
            return Result.error("创建失败");
        }
    }

    /**
     * 更新选举
     */
    @PutMapping("/update")
    public Result<String> updateElection(@RequestBody Election election) {
        boolean success = adminElectionService.updateElection(election);
        if (success) {
            return Result.success("更新成功");
        } else {
            return Result.error("更新失败");
        }
    }

    /**
     * 删除选举
     */
    @DeleteMapping("/{id}")
    public Result<String> deleteElection(@PathVariable Long id) {
        boolean success = adminElectionService.deleteElection(id);
        if (success) {
            return Result.success("删除成功");
        } else {
            return Result.error("删除失败");
        }
    }

    /**
     * 更改选举状态
     */
    @PostMapping("/change-status")
    public Result<String> changeElectionStatus(@RequestBody Map<String, Object> data) {
        if (data.get("electionId") == null || data.get("status") == null) {
            return Result.error("参数不完整");
        }
        Long electionId = Long.valueOf(data.get("electionId").toString());
        Integer status = Integer.valueOf(data.get("status").toString());

        boolean success = adminElectionService.changeElectionStatus(electionId, status);
        if (success) {
            return Result.success("状态更新成功");
        } else {
            return Result.error("状态更新失败");
        }
    }

    private String getTokenFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }
}
