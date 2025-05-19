package com.yanxi.yanxiapi.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

/**
 * 作业提交实体类
 */
@Data
@TableName("assignment_submission")
public class AssignmentSubmission {
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 作业ID
     */
    @TableField("assignment_id")
    private Long assignmentId;

    /**
     * 学生ID
     */
    @TableField("student_id")
    private Long studentId;

    /**
     * 上传文件地址
     */
    @TableField("file_url")
    private String fileUrl;

    /**
     * 提交时间
     */
    @TableField("submitted_at")
    private LocalDateTime submittedAt;

    /**
     * 老师反馈
     */
    @TableField("feedback")
    private String feedback;

    /**
     * 反馈时间
     */
    @TableField("feedback_time")
    private LocalDateTime feedbackTime;
} 