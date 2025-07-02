package com.yanxi.yanxiapi.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yanxi.yanxiapi.entity.Assignment;
import com.yanxi.yanxiapi.entity.User;
import com.yanxi.yanxiapi.entity.AssignmentSubmission;
import com.yanxi.yanxiapi.mapper.AssignmentMapper;
import com.yanxi.yanxiapi.mapper.ClassStudentMapper;
import com.yanxi.yanxiapi.mapper.AssignmentSubmissionMapper;
import com.yanxi.yanxiapi.service.AssignmentService;
import com.yanxi.yanxiapi.service.UserService;
import com.yanxi.yanxiapi.service.ClassService;
import com.yanxi.yanxiapi.utils.FileUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import io.micrometer.core.annotation.Timed;

import java.time.LocalDateTime;
import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.Map;
import java.util.Objects;
import java.util.HashMap;
import java.util.concurrent.CompletableFuture;

import org.springframework.transaction.annotation.Transactional;
import org.springframework.cache.CacheManager;
import org.springframework.transaction.event.TransactionalEventListener;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.context.ApplicationEventPublisher;

@Service
@RequiredArgsConstructor
@Slf4j
public class AssignmentServiceImpl extends ServiceImpl<AssignmentMapper, Assignment> implements AssignmentService {

    private final ClassStudentMapper classStudentMapper;
    private final UserService userService;
    private final AssignmentSubmissionMapper assignmentSubmissionMapper;
    private final ClassService classService;
    private final CacheManager cacheManager;
    private final ApplicationEventPublisher eventPublisher;

    @Override
    @Timed(value = "assignment.get.assignments", description = "Time taken to get assignments")
    @Cacheable(value = "assignments", key = "'teacher_' + #teacher.id + '_class_' + (#classId ?: 'all') + '_student_' + (#studentEmail ?: 'all')")
    public List<Assignment> getAssignments(Long classId, String studentEmail, User teacher) {
        log.debug("Getting assignments for teacher: {}, classId: {}, studentEmail: {}", teacher.getId(), classId, studentEmail);
        
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

        List<Assignment> assignments = list(queryWrapper);
        
        // 批量获取班级名称 - 优化N+1查询问题
        List<Long> classIds = assignments.stream()
                .map(Assignment::getClassId)
                .distinct()
                .collect(Collectors.toList());
        
        Map<Long, String> classNamesMap = classService.getClassNamesMap(classIds);
        
        // 为每个作业添加 className 字段
        assignments.forEach(assignment -> {
            String className = classNamesMap.get(assignment.getClassId());
            assignment.setClassName(className != null ? className : "未知班级");
        });

        return assignments;
    }

    @Override
    @Timed(value = "assignment.create", description = "Time taken to create assignment")
    @CacheEvict(value = {"assignments", "classNames"}, allEntries = true)
    public Assignment createAssignment(Long classId, String title, String description, MultipartFile file, LocalDateTime dueDate, User teacher) {
        log.info("Creating assignment for class: {}, teacher: {}", classId, teacher.getId());
        
        try {
            Assignment assignment = new Assignment();
            assignment.setClassId(classId);
            assignment.setTitle(title);
            assignment.setDescription(description);
            assignment.setDueDate(dueDate);
            assignment.setTeacherId(teacher.getId());
            assignment.setCreatedAt(LocalDateTime.now());

            // Handle file upload if provided
            if (file != null && !file.isEmpty()) {
                String fileUrl = FileUtils.saveFile(file);
                assignment.setFileUrl(fileUrl);
            }

            save(assignment);
            log.info("Assignment created successfully with ID: {}", assignment.getId());
            return assignment;
        } catch (Exception e) {
            log.error("Failed to create assignment", e);
            throw new RuntimeException("创建作业失败: " + e.getMessage());
        }
    }



    @Override
    @Timed(value = "assignment.get.students", description = "Time taken to get assignment students")
    @Cacheable(value = "assignments", key = "'students_' + #assignmentId")
    public List<User> getAssignmentStudents(Long assignmentId) {
        log.debug("Getting students for assignment: {}", assignmentId);
        
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

        // 3. 批量获取所有提交记录 - 优化查询
        List<AssignmentSubmission> submissions = assignmentSubmissionMapper.selectByAssignmentId(assignmentId);
        Map<Long, AssignmentSubmission> submissionMap = submissions.stream()
                .collect(Collectors.toMap(AssignmentSubmission::getStudentId, submission -> submission));

        // 4. 批量获取学生详细信息 - 消除N+1查询问题
        List<User> students = classService.getUsersByIds(studentIds);
        
        // 5. 添加提交信息
        students.forEach(student -> {
            AssignmentSubmission submission = submissionMap.get(student.getId());
            if (submission != null) {
                student.setSubmitted(true);
                student.setSubmittedAt(submission.getSubmittedAt());
                student.setSubmissionId(submission.getId());
                student.setSubmissionFileUrl(submission.getFileUrl());
            } else {
                student.setSubmitted(false);
            }
        });

        return students;
    }

