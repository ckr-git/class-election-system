package com.election.system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.election.system.entity.Election;
import org.apache.ibatis.annotations.Mapper;

/**
 * 选举活动Mapper
 */
@Mapper
public interface ElectionMapper extends BaseMapper<Election> {
}
