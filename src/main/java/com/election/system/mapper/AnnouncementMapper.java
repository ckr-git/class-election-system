package com.election.system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.election.system.entity.Announcement;
import org.apache.ibatis.annotations.Mapper;

/**
 * 公告Mapper
 */
@Mapper
public interface AnnouncementMapper extends BaseMapper<Announcement> {
}
