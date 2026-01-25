package com.election.system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.election.system.entity.Candidate;
import org.apache.ibatis.annotations.Mapper;

/**
 * 候选人Mapper
 */
@Mapper
public interface CandidateMapper extends BaseMapper<Candidate> {
}
