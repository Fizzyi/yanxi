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

    /**
     * 获取作业的学生列表
     * @param assignmentId 作业ID
     * @return 学生列表
     */
    List<User> getAssignmentStudents(Long assignmentId);

    /**
     * 获取当前学生的作业列表
     * @param submitted 是否已提交（可选）
     * @param student 当前登录的学生
     * @return 作业列表
     */
    List<Assignment> getStudentAssignments(Boolean submitted, User student);

    /**
     * 学生提交作业
     * @param assignmentId 作业ID
     * @param file 作业文件
     * @param student 当前登录的学生
     * @return 更新后的作业信息
     */
    Assignment submitAssignment(Long assignmentId, MultipartFile file, User student);
} 