package com.election.system.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 申请记录实体类
 */
@Data
@TableName("application")
public class Application implements Serializable {
    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    private Long electionId;

    private Long positionId;

    private Long userId;

    private String reason;

    private Integer status;

    private String reviewOpinion;

    private Long reviewerId;

    private LocalDateTime reviewTime;

    @TableLogic
    private Integer deleted;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
}
