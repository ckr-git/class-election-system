package com.election.system.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.election.system.common.XssUtil;
import com.election.system.entity.Candidate;
import com.election.system.entity.User;
import com.election.system.mapper.CandidateMapper;
import com.election.system.mapper.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 管理员-候选人管理服务
 */
@Service
public class AdminCandidateService {

    @Autowired
    private CandidateMapper candidateMapper;

    @Autowired
    private UserMapper userMapper;

    /**
     * 获取候选人列表（分页，包含所有状态）
     */
    public Page<Map<String, Object>> getCandidateList(Long electionId, Integer status, Integer current, Integer size) {
        Page<Candidate> page = new Page<>(current, size);
        LambdaQueryWrapper<Candidate> queryWrapper = new LambdaQueryWrapper<>();
        
        if (electionId != null) {
            queryWrapper.eq(Candidate::getElectionId, electionId);
        }
        if (status != null) {
            queryWrapper.eq(Candidate::getStatus, status);
        }
        queryWrapper.orderByDesc(Candidate::getCreateTime);

        Page<Candidate> candidatePage = candidateMapper.selectPage(page, queryWrapper);

        Page<Map<String, Object>> resultPage = new Page<>(current, size);
        resultPage.setTotal(candidatePage.getTotal());

        List<Candidate> candidates = candidatePage.getRecords();
        if (candidates.isEmpty()) {
            resultPage.setRecords(Collections.emptyList());
            return resultPage;
        }

        // 批量查询用户，避免N+1
        Set<Long> userIds = candidates.stream().map(Candidate::getUserId).collect(Collectors.toSet());
        Map<Long, User> userMap = userMapper.selectBatchIds(userIds).stream()
                .collect(Collectors.toMap(User::getId, Function.identity()));

        List<Map<String, Object>> records = candidates.stream().map(candidate -> {
            Map<String, Object> map = new HashMap<>();
            map.put("id", candidate.getId());
            map.put("electionId", candidate.getElectionId());
            map.put("positionId", candidate.getPositionId());
            map.put("slogan", candidate.getSlogan());
            map.put("intro", candidate.getIntro());
            map.put("achievements", candidate.getAchievements());
            map.put("photo", candidate.getPhoto());
            map.put("voteCount", candidate.getVoteCount());
            map.put("status", candidate.getStatus());
            map.put("reviewOpinion", candidate.getReviewOpinion());
            map.put("createTime", candidate.getCreateTime());

            User user = userMap.get(candidate.getUserId());
            if (user != null) {
                map.put("username", user.getUsername());
                map.put("nickname", user.getNickname());
                map.put("avatar", user.getAvatar());
            }

            return map;
        }).collect(Collectors.toList());

        resultPage.setRecords(records);
        return resultPage;
    }

    /**
     * 审核候选人
     */
    public boolean reviewCandidate(Long candidateId, Integer status, String reviewOpinion) {
        Candidate candidate = candidateMapper.selectById(candidateId);
        if (candidate == null) {
            return false;
        }
        // 只能审核待审核状态的候选人
        if (candidate.getStatus() != 0) {
            return false;
        }
        // 只允许通过(1)或拒绝(2)
        if (status != 1 && status != 2) {
            return false;
        }

        candidate.setStatus(status);
        candidate.setReviewOpinion(XssUtil.clean(reviewOpinion));

        return candidateMapper.updateById(candidate) > 0;
    }

    /**
     * 删除候选人
     */
    public boolean deleteCandidate(Long candidateId) {
        return candidateMapper.deleteById(candidateId) > 0;
    }
}
