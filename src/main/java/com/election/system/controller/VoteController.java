package com.election.system.controller;

import com.election.system.common.JwtUtil;
import com.election.system.common.Result;
import com.election.system.service.VoteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;

/**
 * 投票控制器
 */
@RestController
@RequestMapping("/vote")
public class VoteController {

    @Autowired
    private VoteService voteService;

    @Autowired
    private JwtUtil jwtUtil;

    /**
     * 提交投票
     */
    @PostMapping("/submit")
    public Result<String> submitVote(@RequestBody Map<String, Long> voteData, HttpServletRequest request) {
        // 从Token中获取用户ID
        String token = getTokenFromRequest(request);
        if (token == null) {
            return Result.error("未授权");
        }
        Long userId = jwtUtil.getUserIdFromToken(token);
        if (userId == null) {
            return Result.error("无效的Token");
        }

        Long electionId = voteData.get("electionId");
        Long candidateId = voteData.get("candidateId");
        if (electionId == null || candidateId == null) {
            return Result.error("参数不完整");
        }
        String ipAddress = getClientIP(request);

        boolean success = voteService.submitVote(electionId, candidateId, userId, ipAddress);
        if (success) {
            return Result.success("投票成功");
        } else {
            return Result.error("投票失败，请检查是否在投票时间内或已投过票");
        }
    }

    /**
     * 获取投票结果
     */
    @GetMapping("/result/{electionId}")
    public Result<Map<String, Object>> getVoteResult(@PathVariable Long electionId) {
        Map<String, Object> result = voteService.getVoteResult(electionId);
        if (result != null) {
            return Result.success(result);
        } else {
            return Result.error("选举不存在");
        }
    }

    /**
     * 获取我的投票记录
     */
    @GetMapping("/my")
    public Result<List<Map<String, Object>>> getMyVotes(
            @RequestParam(required = false) Long electionId,
            HttpServletRequest request) {
        // 从Token中获取用户ID
        String token = getTokenFromRequest(request);
        if (token == null) {
            return Result.error("未授权");
        }
        Long userId = jwtUtil.getUserIdFromToken(token);
        if (userId == null) {
            return Result.error("无效的Token");
        }

        List<Map<String, Object>> votes = voteService.getMyVotes(userId, electionId);
        return Result.success(votes);
    }

    /**
     * 获取用户在某选举中的已投票数
     */
    @GetMapping("/count")
    public Result<Map<String, Object>> getVoteCount(
            @RequestParam Long electionId,
            HttpServletRequest request) {
        String token = getTokenFromRequest(request);
        if (token == null) {
            return Result.error("未授权");
        }
        Long userId = jwtUtil.getUserIdFromToken(token);
        if (userId == null) {
            return Result.error("无效的Token");
        }

        Map<String, Object> result = voteService.getVoteCount(electionId, userId);
        return Result.success(result);
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

    /**
     * 获取客户端IP地址
     */
    private String getClientIP(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        return ip;
    }
}
