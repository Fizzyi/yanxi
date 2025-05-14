package com.yanxi.yanxiapi.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;
import java.util.Date;

@Data
@TableName("class_student")
public class ClassStudent {
    
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 班级ID
     */
    @TableField("class_id")
    private Long classId;

    /**
     * 学生ID
     */
    @TableField("student_id")
    private Long studentId;

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