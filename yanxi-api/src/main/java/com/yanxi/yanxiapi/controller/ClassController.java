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
import java.util.List;
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
        List<ClassDTO> classDTOs = classStudents.stream()
                .map(cs -> convertToDTO(cs.getClassEntity()))
                .collect(Collectors.toList());
        return ResponseEntity.ok(classDTOs);
    }

    @PostMapping
    public ResponseEntity<ClassDTO> createClass(
            @RequestBody CreateClassRequest request,
            @AuthenticationPrincipal User teacher) {
        ClassEntity classEntity = classService.createClass(request.getName(), teacher);
        return ResponseEntity.ok(convertToDTO(classEntity));
    }

    @DeleteMapping("/{classId}")
    public ResponseEntity<Void> deleteClass(
            @PathVariable Long classId,
            @AuthenticationPrincipal User teacher) {
        ClassEntity classEntity = classService.getClassById(classId)
                .orElseThrow(() -> new IllegalArgumentException("Class not found"));
        
        if (!classEntity.getTeacher().getId().equals(teacher.getId())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        
        classService.deleteClass(classId);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/join")
    public ResponseEntity<ClassDTO> joinClass(
            @RequestBody JoinClassRequest request,
            @AuthenticationPrincipal User student) {
        ClassEntity classEntity = classService.getClassByCode(request.getCode())
                .orElseThrow(() -> new IllegalArgumentException("Invalid class code"));
        
        ClassStudent classStudent = classService.addStudentToClass(classEntity, student);
        return ResponseEntity.ok(convertToDTO(classStudent.getClassEntity()));
    }

    @DeleteMapping("/{classId}/students/{studentId}")
    public ResponseEntity<Void> removeStudent(
            @PathVariable Long classId,
            @PathVariable Long studentId,
            @AuthenticationPrincipal User teacher) {
        ClassEntity classEntity = classService.getClassById(classId)
                .orElseThrow(() -> new IllegalArgumentException("Class not found"));
        
        if (!classEntity.getTeacher().getId().equals(teacher.getId())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        
        User student = userService.getUserById(studentId)
                .orElseThrow(() -> new IllegalArgumentException("Student not found"));
        
        classService.removeStudentFromClass(classEntity, student);
        return ResponseEntity.ok().build();
    }

    private ClassDTO convertToDTO(ClassEntity classEntity) {
        ClassDTO dto = new ClassDTO();
        dto.setId(classEntity.getId());
        dto.setName(classEntity.getName());
        dto.setCode(classEntity.getCode());
        dto.setTeacherId(classEntity.getTeacher().getId());
        dto.setTeacherName(classEntity.getTeacher().getUsername());
        dto.setStudentCount(classEntity.getStudents().size());
        dto.setCreatedAt(classEntity.getCreatedAt().format(DATE_FORMATTER));
        return dto;
    }
} 