package com.election.system.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 选举活动实体类
 */
@Data
@TableName("election")
public class Election implements Serializable {
    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    private String title;

    private String description;

    private Long classId;

    private LocalDateTime startTime;

    private LocalDateTime endTime;

    private LocalDateTime applyStartTime;

    private LocalDateTime applyEndTime;

    private Integer status;

    private Integer voteLimit;

    private Long creatorId;

    @TableLogic
    private Integer deleted;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
}
