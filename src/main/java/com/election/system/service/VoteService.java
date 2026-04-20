package com.election.system.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.election.system.entity.Candidate;
import com.election.system.entity.Election;
import com.election.system.entity.VoteRecord;
import com.election.system.entity.User;
import com.election.system.mapper.CandidateMapper;
import com.election.system.mapper.ElectionMapper;
import com.election.system.mapper.UserMapper;
import com.election.system.mapper.VoteRecordMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 投票服务
 */
@Service
public class VoteService {

    @Autowired
    private VoteRecordMapper voteRecordMapper;

    @Autowired
    private CandidateMapper candidateMapper;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private ElectionMapper electionMapper;

    /**
     * 提交投票
     */
    @Transactional(rollbackFor = Exception.class)
    public boolean submitVote(Long electionId, Long candidateId, Long voterId, String ipAddress) {
        // 检查选举是否在投票期间
        Election election = electionMapper.selectById(electionId);
        if (election == null || election.getStatus() != 2) {
            return false;
        }

        LocalDateTime now = LocalDateTime.now();
        if (now.isBefore(election.getStartTime()) || now.isAfter(election.getEndTime())) {
            return false;
        }

        // 检查候选人是否属于该选举且已通过审核
        LambdaQueryWrapper<Candidate> candidateQuery = new LambdaQueryWrapper<>();
        candidateQuery.eq(Candidate::getId, candidateId)
                .eq(Candidate::getElectionId, electionId)
                .eq(Candidate::getStatus, 1);
        Long candidateCount = candidateMapper.selectCount(candidateQuery);
        if (candidateCount == 0) {
            return false;
        }

        // 检查是否已经投过票
        LambdaQueryWrapper<VoteRecord> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(VoteRecord::getElectionId, electionId)
                .eq(VoteRecord::getVoterId, voterId)
                .eq(VoteRecord::getCandidateId, candidateId);
        Long count = voteRecordMapper.selectCount(queryWrapper);
        if (count > 0) {
            return false;
        }

        // 检查投票数限制
        LambdaQueryWrapper<VoteRecord> voteQueryWrapper = new LambdaQueryWrapper<>();
        voteQueryWrapper.eq(VoteRecord::getElectionId, electionId)
                .eq(VoteRecord::getVoterId, voterId);
        Long voteCount = voteRecordMapper.selectCount(voteQueryWrapper);
        if (voteCount >= election.getVoteLimit()) {
            return false;
        }

        // 保存投票记录
        VoteRecord voteRecord = new VoteRecord();
        voteRecord.setElectionId(electionId);
        voteRecord.setVoterId(voterId);
        voteRecord.setCandidateId(candidateId);
        voteRecord.setIpAddress(ipAddress);

        if (voteRecordMapper.insert(voteRecord) > 0) {
            // 原子更新候选人票数，避免并发问题
            candidateMapper.incrementVoteCount(candidateId);
            return true;
        }

        return false;
    }

    /**
     * 获取投票结果
     */
    public Map<String, Object> getVoteResult(Long electionId) {
        Election election = electionMapper.selectById(electionId);
        if (election == null) {
            return null;
        }

        Map<String, Object> result = new HashMap<>();
        result.put("electionId", electionId);
        result.put("title", election.getTitle());
        result.put("status", election.getStatus());

        // 获取候选人投票情况
        LambdaQueryWrapper<Candidate> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Candidate::getElectionId, electionId)
                .eq(Candidate::getStatus, 1)
                .orderByDesc(Candidate::getVoteCount);
        List<Candidate> candidates = candidateMapper.selectList(queryWrapper);

        List<Map<String, Object>> candidateList = candidates.stream().map(candidate -> {
            Map<String, Object> map = new HashMap<>();
            map.put("candidateId", candidate.getId());
            map.put("positionId", candidate.getPositionId());
            map.put("userId", candidate.getUserId());
            map.put("voteCount", candidate.getVoteCount());
            User user = userMapper.selectById(candidate.getUserId());
            if (user != null) {
                map.put("nickname", user.getNickname());
            }
            return map;
        }).collect(Collectors.toList());

        result.put("candidates", candidateList);

        // 统计总投票数
        LambdaQueryWrapper<VoteRecord> voteQueryWrapper = new LambdaQueryWrapper<>();
        voteQueryWrapper.eq(VoteRecord::getElectionId, electionId);
        Long totalVotes = voteRecordMapper.selectCount(voteQueryWrapper);
        result.put("totalVotes", totalVotes);

        return result;
    }

    /**
     * 获取我的投票记录
     */
    public List<Map<String, Object>> getMyVotes(Long userId, Long electionId) {
        LambdaQueryWrapper<VoteRecord> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(VoteRecord::getVoterId, userId);
        if (electionId != null) {
            queryWrapper.eq(VoteRecord::getElectionId, electionId);
        }
        queryWrapper.orderByDesc(VoteRecord::getCreateTime);

        List<VoteRecord> voteRecords = voteRecordMapper.selectList(queryWrapper);

        return voteRecords.stream().map(voteRecord -> {
            Map<String, Object> map = new HashMap<>();
            map.put("id", voteRecord.getId());
            map.put("electionId", voteRecord.getElectionId());
            map.put("candidateId", voteRecord.getCandidateId());
            map.put("createTime", voteRecord.getCreateTime());

            // 获取候选人信息
            Candidate candidate = candidateMapper.selectById(voteRecord.getCandidateId());
            if (candidate != null) {
                map.put("positionId", candidate.getPositionId());
            }

            return map;
        }).collect(Collectors.toList());
    }

    /**
     * 获取用户在某选举中的已投票数
     */
    public Map<String, Object> getVoteCount(Long electionId, Long userId) {
        LambdaQueryWrapper<VoteRecord> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(VoteRecord::getElectionId, electionId)
                .eq(VoteRecord::getVoterId, userId);
        Long votedCount = voteRecordMapper.selectCount(queryWrapper);

        Election election = electionMapper.selectById(electionId);
        Integer voteLimit = (election != null) ? election.getVoteLimit() : 1;

        Map<String, Object> result = new HashMap<>();
        result.put("votedCount", votedCount);
        result.put("voteLimit", voteLimit);
        return result;
    }
}
