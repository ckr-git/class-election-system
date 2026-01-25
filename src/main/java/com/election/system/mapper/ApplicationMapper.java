package com.election.system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.election.system.entity.Application;
import org.apache.ibatis.annotations.Mapper;

/**
 * 申请记录Mapper
 */
@Mapper
public interface ApplicationMapper extends BaseMapper<Application> {
}
