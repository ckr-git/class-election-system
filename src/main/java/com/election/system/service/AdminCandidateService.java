package com.election.system.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.election.system.entity.Candidate;
import com.election.system.entity.User;
import com.election.system.mapper.CandidateMapper;
import com.election.system.mapper.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
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

        // 组装候选人信息
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
            map.put("status", candidate.getStatus());
            map.put("reviewOpinion", candidate.getReviewOpinion());
            map.put("createTime", candidate.getCreateTime());

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
     * 审核候选人
     */
    public boolean reviewCandidate(Long candidateId, Integer status, String reviewOpinion) {
        Candidate candidate = candidateMapper.selectById(candidateId);
        if (candidate == null) {
            return false;
        }
        
        candidate.setStatus(status);
        candidate.setReviewOpinion(reviewOpinion);
        
        return candidateMapper.updateById(candidate) > 0;
    }

    /**
     * 删除候选人
     */
    public boolean deleteCandidate(Long candidateId) {
        return candidateMapper.deleteById(candidateId) > 0;
    }
}
