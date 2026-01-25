package com.election.system.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.election.system.entity.*;
import com.election.system.mapper.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 管理员-数据统计服务
 */
@Service
public class AdminStatisticsService {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private ElectionMapper electionMapper;

    @Autowired
    private CandidateMapper candidateMapper;

    @Autowired
    private VoteRecordMapper voteRecordMapper;

    @Autowired
    private ApplicationMapper applicationMapper;

    /**
     * 获取仪表盘统计数据
     */
    public Map<String, Object> getDashboardStats() {
        Map<String, Object> stats = new HashMap<>();

        // 用户统计
        Long totalUsers = userMapper.selectCount(null);
        LambdaQueryWrapper<User> studentWrapper = new LambdaQueryWrapper<>();
        studentWrapper.eq(User::getRole, "STUDENT");
        Long studentCount = userMapper.selectCount(studentWrapper);
        
        stats.put("totalUsers", totalUsers);
        stats.put("studentCount", studentCount);

        // 选举统计
        Long totalElections = electionMapper.selectCount(null);
        LambdaQueryWrapper<Election> ongoingWrapper = new LambdaQueryWrapper<>();
        ongoingWrapper.eq(Election::getStatus, 2);
        Long ongoingElections = electionMapper.selectCount(ongoingWrapper);
        
        stats.put("totalElections", totalElections);
        stats.put("ongoingElections", ongoingElections);

        // 候选人统计
        Long totalCandidates = candidateMapper.selectCount(null);
        LambdaQueryWrapper<Candidate> pendingWrapper = new LambdaQueryWrapper<>();
        pendingWrapper.eq(Candidate::getStatus, 0);
        Long pendingCandidates = candidateMapper.selectCount(pendingWrapper);
        
        stats.put("totalCandidates", totalCandidates);
        stats.put("pendingCandidates", pendingCandidates);

        // 投票统计
        Long totalVotes = voteRecordMapper.selectCount(null);
        stats.put("totalVotes", totalVotes);

        // 申请统计
        LambdaQueryWrapper<Application> appWrapper = new LambdaQueryWrapper<>();
        appWrapper.eq(Application::getStatus, 0);
        Long pendingApplications = applicationMapper.selectCount(appWrapper);
        stats.put("pendingApplications", pendingApplications);

        return stats;
    }

    /**
     * 获取选举投票统计
     */
    public Map<String, Object> getElectionVoteStats(Long electionId) {
        Map<String, Object> stats = new HashMap<>();

        // 获取候选人投票情况
        LambdaQueryWrapper<Candidate> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Candidate::getElectionId, electionId)
                .eq(Candidate::getStatus, 1)
                .orderByDesc(Candidate::getVoteCount);
        List<Candidate> candidates = candidateMapper.selectList(queryWrapper);

        List<Map<String, Object>> candidateStats = candidates.stream().map(candidate -> {
            Map<String, Object> map = new HashMap<>();
            map.put("candidateId", candidate.getId());
            map.put("positionId", candidate.getPositionId());
            map.put("voteCount", candidate.getVoteCount());
            return map;
        }).collect(Collectors.toList());

        stats.put("candidates", candidateStats);

        // 总投票数
        LambdaQueryWrapper<VoteRecord> voteWrapper = new LambdaQueryWrapper<>();
        voteWrapper.eq(VoteRecord::getElectionId, electionId);
        Long totalVotes = voteRecordMapper.selectCount(voteWrapper);
        stats.put("totalVotes", totalVotes);

        // 投票率计算
        Long totalStudents = userMapper.selectCount(new LambdaQueryWrapper<User>().eq(User::getRole, "STUDENT"));
        if (totalStudents > 0) {
            double turnoutRate = (double) totalVotes / totalStudents * 100;
            stats.put("turnoutRate", String.format("%.2f", turnoutRate));
        } else {
            stats.put("turnoutRate", "0.00");
        }

        return stats;
    }
}
