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
     * 加入时间
     */
    @TableField(value = "joined_at", fill = FieldFill.INSERT)
    private Date joinedAt;
} 