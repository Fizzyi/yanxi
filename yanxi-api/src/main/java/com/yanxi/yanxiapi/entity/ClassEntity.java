package com.yanxi.yanxiapi.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;
import java.util.Date;

@Data
@TableName("class")
public class ClassEntity {
    
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 班级名称
     */
    @TableField("name")
    private String name;

    /**
     * 班级代码
     */
    @TableField("code")
    private String code;

    /**
     * 教师ID
     */
    @TableField("teacher_id")
    private Long teacherId;

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
} 