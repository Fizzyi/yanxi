package com.yanxi.yanxiapi.controller;

import com.yanxi.yanxiapi.entity.Assignment;
import com.yanxi.yanxiapi.entity.User;
import com.yanxi.yanxiapi.service.AssignmentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@RestController
@RequestMapping("/api/assignments")
@RequiredArgsConstructor
public class AssignmentController {
    private final AssignmentService assignmentService;

    @GetMapping
    public ResponseEntity<List<Assignment>> getAssignments(
            @RequestParam(required = false) Long classId,
            @RequestParam(required = false) String studentEmail,
            @AuthenticationPrincipal User teacher) {
        List<Assignment> assignments = assignmentService.getAssignments(classId, studentEmail, teacher);
        return ResponseEntity.ok(assignments);
    }

    @PostMapping
    public ResponseEntity<Assignment> createAssignment(
            @RequestParam("classId") Long classId,
            @RequestParam("title") String title,
            @RequestParam("description") String description,
            @RequestParam("file") MultipartFile file,
            @RequestParam("dueDate") String dueDate,
            @AuthenticationPrincipal User teacher) {
        
        LocalDateTime dueDateTime = LocalDateTime.parse(dueDate, DateTimeFormatter.ISO_LOCAL_DATE_TIME);
        Assignment assignment = assignmentService.createAssignment(classId, title, description, file, dueDateTime, teacher);
        return ResponseEntity.ok(assignment);
    }
} 