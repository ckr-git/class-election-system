package com.election.system.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.election.system.common.JwtUtil;
import com.election.system.common.Result;
import com.election.system.entity.Candidate;
import com.election.system.service.CandidateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

/**
 * 候选人控制器
 */
@RestController
@RequestMapping("/candidate")
public class CandidateController {

    @Autowired
    private CandidateService candidateService;

    @Autowired
    private JwtUtil jwtUtil;

    /**
     * 申请成为候选人
     */
    @PostMapping("/apply")
    public Result<String> apply(@RequestBody Candidate candidate, HttpServletRequest request) {
        // 从Token中获取用户ID
        String token = getTokenFromRequest(request);
        if (token == null) {
            return Result.error("未授权");
        }
        Long userId = jwtUtil.getUserIdFromToken(token);
        if (userId == null) {
            return Result.error("无效的Token");
        }

        boolean success = candidateService.apply(candidate, userId);
        if (success) {
            return Result.success("申请成功，等待审核");
        } else {
            return Result.error("申请失败，请检查是否在报名时间内或已申请过");
        }
    }

    /**
     * 获取候选人列表
     */
    @GetMapping("/list")
    public Result<Page<Map<String, Object>>> getCandidateList(
            @RequestParam(required = false) Long electionId,
            @RequestParam(required = false) Long positionId,
            @RequestParam(required = false) Long userId,
            @RequestParam(defaultValue = "1") Integer current,
            @RequestParam(defaultValue = "10") Integer size,
            HttpServletRequest request) {
        // IDOR防护：非管理员只能查自己的申请
        if (userId != null) {
            String token = getTokenFromRequest(request);
            if (token == null) {
                return Result.error("未授权");
            }
            Long currentUserId = jwtUtil.getUserIdFromToken(token);
            String role = jwtUtil.getRoleFromToken(token);
            if (!"ADMIN".equals(role) && (currentUserId == null || !currentUserId.equals(userId))) {
                return Result.error(403, "无权查询其他用户申请");
            }
        }
        Page<Map<String, Object>> page = candidateService.getCandidateList(electionId, positionId, userId, current, size);
        return Result.success(page);
    }

    /**
     * 获取候选人详情
     */
    @GetMapping("/{id}")
    public Result<Map<String, Object>> getCandidateDetail(@PathVariable Long id) {
        Map<String, Object> candidate = candidateService.getCandidateDetail(id);
        if (candidate != null) {
            return Result.success(candidate);
        } else {
            return Result.error("候选人不存在");
        }
    }

    /**
     * 从请求头中获取Token
     */
    private String getTokenFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }
}
