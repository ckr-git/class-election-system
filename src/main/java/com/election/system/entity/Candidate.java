package com.election.system.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 候选人实体类
 */
@Data
@TableName("candidate")
public class Candidate implements Serializable {
    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    private Long electionId;

    private Long positionId;

    private Long userId;

    private String slogan;

    private String intro;

    private String achievements;

    private String photo;

    private Integer voteCount;

    private Integer status;

    private String reviewOpinion;

    @TableLogic
    private Integer deleted;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
}
