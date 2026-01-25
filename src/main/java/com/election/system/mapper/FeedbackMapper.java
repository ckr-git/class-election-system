package com.election.system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.election.system.entity.Feedback;
import org.apache.ibatis.annotations.Mapper;

/**
 * 反馈意见Mapper
 */
@Mapper
public interface FeedbackMapper extends BaseMapper<Feedback> {
}
