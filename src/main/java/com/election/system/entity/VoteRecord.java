package com.election.system.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 投票记录实体类
 */
@Data
@TableName("vote_record")
public class VoteRecord implements Serializable {
    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    private Long electionId;

    private Long voterId;

    private Long candidateId;

    private String ipAddress;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
}