    @Override
    @Timed(value = "assignment.get.student.assignments", description = "Time taken to get student assignments")
    @Cacheable(value = "assignments", key = "'student_' + #student.id + '_submitted_' + (#submitted ?: 'all')")
    public List<Assignment> getStudentAssignments(Boolean submitted, User student) {
        log.debug("Getting assignments for student: {}, submitted filter: {}", student.getId(), submitted);
        
        // 1. 获取学生所在的所有班级ID
        List<Long> studentClassIds = classStudentMapper.selectClassIdsByStudentId(student.getId());
        if (studentClassIds.isEmpty()) {
            return Collections.emptyList();
        }

        // 2. 构建查询条件
        LambdaQueryWrapper<Assignment> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.in(Assignment::getClassId, studentClassIds);

        // 3. 获取所有符合条件的作业
        List<Assignment> assignments = list(queryWrapper);
        if (assignments.isEmpty()) {
            return Collections.emptyList();
        }

        // 4. 批量查询学生已提交的作业ID - 优化查询
        List<Long> submittedAssignmentIds = baseMapper.selectSubmittedAssignmentIds(student.getId());

        // 5. 为每个作业添加 submitted 字段
        assignments.forEach(assignment -> {
            assignment.setSubmitted(submittedAssignmentIds.contains(assignment.getId()));
        });

        // 6. 批量获取班级名称 - 优化N+1查询问题
        List<Long> classIds = assignments.stream()
                .map(Assignment::getClassId)
                .distinct()
                .collect(Collectors.toList());
        
        Map<Long, String> classNamesMap = classService.getClassNamesMap(classIds);
        
        // 7. 为每个作业添加 className 字段
        assignments.forEach(assignment -> {
            String className = classNamesMap.get(assignment.getClassId());
            assignment.setClassName(className != null ? className : "未知班级");
        });

        // 8. 如果指定了提交状态，进行过滤
        if (submitted != null) {
            assignments = assignments.stream()
                    .filter(assignment -> assignment.getSubmitted().equals(submitted))
                    .collect(Collectors.toList());
        }

        // 9. 按创建时间倒序排序
        assignments.sort((a1, a2) -> a2.getCreatedAt().compareTo(a1.getCreatedAt()));

        return assignments;
    }

    @Override
    @Transactional
    public Assignment submitAssignment(Long assignmentId, MultipartFile file, User student) {
        // 1. 获取作业信息
        Assignment assignment = getById(assignmentId);
        if (assignment == null) {
            throw new RuntimeException("作业不存在");
        }

        // 2. 检查学生是否在该作业所属的班级中
        List<Long> studentClassIds = classStudentMapper.selectClassIdsByStudentId(student.getId());
        if (!studentClassIds.contains(assignment.getClassId())) {
            throw new RuntimeException("您不在该作业所属的班级中");
        }

        // 3. 检查是否已经提交
        AssignmentSubmission existingSubmission = assignmentSubmissionMapper.selectByAssignmentIdAndStudentId(assignmentId, student.getId());
        if (existingSubmission != null) {
            throw new RuntimeException("您已经提交过该作业，如需修改请使用更新功能");
        }

        // 4. 检查是否已过截止时间
        if (assignment.getDueDate() != null && assignment.getDueDate().isBefore(LocalDateTime.now())) {
            throw new RuntimeException("作业已过截止时间，无法提交");
        }

        try {
            // 5. 保存文件
            String fileUrl = FileUtils.saveFile(file);

            // 6. 创建作业提交记录
            AssignmentSubmission submission = new AssignmentSubmission();
            submission.setAssignmentId(assignmentId);
            submission.setStudentId(student.getId());
            submission.setFileUrl(fileUrl);
            submission.setSubmittedAt(LocalDateTime.now());

            // 保存提交记录
            baseMapper.insertSubmission(submission);

            return assignment;
        } catch (IOException e) {
            throw new RuntimeException("文件上传失败: " + e.getMessage());
        }
    }

    @Override
    @Transactional
    public void unsubmitAssignment(Long assignmentId, User student) {
        // 1. 获取作业信息
        Assignment assignment = getById(assignmentId);
        if (assignment == null) {
            throw new RuntimeException("作业不存在");
        }

        // 2. 检查学生是否在该作业所属的班级中
        List<Long> studentClassIds = classStudentMapper.selectClassIdsByStudentId(student.getId());
        if (!studentClassIds.contains(assignment.getClassId())) {
            throw new RuntimeException("您不在该作业所属的班级中");
        }

        // 3. 检查是否已提交
        AssignmentSubmission existingSubmission = assignmentSubmissionMapper.selectByAssignmentIdAndStudentId(assignmentId, student.getId());
        if (existingSubmission == null) {
            throw new RuntimeException("您尚未提交该作业");
        }

        // 4. 删除提交记录
        int deletedRows = assignmentSubmissionMapper.deleteByAssignmentIdAndStudentId(assignmentId, student.getId());
        if (deletedRows == 0) {
            throw new RuntimeException("取消提交失败");
        }

        // 5. 可以选择删除文件（这里暂时保留文件，避免其他地方引用出错）
        // 如果需要删除文件，可以调用 FileUtils.deleteFile(existingSubmission.getFileUrl());
    }

