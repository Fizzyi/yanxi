package com.yanxi.yanxiapi.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yanxi.yanxiapi.entity.Assignment;
import com.yanxi.yanxiapi.entity.User;
import com.yanxi.yanxiapi.mapper.AssignmentMapper;
import com.yanxi.yanxiapi.mapper.ClassStudentMapper;
import com.yanxi.yanxiapi.service.AssignmentService;
import com.yanxi.yanxiapi.service.UserService;
import com.yanxi.yanxiapi.utils.FileUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.time.LocalDateTime;
import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AssignmentServiceImpl extends ServiceImpl<AssignmentMapper, Assignment> implements AssignmentService {

    private final ClassStudentMapper classStudentMapper;
    private final UserService userService;

    @Override
    public List<Assignment> getAssignments(Long classId, String studentEmail, User teacher) {
        LambdaQueryWrapper<Assignment> queryWrapper = new LambdaQueryWrapper<>();
        
        // 只查询当前教师的作业
        queryWrapper.eq(Assignment::getTeacherId, teacher.getId());
        
        // 如果指定了班级，添加班级筛选条件
        if (classId != null) {
            queryWrapper.eq(Assignment::getClassId, classId);
        }
        
        // 如果指定了学生邮箱，需要关联查询该学生所在班级的作业
        if (studentEmail != null) {
            // 通过邮箱查找学生
            User student = userService.getByEmail(studentEmail);
            if (student != null) {
                // 获取学生所在的所有班级ID
                List<Long> studentClassIds = classStudentMapper.selectClassIdsByStudentId(student.getId());
                if (!studentClassIds.isEmpty()) {
                    queryWrapper.in(Assignment::getClassId, studentClassIds);
                } else {
                    // 如果学生不在任何班级，返回空列表
                    return Collections.emptyList();
                }
            }
        }
        
        // 按创建时间倒序排序
        queryWrapper.orderByDesc(Assignment::getCreatedAt);
        
        return list(queryWrapper);
    }

    @Override
    public Assignment createAssignment(Long classId, String title, String description, MultipartFile file, LocalDateTime dueDate, User teacher) {
        try {
            // 保存文件并获取文件路径
            String fileUrl = FileUtils.saveFile(file);
            
            Assignment assignment = new Assignment();
            assignment.setClassId(classId);
            assignment.setTitle(title);
            assignment.setDescription(description);
            assignment.setFileUrl(fileUrl);
            assignment.setDueDate(dueDate);
            assignment.setTeacherId(teacher.getId());
            assignment.setCreatedAt(LocalDateTime.now());
            assignment.setUpdatedAt(LocalDateTime.now());
            
            save(assignment);
            return assignment;
        } catch (IOException e) {
            throw new RuntimeException("文件上传失败: " + e.getMessage());
        }
    }

    @Override
    public List<User> getAssignmentStudents(Long assignmentId) {
        // 1. 获取作业信息
        Assignment assignment = getById(assignmentId);
        if (assignment == null) {
            throw new RuntimeException("作业不存在");
        }

        // 2. 获取该作业所属班级的所有学生ID
        List<Long> studentIds = classStudentMapper.selectStudentIdsByClassId(assignment.getClassId());
        if (studentIds.isEmpty()) {
            return Collections.emptyList();
        }

        // 3. 获取学生详细信息
        return studentIds.stream()
                .map(userService::getById)
                .collect(Collectors.toList());
    }

    @Override
    public List<Assignment> getStudentAssignments(Boolean submitted, User student) {
        // 1. 获取学生所在的所有班级ID
        List<Long> studentClassIds = classStudentMapper.selectClassIdsByStudentId(student.getId());
        if (studentClassIds.isEmpty()) {
            return Collections.emptyList();
        }

        // 2. 构建查询条件
        LambdaQueryWrapper<Assignment> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.in(Assignment::getClassId, studentClassIds);
        
        // 3. 如果指定了提交状态，需要关联查询作业提交表
        if (submitted != null) {
            // 获取所有作业ID
            List<Assignment> allAssignments = list(queryWrapper);
            List<Long> assignmentIds = allAssignments.stream()
                    .map(Assignment::getId)
                    .collect(Collectors.toList());
            
            if (assignmentIds.isEmpty()) {
                return Collections.emptyList();
            }
            
            // 查询学生已提交的作业ID
            List<Long> submittedAssignmentIds = baseMapper.selectSubmittedAssignmentIds(student.getId(), assignmentIds);
            
            if (submitted) {
                // 只返回已提交的作业
                queryWrapper.in(Assignment::getId, submittedAssignmentIds);
            } else {
                // 只返回未提交的作业
                queryWrapper.notIn(Assignment::getId, submittedAssignmentIds);
            }
        }
        
        // 4. 按创建时间倒序排序
        queryWrapper.orderByDesc(Assignment::getCreatedAt);
        
        return list(queryWrapper);
    }
} 