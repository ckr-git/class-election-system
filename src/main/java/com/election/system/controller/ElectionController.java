package com.election.system.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.election.system.common.Result;
import com.election.system.entity.Election;
import com.election.system.service.ElectionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 选举控制器
 */
@RestController
@RequestMapping("/election")
public class ElectionController {

    @Autowired
    private ElectionService electionService;

    /**
     * 获取选举列表
     */
    @GetMapping("/list")
    public Result<Page<Election>> getElectionList(
            @RequestParam(required = false) Integer status,
            @RequestParam(defaultValue = "1") Integer current,
            @RequestParam(defaultValue = "10") Integer size) {
        Page<Election> page = electionService.getElectionList(status, current, size);
        return Result.success(page);
    }

    /**
     * 获取选举详情
     */
    @GetMapping("/{id}")
    public Result<Election> getElectionDetail(@PathVariable Long id) {
        Election election = electionService.getElectionDetail(id);
        if (election != null) {
            return Result.success(election);
        } else {
            return Result.error("选举不存在");
        }
    }
}
