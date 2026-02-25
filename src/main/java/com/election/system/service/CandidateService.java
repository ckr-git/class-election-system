package com.election.system.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.election.system.common.XssUtil;
import com.election.system.entity.Candidate;
import com.election.system.entity.Election;
import com.election.system.entity.User;
import com.election.system.mapper.CandidateMapper;
import com.election.system.mapper.ElectionMapper;
import com.election.system.mapper.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 候选人服务
 */
@Service
public class CandidateService {

    @Autowired
    private CandidateMapper candidateMapper;

    @Autowired
    private ElectionMapper electionMapper;

    @Autowired
    private UserMapper userMapper;

    /**
     * 申请成为候选人
     */
    public boolean apply(Candidate candidate, Long userId) {
        // 检查选举是否在报名期间
        Election election = electionMapper.selectById(candidate.getElectionId());
        if (election == null) {
            return false;
        }

        // 检查报名时间（处理null情况）
        LocalDateTime now = LocalDateTime.now();
        if (election.getApplyStartTime() != null && now.isBefore(election.getApplyStartTime())) {
            return false;
        }
        if (election.getApplyEndTime() != null && now.isAfter(election.getApplyEndTime())) {
            return false;
        }

        // 检查是否已经申请过该职位
        LambdaQueryWrapper<Candidate> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Candidate::getElectionId, candidate.getElectionId())
                .eq(Candidate::getPositionId, candidate.getPositionId())
                .eq(Candidate::getUserId, userId);
        Long count = candidateMapper.selectCount(queryWrapper);
        if (count > 0) {
            return false;
        }

        candidate.setUserId(userId);
        candidate.setStatus(0); // 待审核
        candidate.setVoteCount(0);

        // XSS过滤
        candidate.setSlogan(XssUtil.clean(candidate.getSlogan()));
        candidate.setIntro(XssUtil.clean(candidate.getIntro()));
        candidate.setAchievements(XssUtil.clean(candidate.getAchievements()));

        return candidateMapper.insert(candidate) > 0;
    }

    /**
     * 获取候选人列表（分页）
     */
    public Page<Map<String, Object>> getCandidateList(Long electionId, Long positionId, Long userId, Integer current, Integer size) {
        Page<Candidate> page = new Page<>(current, size);
        LambdaQueryWrapper<Candidate> queryWrapper = new LambdaQueryWrapper<>();

        if (electionId != null) {
            queryWrapper.eq(Candidate::getElectionId, electionId);
        }
        if (positionId != null) {
            queryWrapper.eq(Candidate::getPositionId, positionId);
        }
        if (userId != null) {
            queryWrapper.eq(Candidate::getUserId, userId);
        } else {
            queryWrapper.eq(Candidate::getStatus, 1); // 无userId时只显示审核通过的
        }
        queryWrapper.orderByDesc(Candidate::getVoteCount);

        Page<Candidate> candidatePage = candidateMapper.selectPage(page, queryWrapper);

        // 批量查询用户和选举，避免N+1
        Page<Map<String, Object>> resultPage = new Page<>(current, size);
        resultPage.setTotal(candidatePage.getTotal());

        List<Candidate> candidates = candidatePage.getRecords();
        if (candidates.isEmpty()) {
            resultPage.setRecords(Collections.emptyList());
            return resultPage;
        }

        Set<Long> userIds = candidates.stream().map(Candidate::getUserId).collect(Collectors.toSet());
        Set<Long> electionIds = candidates.stream().map(Candidate::getElectionId).collect(Collectors.toSet());

        Map<Long, User> userMap = userMapper.selectBatchIds(userIds).stream()
                .collect(Collectors.toMap(User::getId, Function.identity()));
        Map<Long, Election> electionMap = electionMapper.selectBatchIds(electionIds).stream()
                .collect(Collectors.toMap(Election::getId, Function.identity()));

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

            Election election = electionMap.get(candidate.getElectionId());
            if (election != null) {
                map.put("electionTitle", election.getTitle());
            }

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
     * 获取候选人详情
     */
    public Map<String, Object> getCandidateDetail(Long candidateId) {
        Candidate candidate = candidateMapper.selectById(candidateId);
        if (candidate == null) {
            return null;
        }

        Map<String, Object> map = new HashMap<>();
        map.put("id", candidate.getId());
        map.put("electionId", candidate.getElectionId());
        map.put("positionId", candidate.getPositionId());
        map.put("slogan", candidate.getSlogan());
        map.put("intro", candidate.getIntro());
        map.put("achievements", candidate.getAchievements());
        map.put("photo", candidate.getPhoto());
        map.put("voteCount", candidate.getVoteCount());

        // 获取用户信息
        User user = userMapper.selectById(candidate.getUserId());
        if (user != null) {
            map.put("username", user.getUsername());
            map.put("nickname", user.getNickname());
            map.put("avatar", user.getAvatar());
        }

        return map;
    }
}
