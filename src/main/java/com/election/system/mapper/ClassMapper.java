package com.election.system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.election.system.entity.ClassInfo;
import org.apache.ibatis.annotations.Mapper;

/**
 * 班级Mapper
 */
@Mapper
public interface ClassMapper extends BaseMapper<ClassInfo> {
}
