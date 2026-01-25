package com.election.system.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.election.system.entity.Candidate;
import com.election.system.entity.Election;
import com.election.system.entity.User;
import com.election.system.mapper.CandidateMapper;
import com.election.system.mapper.ElectionMapper;
import com.election.system.mapper.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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

        LocalDateTime now = LocalDateTime.now();
        if (now.isBefore(election.getApplyStartTime()) || now.isAfter(election.getApplyEndTime())) {
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

        return candidateMapper.insert(candidate) > 0;
    }

    /**
     * 获取候选人列表（分页）
     */
    public Page<Map<String, Object>> getCandidateList(Long electionId, Long positionId, Integer current, Integer size) {
        Page<Candidate> page = new Page<>(current, size);
        LambdaQueryWrapper<Candidate> queryWrapper = new LambdaQueryWrapper<>();
        
        if (electionId != null) {
            queryWrapper.eq(Candidate::getElectionId, electionId);
        }
        if (positionId != null) {
            queryWrapper.eq(Candidate::getPositionId, positionId);
        }
        queryWrapper.eq(Candidate::getStatus, 1); // 只显示审核通过的
        queryWrapper.orderByDesc(Candidate::getVoteCount);

        Page<Candidate> candidatePage = candidateMapper.selectPage(page, queryWrapper);

        // 组装候选人信息（包含用户信息）
        Page<Map<String, Object>> resultPage = new Page<>(current, size);
        resultPage.setTotal(candidatePage.getTotal());

        List<Map<String, Object>> records = candidatePage.getRecords().stream().map(candidate -> {
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
