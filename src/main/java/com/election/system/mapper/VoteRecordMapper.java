package com.election.system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.election.system.entity.VoteRecord;
import org.apache.ibatis.annotations.Mapper;

/**
 * 投票记录Mapper
 */
@Mapper
public interface VoteRecordMapper extends BaseMapper<VoteRecord> {
}
