package com.yanxi.yanxiapi.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.yanxi.yanxiapi.entity.Assignment;
import com.yanxi.yanxiapi.entity.User;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;

public interface AssignmentService extends IService<Assignment> {
    Assignment createAssignment(Long classId, String title, String description, MultipartFile file, LocalDateTime dueDate, User teacher);
} 