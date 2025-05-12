package com.yanxi.yanxiapi.entity;


import lombok.Data;

import javax.persistence.*;

/**
 * 用户实体类
 */
@Data
@Entity
@Table(name = "users")
public class User {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 用户名
     */
    @Column(nullable = false, unique = true)
    private String username;

    /**
     * 密码
     */
    @Column(nullable = false)
    private String password;

    /**
     * 用户类型：STUDENT-学生，TEACHER-教师
     */
    @Column(nullable = false)
    private String userType;

    /**
     * 真实姓名
     */
    private String realName;

    /**
     * 邮箱
     */
    private String email;

    /**
     * 手机号
     */
    private String phone;

    /**
     * 创建时间
     */
    @Column(nullable = false)
    private Long createTime;

    /**
     * 更新时间
     */
    @Column(nullable = false)
    private Long updateTime;
} 