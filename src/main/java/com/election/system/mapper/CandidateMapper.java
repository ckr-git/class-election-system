package com.election.system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.election.system.entity.Candidate;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Update;

/**
 * 候选人Mapper
 */
@Mapper
public interface CandidateMapper extends BaseMapper<Candidate> {

    /**
     * 原子更新投票数
     */
    @Update("UPDATE candidate SET vote_count = vote_count + 1 WHERE id = #{candidateId}")
    int incrementVoteCount(Long candidateId);
}
