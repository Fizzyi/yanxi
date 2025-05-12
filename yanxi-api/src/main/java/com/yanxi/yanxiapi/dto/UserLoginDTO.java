package com.yanxi.yanxiapi.dto;

import lombok.Data;

/**
 * 用户登录DTO
 */
@Data
public class UserLoginDTO {
    /**
     * 用户名
     */
    private String username;

    /**
     * 密码
     */
    private String password;

    /**
     * 用户类型：STUDENT-学生，TEACHER-教师
     */
    private String userType;
} 