package com.election.system.controller;

import com.election.system.common.Result;
import com.election.system.service.AdminStatisticsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * 管理员-数据统计控制器
 */
@RestController
@RequestMapping("/admin/statistics")
@PreAuthorize("hasRole('ADMIN')")
public class AdminStatisticsController {

    @Autowired
    private AdminStatisticsService adminStatisticsService;

    /**
     * 获取仪表盘统计数据
     */
    @GetMapping("/dashboard")
    public Result<Map<String, Object>> getDashboardStats() {
        Map<String, Object> stats = adminStatisticsService.getDashboardStats();
        return Result.success(stats);
    }

    /**
     * 获取选举投票统计
     */
    @GetMapping("/election/{electionId}")
    public Result<Map<String, Object>> getElectionVoteStats(@PathVariable Long electionId) {
        Map<String, Object> stats = adminStatisticsService.getElectionVoteStats(electionId);
        return Result.success(stats);
    }
}
