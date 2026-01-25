package com.election.system.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.election.system.common.Result;
import com.election.system.service.AdminCandidateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * 管理员-候选人管理控制器
 */
@RestController
@RequestMapping("/admin/candidate")
@PreAuthorize("hasRole('ADMIN')")
public class AdminCandidateController {

    @Autowired
    private AdminCandidateService adminCandidateService;

    /**
     * 获取候选人列表
     */
    @GetMapping("/list")
    public Result<Page<Map<String, Object>>> getCandidateList(
            @RequestParam(required = false) Long electionId,
            @RequestParam(required = false) Integer status,
            @RequestParam(defaultValue = "1") Integer current,
            @RequestParam(defaultValue = "10") Integer size) {
        Page<Map<String, Object>> page = adminCandidateService.getCandidateList(electionId, status, current, size);
        return Result.success(page);
    }

    /**
     * 审核候选人
     */
    @PostMapping("/review")
    public Result<String> reviewCandidate(@RequestBody Map<String, Object> data) {
        Long candidateId = Long.valueOf(data.get("candidateId").toString());
        Integer status = Integer.valueOf(data.get("status").toString());
        String reviewOpinion = data.get("reviewOpinion").toString();
        
        boolean success = adminCandidateService.reviewCandidate(candidateId, status, reviewOpinion);
        if (success) {
            return Result.success("审核成功");
        } else {
            return Result.error("审核失败");
        }
    }

    /**
     * 删除候选人
     */
    @DeleteMapping("/{id}")
    public Result<String> deleteCandidate(@PathVariable Long id) {
        boolean success = adminCandidateService.deleteCandidate(id);
        if (success) {
            return Result.success("删除成功");
        } else {
            return Result.error("删除失败");
        }
    }
}
