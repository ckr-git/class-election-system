package com.election.system.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.election.system.common.BusinessException;
import com.election.system.common.XssUtil;
import com.election.system.entity.Candidate;
import com.election.system.entity.Election;
import com.election.system.entity.VoteRecord;
import com.election.system.mapper.CandidateMapper;
import com.election.system.mapper.ElectionMapper;
import com.election.system.mapper.VoteRecordMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

/**
 * 管理员-选举管理服务
 */
@Service
public class AdminElectionService {

    @Autowired
    private ElectionMapper electionMapper;

    @Autowired
    private CandidateMapper candidateMapper;

    @Autowired
    private VoteRecordMapper voteRecordMapper;

    /**
     * 获取选举列表（分页，支持关键词搜索）
     */
    public Page<Election> getElectionList(Integer status, String keyword, Integer current, Integer size) {
        Page<Election> page = new Page<>(current, size);
        LambdaQueryWrapper<Election> queryWrapper = new LambdaQueryWrapper<>();

        if (status != null) {
            queryWrapper.eq(Election::getStatus, status);
        }
        if (keyword != null && !keyword.trim().isEmpty()) {
            queryWrapper.like(Election::getTitle, keyword.trim());
        }
        queryWrapper.orderByDesc(Election::getCreateTime);

        return electionMapper.selectPage(page, queryWrapper);
    }

    /**
     * 创建选举
     */
    public boolean createElection(Election election, Long creatorId) {
        election.setCreatorId(creatorId);
        election.setStatus(0); // 未开始
        return electionMapper.insert(election) > 0;
    }

    /**
     * 更新选举（含业务校验）
     */
    public boolean updateElection(Election election) {
        Election existing = electionMapper.selectById(election.getId());
        if (existing == null) {
            throw new BusinessException(404, "选举不存在");
        }

        // 已结束选举禁止修改核心字段
        if (existing.getStatus() == 3) {
            throw new BusinessException(400, "已结束的选举不能修改");
        }

        // 合并时间字段（部分更新时用已有值填充）
        LocalDateTime applyStart = election.getApplyStartTime() != null ? election.getApplyStartTime() : existing.getApplyStartTime();
        LocalDateTime applyEnd = election.getApplyEndTime() != null ? election.getApplyEndTime() : existing.getApplyEndTime();
        LocalDateTime voteStart = election.getStartTime() != null ? election.getStartTime() : existing.getStartTime();
        LocalDateTime voteEnd = election.getEndTime() != null ? election.getEndTime() : existing.getEndTime();

        // 时间线校验
        if (applyStart != null && applyEnd != null && voteStart != null && voteEnd != null) {
            if (!applyStart.isBefore(applyEnd)) {
                throw new BusinessException(400, "报名开始时间必须早于报名结束时间");
            }
            if (applyEnd.isAfter(voteStart)) {
                throw new BusinessException(400, "报名结束时间不能晚于投票开始时间");
            }
            if (!voteStart.isBefore(voteEnd)) {
                throw new BusinessException(400, "投票开始时间必须早于投票结束时间");
            }
        }

        // 已有投票记录时禁止修改核心投票字段
        LambdaQueryWrapper<VoteRecord> voteQuery = new LambdaQueryWrapper<>();
        voteQuery.eq(VoteRecord::getElectionId, election.getId());
        Long voteCount = voteRecordMapper.selectCount(voteQuery);
        if (voteCount > 0) {
            boolean coreChanged = (election.getStartTime() != null && !election.getStartTime().equals(existing.getStartTime()))
                    || (election.getEndTime() != null && !election.getEndTime().equals(existing.getEndTime()))
                    || (election.getVoteLimit() != null && !election.getVoteLimit().equals(existing.getVoteLimit()));
            if (coreChanged) {
                throw new BusinessException(409, "已有投票记录，不能修改投票时间和投票限制");
            }
        }

        // XSS过滤
        if (election.getTitle() != null) {
            election.setTitle(XssUtil.clean(election.getTitle()));
        }
        if (election.getDescription() != null) {
            election.setDescription(XssUtil.clean(election.getDescription()));
        }

        return electionMapper.updateById(election) > 0;
    }

    /**
     * 删除选举
     */
    @Transactional(rollbackFor = Exception.class)
    public boolean deleteElection(Long electionId) {
        // 删除相关候选人
        LambdaQueryWrapper<Candidate> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Candidate::getElectionId, electionId);
        candidateMapper.delete(queryWrapper);
        
        return electionMapper.deleteById(electionId) > 0;
    }

    /**
     * 更改选举状态
     */
    public boolean changeElectionStatus(Long electionId, Integer status) {
        Election election = electionMapper.selectById(electionId);
        if (election == null) {
            return false;
        }
        election.setStatus(status);
        return electionMapper.updateById(election) > 0;
    }
}
