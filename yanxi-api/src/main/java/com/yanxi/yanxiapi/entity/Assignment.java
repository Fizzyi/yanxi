package com.yanxi.yanxiapi.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

/**
 * 作业实体类
 */
@Data
@TableName("assignment")
public class Assignment {
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 班级ID
     */
    @TableField("class_id")
    private Long classId;

    /**
     * 作业标题
     */
    @TableField("title")
    private String title;

    /**
     * 作业描述
     */
    @TableField("description")
    private String description;

    /**
     * 附件文件URL
     */
    @TableField("file_url")
    private String fileUrl;

    /**
     * 截止时间
     */
    @TableField("due_date")
    private LocalDateTime dueDate;

    /**
     * 教师ID
     */
    @TableField("teacher_id")
    private Long teacherId;

    /**
     * 创建时间
     */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;

    /**
     * 是否已提交（非数据库字段）
     */
    @TableField(exist = false)
    private Boolean submitted;

    /**
     * 班级名称（非数据库字段）
     */
    @TableField(exist = false)
    private String className;
} 