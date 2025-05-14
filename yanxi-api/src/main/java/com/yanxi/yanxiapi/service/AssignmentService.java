package com.yanxi.yanxiapi.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.yanxi.yanxiapi.entity.Assignment;
import com.yanxi.yanxiapi.entity.User;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.List;

public interface AssignmentService extends IService<Assignment> {
    /**
     * 创建作业
     */
    Assignment createAssignment(Long classId, String title, String description, MultipartFile file, LocalDateTime dueDate, User teacher);
    
    /**
     * 获取作业列表
     * @param classId 班级ID（可选）
     * @param studentEmail 学生邮箱（可选）
     * @param teacher 当前教师
     * @return 作业列表
     */
    List<Assignment> getAssignments(Long classId, String studentEmail, User teacher);
} 