    @Override
    @Transactional
    public Assignment updateSubmission(Long assignmentId, MultipartFile file, User student) {
        // 1. 获取作业信息
        Assignment assignment = getById(assignmentId);
        if (assignment == null) {
            throw new RuntimeException("作业不存在");
        }

        // 2. 检查学生是否在该作业所属的班级中
        List<Long> studentClassIds = classStudentMapper.selectClassIdsByStudentId(student.getId());
        if (!studentClassIds.contains(assignment.getClassId())) {
            throw new RuntimeException("您不在该作业所属的班级中");
        }

        // 3. 检查是否已提交
        AssignmentSubmission existingSubmission = assignmentSubmissionMapper.selectByAssignmentIdAndStudentId(assignmentId, student.getId());
        if (existingSubmission == null) {
            throw new RuntimeException("您尚未提交该作业，请先提交");
        }

        // 4. 检查是否已过截止时间
        if (assignment.getDueDate() != null && assignment.getDueDate().isBefore(LocalDateTime.now())) {
            throw new RuntimeException("作业已过截止时间，无法更新提交");
        }

        try {
            // 5. 保存新文件
            String newFileUrl = FileUtils.saveFile(file);

            // 6. 更新提交记录
            int updatedRows = assignmentSubmissionMapper.updateByAssignmentIdAndStudentId(
                assignmentId, student.getId(), newFileUrl, LocalDateTime.now());
            
            if (updatedRows == 0) {
                throw new RuntimeException("更新提交失败");
            }

            // 7. 可以选择删除旧文件（这里暂时保留旧文件，避免其他地方引用出错）
            // 如果需要删除旧文件，可以调用 FileUtils.deleteFile(existingSubmission.getFileUrl());

            return assignment;
        } catch (IOException e) {
            throw new RuntimeException("文件上传失败: " + e.getMessage());
        }
    }

    @Override
    public Map<String, Object> getSubmissionDetails(Long assignmentId, User student) {
        // 1. 获取作业信息
        Assignment assignment = getById(assignmentId);
        if (assignment == null) {
            throw new RuntimeException("作业不存在");
        }

        // 2. 检查学生是否在该作业所属的班级中
        List<Long> studentClassIds = classStudentMapper.selectClassIdsByStudentId(student.getId());
        if (!studentClassIds.contains(assignment.getClassId())) {
            throw new RuntimeException("您不在该作业所属的班级中");
        }

        // 3. 获取提交记录
        AssignmentSubmission submission = assignmentSubmissionMapper.selectByAssignmentIdAndStudentId(assignmentId, student.getId());
        if (submission == null) {
            throw new RuntimeException("您尚未提交该作业");
        }

        // 4. 构建返回数据
        Map<String, Object> result = new HashMap<>();
        result.put("assignmentId", assignmentId);
        result.put("assignmentTitle", assignment.getTitle());
        result.put("assignmentDescription", assignment.getDescription());
        result.put("submissionId", submission.getId());
        result.put("fileUrl", submission.getFileUrl());
        result.put("submittedAt", submission.getSubmittedAt());
        result.put("feedback", submission.getFeedback());
        result.put("feedbackTime", submission.getFeedbackTime());

        return result;
    }

