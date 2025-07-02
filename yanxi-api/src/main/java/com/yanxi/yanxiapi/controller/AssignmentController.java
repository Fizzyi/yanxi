package com.yanxi.yanxiapi.controller;

import com.yanxi.yanxiapi.entity.Assignment;
import com.yanxi.yanxiapi.entity.User;
import com.yanxi.yanxiapi.service.AssignmentService;
import com.yanxi.yanxiapi.service.FileService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import io.micrometer.core.annotation.Timed;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Positive;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

@RestController
@RequestMapping("/api/assignments")
@RequiredArgsConstructor
@Validated
@Slf4j
public class AssignmentController {
    private final AssignmentService assignmentService;
    private final FileService fileService;

    @GetMapping
    @Timed(value = "controller.assignment.get", description = "Time taken to get assignments")
    public ResponseEntity<List<Assignment>> getAssignments(
            @RequestParam(name = "classId", required = false) @Positive Long classId,
            @RequestParam(name = "studentEmail", required = false) String studentEmail,
            @AuthenticationPrincipal User teacher) {
        
        log.debug("Getting assignments for teacher: {}, classId: {}, studentEmail: {}", 
                 teacher.getId(), classId, studentEmail);
        
        try {
            List<Assignment> assignments = assignmentService.getAssignments(classId, studentEmail, teacher);
            return ResponseEntity.ok(assignments);
        } catch (Exception e) {
            log.error("Error getting assignments", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     *  创建作业
     * @param classId 班级ID
     * @param title 作业标题
     * @param description 作业描述
     * @param file 作业文件
     * @param dueDate 截止时间
     * @param teacher 当前登录教师
     * @return 创建的作业信息
     */
    @PostMapping
    @Timed(value = "controller.assignment.create", description = "Time taken to create assignment")
    public ResponseEntity<?> createAssignment(
            @RequestParam("classId") @NotNull @Positive Long classId,
            @RequestParam("title") @NotBlank String title,
            @RequestParam("description") String description,
            @RequestParam("file") @NotNull MultipartFile file,
            @RequestParam("dueDate") @NotBlank String dueDate,
            @AuthenticationPrincipal User teacher) {
        
        log.info("Creating assignment for class: {}, teacher: {}", classId, teacher.getId());
        
        try {
            // 验证文件大小和类型
            if (file.isEmpty()) {
                return ResponseEntity.badRequest()
                    .body(Map.of("error", "文件不能为空"));
            }
            
            if (file.getSize() > 10 * 1024 * 1024) { // 10MB
                return ResponseEntity.badRequest()
                    .body(Map.of("error", "文件大小不能超过10MB"));
            }
            
            LocalDateTime dueDateTime = LocalDateTime.parse(dueDate, DateTimeFormatter.ISO_LOCAL_DATE_TIME);
            Assignment assignment = assignmentService.createAssignment(classId, title, description, file, dueDateTime, teacher);
            
            log.info("Assignment created successfully with ID: {}", assignment.getId());
            return ResponseEntity.ok(assignment);
        } catch (Exception e) {
            log.error("Error creating assignment", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("error", "创建作业失败: " + e.getMessage()));
        }
    }

    @GetMapping("/download")
    @Timed(value = "controller.assignment.download", description = "Time taken to download file")
    public ResponseEntity<Resource> downloadFile(@RequestParam @NotBlank String fileUrl) {
        log.debug("Downloading file: {}", fileUrl);
        
        try {
            Resource resource = fileService.loadFileAsResource(fileUrl);
            
            String contentType = "application/octet-stream";
            String headerValue = "attachment; filename=\"" + resource.getFilename() + "\"";

            return ResponseEntity.ok()
                    .contentType(MediaType.parseMediaType(contentType))
                    .header(HttpHeaders.CONTENT_DISPOSITION, headerValue)
                    .body(resource);
        } catch (IOException e) {
            log.error("Error downloading file: {}", fileUrl, e);
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * 教师下载学生作业提交（带有学生姓名和作业名称的文件名）
     * @param assignmentId 作业ID
     * @param studentId 学生ID
     * @param teacher 当前登录的教师
     * @return 文件资源
     */
    @GetMapping("/{assignmentId}/download/{studentId}")
    @Timed(value = "controller.assignment.download.student", description = "Time taken to download student submission")
    public ResponseEntity<Resource> downloadStudentSubmission(
            @PathVariable @NotNull @Positive Long assignmentId,
            @PathVariable @NotNull @Positive Long studentId,
            @AuthenticationPrincipal User teacher) {
        
        log.debug("Teacher {} downloading submission for assignment: {}, student: {}", 
                 teacher.getId(), assignmentId, studentId);
        
        try {
            Map<String, Object> downloadInfo = assignmentService.getStudentSubmissionForDownload(assignmentId, studentId, teacher);
            
            Resource resource = fileService.loadFileAsResource((String) downloadInfo.get("fileUrl"));
            String customFileName = (String) downloadInfo.get("fileName");
            
            String contentType = "application/octet-stream";
            String headerValue = "attachment; filename=\"" + customFileName + "\"";

            return ResponseEntity.ok()
                    .contentType(MediaType.parseMediaType(contentType))
                    .header(HttpHeaders.CONTENT_DISPOSITION, headerValue)
                    .body(resource);
        } catch (Exception e) {
            log.error("Error downloading student submission", e);
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * 学生下载作业文件（带有作业名称-instructions的文件名）
     * @param assignmentId 作业ID
     * @param student 当前登录的学生
     * @return 文件资源
     */
    @GetMapping("/{assignmentId}/download")
    @Timed(value = "controller.assignment.download.instructions", description = "Time taken to download assignment instructions")
    public ResponseEntity<Resource> downloadAssignmentFile(
            @PathVariable @NotNull @Positive Long assignmentId,
            @AuthenticationPrincipal User student) {
        
        log.debug("Student {} downloading assignment file: {}", student.getId(), assignmentId);
        
        try {
            Map<String, Object> downloadInfo = assignmentService.getAssignmentFileForDownload(assignmentId, student);
            
            Resource resource = fileService.loadFileAsResource((String) downloadInfo.get("fileUrl"));
            String customFileName = (String) downloadInfo.get("fileName");
            
            String contentType = "application/octet-stream";
            String headerValue = "attachment; filename=\"" + customFileName + "\"";

            return ResponseEntity.ok()
                    .contentType(MediaType.parseMediaType(contentType))
                    .header(HttpHeaders.CONTENT_DISPOSITION, headerValue)
                    .body(resource);
        } catch (Exception e) {
            log.error("Error downloading assignment file", e);
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * 获取作业的学生列表
     * @param assignmentId 作业ID
     * @return 学生列表
     */
    @GetMapping("/{assignmentId}/students")
    @Timed(value = "controller.assignment.get.students", description = "Time taken to get assignment students")
    public ResponseEntity<List<User>> getAssignmentStudents(
            @PathVariable @NotNull @Positive Long assignmentId) {
        
        log.debug("Getting students for assignment: {}", assignmentId);
        
        try {
            List<User> students = assignmentService.getAssignmentStudents(assignmentId);
            return ResponseEntity.ok(students);
        } catch (Exception e) {
            log.error("Error getting assignment students", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * 获取当前学生的作业列表
     * @param submitted 是否已提交（可选）
     * @param student 当前登录的学生
     * @return 作业列表
     */
    @GetMapping("/student")
    @Timed(value = "controller.assignment.get.student", description = "Time taken to get student assignments")
    public ResponseEntity<List<Assignment>> getStudentAssignments(
            @RequestParam(value = "submitted", required = false) Boolean submitted,
            @AuthenticationPrincipal User student) {
        
        log.debug("Getting assignments for student: {}, submitted filter: {}", student.getId(), submitted);
        
        try {
            List<Assignment> assignments = assignmentService.getStudentAssignments(submitted, student);
            return ResponseEntity.ok(assignments);
        } catch (Exception e) {
            log.error("Error getting student assignments", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * 学生提交作业
     * @param assignmentId 作业ID
     * @param file 作业文件
     * @param student 当前登录的学生
     * @return 更新后的作业信息
     */
    @PostMapping("/{assignmentId}/submit")
    @Timed(value = "controller.assignment.submit", description = "Time taken to submit assignment")
    public ResponseEntity<?> submitAssignment(
            @PathVariable @NotNull @Positive Long assignmentId,
            @RequestParam("file") @NotNull MultipartFile file,
            @AuthenticationPrincipal User student) {
        
        log.info("Student {} submitting assignment: {}", student.getId(), assignmentId);
        
        try {
            // 验证文件
            if (file.isEmpty()) {
                return ResponseEntity.badRequest()
                    .body(Map.of("error", "文件不能为空"));
            }
            
            if (file.getSize() > 10 * 1024 * 1024) { // 10MB
                return ResponseEntity.badRequest()
                    .body(Map.of("error", "文件大小不能超过10MB"));
            }
            
            Assignment assignment = assignmentService.submitAssignment(assignmentId, file, student);
            log.info("Assignment submitted successfully by student: {}", student.getId());
            return ResponseEntity.ok(assignment);
        } catch (Exception e) {
            log.error("Error submitting assignment", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("error", "提交作业失败: " + e.getMessage()));
        }
    }

    /**
     * 学生取消提交作业
     * @param assignmentId 作业ID
     * @param student 当前登录的学生
     * @return 操作结果
     */
    @DeleteMapping("/{assignmentId}/unsubmit")
    @Timed(value = "controller.assignment.unsubmit", description = "Time taken to unsubmit assignment")
    public ResponseEntity<?> unsubmitAssignment(
            @PathVariable @NotNull @Positive Long assignmentId,
            @AuthenticationPrincipal User student) {
        
        log.info("Student {} unsubmitting assignment: {}", student.getId(), assignmentId);
        
        try {
            assignmentService.unsubmitAssignment(assignmentId, student);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            log.error("Error unsubmitting assignment", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("error", "取消提交失败: " + e.getMessage()));
        }
    }

    /**
     * 学生更新作业提交
     * @param assignmentId 作业ID
     * @param file 新的作业文件
     * @param student 当前登录的学生
     * @return 更新后的作业信息
     */
    @PutMapping("/{assignmentId}/submit")
    @Timed(value = "controller.assignment.update.submit", description = "Time taken to update assignment submission")
    public ResponseEntity<?> updateSubmission(
            @PathVariable @NotNull @Positive Long assignmentId,
            @RequestParam("file") @NotNull MultipartFile file,
            @AuthenticationPrincipal User student) {
        
        log.info("Student {} updating submission for assignment: {}", student.getId(), assignmentId);
        
        try {
            // 验证文件
            if (file.isEmpty()) {
                return ResponseEntity.badRequest()
                    .body(Map.of("error", "文件不能为空"));
            }
            
            if (file.getSize() > 10 * 1024 * 1024) { // 10MB
                return ResponseEntity.badRequest()
                    .body(Map.of("error", "文件大小不能超过10MB"));
            }
            
            Assignment assignment = assignmentService.updateSubmission(assignmentId, file, student);
            log.info("Assignment submission updated successfully by student: {}", student.getId());
            return ResponseEntity.ok(assignment);
        } catch (Exception e) {
            log.error("Error updating assignment submission", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("error", "更新提交失败: " + e.getMessage()));
        }
    }

    /**
     * 获取学生的作业提交详情
     * @param assignmentId 作业ID
     * @param student 当前登录的学生
     * @return 提交详情
     */
    @GetMapping("/{assignmentId}/submission")
    public ResponseEntity<?> getSubmissionDetails(
            @PathVariable Long assignmentId,
            @AuthenticationPrincipal User student) {
        try {
            Map<String, Object> submissionDetails = assignmentService.getSubmissionDetails(assignmentId, student);
            return ResponseEntity.ok(submissionDetails);
        } catch (Exception e) {
            Map<String, String> response = new HashMap<>();
            response.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    /**
     * 删除作业
     * @param assignmentId 作业ID
     * @param teacher 当前登录的教师
     * @return 删除结果
     */
    @DeleteMapping("/{assignmentId}")
    public ResponseEntity<Void> deleteAssignment(
            @PathVariable Long assignmentId,
            @AuthenticationPrincipal User teacher) {
        try {
            assignmentService.deleteAssignment(assignmentId, teacher);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            log.error("Error deleting assignment: {}", assignmentId, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
} 