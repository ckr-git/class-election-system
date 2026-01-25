package com.election.system.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.election.system.entity.Candidate;
import com.election.system.entity.Election;
import com.election.system.mapper.CandidateMapper;
import com.election.system.mapper.ElectionMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 管理员-选举管理服务
 */
@Service
public class AdminElectionService {

    @Autowired
    private ElectionMapper electionMapper;

    @Autowired
    private CandidateMapper candidateMapper;

    /**
     * 获取选举列表（分页）
     */
    public Page<Election> getElectionList(Integer status, Integer current, Integer size) {
        Page<Election> page = new Page<>(current, size);
        LambdaQueryWrapper<Election> queryWrapper = new LambdaQueryWrapper<>();
        
        if (status != null) {
            queryWrapper.eq(Election::getStatus, status);
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
     * 更新选举
     */
    public boolean updateElection(Election election) {
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