    @Override
    public Map<String, Object> getStudentSubmissionForDownload(Long assignmentId, Long studentId, User teacher) {
        // 1. 获取作业信息
        Assignment assignment = getById(assignmentId);
        if (assignment == null) {
            throw new RuntimeException("作业不存在");
        }

        // 2. 检查教师权限：只有作业的创建者才能下载
        if (!assignment.getTeacherId().equals(teacher.getId())) {
            throw new RuntimeException("您没有权限下载此作业的提交");
        }

        // 3. 获取学生信息
        User student = userService.getById(studentId);
        if (student == null) {
            throw new RuntimeException("学生不存在");
        }

        // 4. 获取提交记录
        AssignmentSubmission submission = assignmentSubmissionMapper.selectByAssignmentIdAndStudentId(assignmentId, studentId);
        if (submission == null) {
            throw new RuntimeException("该学生尚未提交该作业");
        }

        // 5. 构建自定义文件名
        String originalFileName = submission.getFileUrl();
        String fileExtension = "";
        
        // 提取文件扩展名
        int lastDotIndex = originalFileName.lastIndexOf('.');
        if (lastDotIndex > 0) {
            fileExtension = originalFileName.substring(lastDotIndex);
        }
        
        // 清理学生姓名和作业名称，移除特殊字符
        String cleanStudentName = cleanFileName(student.getRealName() != null ? student.getRealName() : student.getUsername());
        String cleanAssignmentName = cleanFileName(assignment.getTitle());
        
        // 构建新文件名：学生姓名-作业名称.扩展名
        String customFileName = cleanStudentName + "-" + cleanAssignmentName + fileExtension;

        // 6. 构建返回数据
        Map<String, Object> result = new HashMap<>();
        result.put("fileUrl", submission.getFileUrl());
        result.put("fileName", customFileName);
        result.put("studentName", student.getRealName() != null ? student.getRealName() : student.getUsername());
        result.put("assignmentTitle", assignment.getTitle());

        return result;
    }

    @Override
    public Map<String, Object> getAssignmentFileForDownload(Long assignmentId, User student) {
        // 1. 获取作业信息
        Assignment assignment = getById(assignmentId);
        if (assignment == null) {
            throw new RuntimeException("作业不存在");
        }

        // 2. 检查学生是否有权限下载（学生必须在该作业所属的班级中）
        List<Long> studentClassIds = classStudentMapper.selectClassIdsByStudentId(student.getId());
        if (!studentClassIds.contains(assignment.getClassId())) {
            throw new RuntimeException("您没有权限下载此作业文件");
        }

        // 3. 检查作业是否有文件
        if (assignment.getFileUrl() == null || assignment.getFileUrl().trim().isEmpty()) {
            throw new RuntimeException("该作业没有附件文件");
        }

        // 4. 构建自定义文件名
        String originalFileName = assignment.getFileUrl();
        String fileExtension = "";
        
        // 提取文件扩展名
        int lastDotIndex = originalFileName.lastIndexOf('.');
        if (lastDotIndex > 0) {
            fileExtension = originalFileName.substring(lastDotIndex);
        }
        
        // 清理作业名称，移除特殊字符
        String cleanAssignmentName = cleanFileName(assignment.getTitle());
        
        // 构建新文件名：作业名称-instructions.扩展名
        String customFileName = cleanAssignmentName + "-instructions" + fileExtension;

        // 5. 构建返回数据
        Map<String, Object> result = new HashMap<>();
        result.put("fileUrl", assignment.getFileUrl());
        result.put("fileName", customFileName);
        result.put("assignmentTitle", assignment.getTitle());
        result.put("originalFileName", originalFileName);

        return result;
    }

    @Override
    @Transactional
    public void deleteAssignment(Long assignmentId, User teacher) {
        log.info("Deleting assignment: {} by teacher: {}", assignmentId, teacher.getId());
        
        // 1. 获取作业信息
        Assignment assignment = getById(assignmentId);
        if (assignment == null) {
            throw new RuntimeException("作业不存在");
        }
        
        // 2. 检查权限：只有作业的创建者（教师）才能删除
        if (!assignment.getTeacherId().equals(teacher.getId())) {
            throw new RuntimeException("您没有权限删除此作业");
        }
        
        // 3. 删除所有相关的作业提交记录
        int submissionsDeleted = assignmentSubmissionMapper.deleteByAssignmentId(assignmentId);
        log.info("Deleted {} assignment submissions for assignment {}", submissionsDeleted, assignmentId);
        
        // 4. 删除作业本身
        boolean deleted = removeById(assignmentId);
        log.info("Assignment {} deletion result: {}", assignmentId, deleted);
        
        if (!deleted) {
            throw new RuntimeException("删除作业失败");
        }
        
        // 5. Manually clear cache after transaction commits
        clearAssignmentCache();
        
        log.info("Assignment {} deleted successfully", assignmentId);
    }

    private void clearAssignmentCache() {
        try {
            if (cacheManager.getCache("assignments") != null) {
                cacheManager.getCache("assignments").clear();
                log.info("Assignments cache cleared manually");
            }
            if (cacheManager.getCache("classNames") != null) {
                cacheManager.getCache("classNames").clear();
                log.info("ClassNames cache cleared manually");
            }
        } catch (Exception e) {
            log.warn("Failed to clear cache manually: {}", e.getMessage());
        }
    }

    /**
     * 清理文件名，移除特殊字符
     * @param input 输入字符串
     * @return 清理后的字符串
     */
    private String cleanFileName(String input) {
        if (input == null) {
            return "Unknown";
        }
        // 移除或替换特殊字符，只保留字母、数字、中文字符、连字符和下划线
        return input.replaceAll("[^\\w\\u4e00-\\u9fa5\\-_]", "").trim();
    }
} 