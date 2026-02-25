package com.election.system.dto;

import com.alibaba.excel.annotation.ExcelProperty;
import lombok.Data;

@Data
public class UserImportDTO {

    @ExcelProperty("学号")
    private String username;

    @ExcelProperty("姓名")
    private String nickname;

    @ExcelProperty("班级ID")
    private Long classId;

    @ExcelProperty("手机号")
    private String phone;

    @ExcelProperty("邮箱")
    private String email;
}
