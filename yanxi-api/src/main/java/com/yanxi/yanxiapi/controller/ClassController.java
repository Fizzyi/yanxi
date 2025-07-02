package com.yanxi.yanxiapi.controller;

import com.yanxi.yanxiapi.dto.ClassDTO;
import com.yanxi.yanxiapi.dto.CreateClassRequest;
import com.yanxi.yanxiapi.dto.JoinClassRequest;
import com.yanxi.yanxiapi.entity.ClassEntity;
import com.yanxi.yanxiapi.entity.ClassStudent;
import com.yanxi.yanxiapi.entity.User;
import com.yanxi.yanxiapi.service.ClassService;
import com.yanxi.yanxiapi.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/classes")
@RequiredArgsConstructor
public class ClassController {
    private final ClassService classService;
    private final UserService userService;
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @GetMapping("/teacher")
    public ResponseEntity<List<ClassDTO>> getTeacherClasses(@AuthenticationPrincipal User teacher) {
        List<ClassEntity> classes = classService.getClassesByTeacher(teacher);
        List<ClassDTO> classDTOs = classes.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(classDTOs);
    }

    @GetMapping("/student")
    public ResponseEntity<List<ClassDTO>> getStudentClasses(@AuthenticationPrincipal User student) {
        List<ClassStudent> classStudents = classService.getClassesByStudent(student);
        
        if (classStudents.isEmpty()) {
            return ResponseEntity.ok(Collections.emptyList());
        }
        
        // 批量获取班级信息 - 优化N+1查询问题
        List<Long> classIds = classStudents.stream()
                .map(ClassStudent::getClassId)
                .collect(Collectors.toList());
        
        List<ClassEntity> classes = classService.getClassesByIds(classIds);
        
        // 转换为DTO
        List<ClassDTO> classDTOs = classes.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
                
        return ResponseEntity.ok(classDTOs);
    }

    @PostMapping
    public ResponseEntity<?> createClass(
            @RequestBody CreateClassRequest request,
            @AuthenticationPrincipal User teacher) {
        try {
            System.out.println("DEBUG: Create class request - Name: " + request.getName());
            System.out.println("DEBUG: Teacher from @AuthenticationPrincipal: " + teacher);
            
            if (teacher == null) {
                System.out.println("DEBUG: Teacher is null, authentication failed");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "Authentication failed"));
            }
            
            System.out.println("DEBUG: Teacher ID: " + teacher.getId() + ", Username: " + teacher.getUsername());
            
            ClassEntity classEntity = classService.createClass(request.getName(), teacher);
            System.out.println("DEBUG: Class created successfully - ID: " + classEntity.getId());
            
            return ResponseEntity.ok(convertToDTO(classEntity));
        } catch (Exception e) {
            System.out.println("DEBUG: Error creating class: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("error", "Failed to create class: " + e.getMessage()));
        }
    }

    @DeleteMapping("/{classId}")
    public ResponseEntity<Void> deleteClass(
            @PathVariable Long classId,
            @AuthenticationPrincipal User teacher) {
        ClassEntity classEntity = classService.getClassById(classId)
                .orElseThrow(() -> new IllegalArgumentException("Class not found"));

        if (!classEntity.getTeacherId().equals(teacher.getId())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        classService.deleteClass(classId);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/join")
    public ResponseEntity<ClassDTO> joinClass(
            @RequestBody JoinClassRequest request,
            @AuthenticationPrincipal User student) {
        Optional<ClassEntity> classByCode = classService.getClassByCode(request.getCode());
        if (classByCode.isPresent()) {
            ClassEntity classEntity = classByCode.get();
            ClassStudent classStudent = classService.addStudentToClass(classEntity, student);
            return ResponseEntity.ok(convertToDTO(classEntity));
        } else {
            return ResponseEntity.badRequest().build();
        }
    }

    @DeleteMapping("/{classId}/students/{studentId}")
    public ResponseEntity<Void> removeStudent(
            @PathVariable Long classId,
            @PathVariable Long studentId,
            @AuthenticationPrincipal User teacher) {
        ClassEntity classEntity = classService.getClassById(classId)
                .orElseThrow(() -> new IllegalArgumentException("Class not found"));

        if (!classEntity.getTeacherId().equals(teacher.getId())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        User student = userService.getUserById(studentId)
                .orElseThrow(() -> new IllegalArgumentException("Student not found"));

        classService.removeStudentFromClass(classEntity, student);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/teacher/students")
    public ResponseEntity<List<User>> getTeacherAllStudents(
            @AuthenticationPrincipal User teacher,
            @RequestParam(value = "email", required = false) String email,
            @RequestParam(value = "classId", required = false) Long classId) {
        List<User> students = classService.getTeacherAllStudents(teacher, email, classId);
        return ResponseEntity.ok(students);
    }

    private ClassDTO convertToDTO(ClassEntity classEntity) {
        ClassDTO dto = new ClassDTO();
        dto.setId(classEntity.getId());
        dto.setName(classEntity.getName());
        dto.setCode(classEntity.getCode());
        dto.setTeacherId(classEntity.getTeacherId());
        // 获取教师信息
        User teacher = userService.getUserById(classEntity.getTeacherId())
                .orElseThrow(() -> new IllegalArgumentException("Teacher not found"));
        dto.setTeacherName(teacher.getUsername());
        // 获取学生数量
        dto.setStudentCount(classService.getStudentCount(classEntity.getId()));
//        dto.setCreatedAt(classEntity.getCreatedAt().format(DATE_FORMATTER));
        return dto;
    }
} 