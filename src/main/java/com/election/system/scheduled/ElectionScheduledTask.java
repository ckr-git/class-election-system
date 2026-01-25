package com.election.system.scheduled;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.election.system.entity.Election;
import com.election.system.mapper.ElectionMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 选举定时任务
 */
@Component
public class ElectionScheduledTask {

    private static final Logger logger = LoggerFactory.getLogger(ElectionScheduledTask.class);

    @Autowired
    private ElectionMapper electionMapper;

    /**
     * 每5分钟检查一次选举状态
     * 自动开始报名、开始投票、结束投票
     */
    @Scheduled(cron = "0 */5 * * * ?")
    public void updateElectionStatus() {
        logger.info("开始执行选举状态更新任务");

        LocalDateTime now = LocalDateTime.now();

        // 1. 检查并开始报名（状态从0变为1）
        LambdaQueryWrapper<Election> startApplyWrapper = new LambdaQueryWrapper<>();
        startApplyWrapper.eq(Election::getStatus, 0)
                .le(Election::getApplyStartTime, now)
                .gt(Election::getApplyEndTime, now);
        List<Election> toStartApply = electionMapper.selectList(startApplyWrapper);
        
        for (Election election : toStartApply) {
            election.setStatus(1);
            electionMapper.updateById(election);
            logger.info("选举{}进入报名阶段", election.getId());
        }

        // 2. 检查并开始投票（状态从1变为2）
        LambdaQueryWrapper<Election> startVoteWrapper = new LambdaQueryWrapper<>();
        startVoteWrapper.eq(Election::getStatus, 1)
                .le(Election::getStartTime, now)
                .gt(Election::getEndTime, now);
        List<Election> toStartVote = electionMapper.selectList(startVoteWrapper);
        
        for (Election election : toStartVote) {
            election.setStatus(2);
            electionMapper.updateById(election);
            logger.info("选举{}进入投票阶段", election.getId());
        }

        // 3. 检查并结束投票（状态从2变为3）
        LambdaQueryWrapper<Election> endVoteWrapper = new LambdaQueryWrapper<>();
        endVoteWrapper.eq(Election::getStatus, 2)
                .le(Election::getEndTime, now);
        List<Election> toEndVote = electionMapper.selectList(endVoteWrapper);
        
        for (Election election : toEndVote) {
            election.setStatus(3);
            electionMapper.updateById(election);
            logger.info("选举{}已结束", election.getId());
        }

        logger.info("选举状态更新任务执行完成，开始报名:{}个，开始投票:{}个，结束投票:{}个",
                toStartApply.size(), toStartVote.size(), toEndVote.size());
    }

    /**
     * 每天凌晨2点执行数据统计任务
     */
    @Scheduled(cron = "0 0 2 * * ?")
    public void dailyStatistics() {
        logger.info("开始执行每日数据统计任务");
        // 这里可以添加统计逻辑，如生成报表等
        logger.info("每日数据统计任务执行完成");
    }
}
