package com.election.system.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.election.system.entity.Election;
import com.election.system.mapper.ElectionMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 选举服务
 */
@Service
public class ElectionService {

    @Autowired
    private ElectionMapper electionMapper;

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
     * 获取选举详情
     */
    public Election getElectionDetail(Long electionId) {
        return electionMapper.selectById(electionId);
    }
}
