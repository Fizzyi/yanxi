package com.yanxi.yanxiapi.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;
import java.util.Date;

/**
 * 用户实体类
 */
@Data
@TableName("user")
public class User {
    
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 用户名
     */
    @TableField("username")
    private String username;

    /**
     * 密码
     */
    @TableField("password")
    private String password;

    /**
     * 用户类型：STUDENT-学生，TEACHER-教师
     */
    @TableField(value = "role")
    private String userType;

    /**
     * 真实姓名
     */
    @TableField("real_name")
    private String realName;

    /**
     * 邮箱
     */
    @TableField("email")
    private String email;



    /**
     * 创建时间
     */
    @TableField(fill = FieldFill.INSERT)
    private Date createdAt;

    /**
     * 更新时间
     */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private Date updatedAt;

    @TableField(exist = false)
    private Boolean submitted;

    @TableField(exist = false)
    private LocalDateTime submittedAt;

    @TableField(exist = false)
    private Long submissionId;

    @TableField(exist = false)
    private String submissionFileUrl;
} 