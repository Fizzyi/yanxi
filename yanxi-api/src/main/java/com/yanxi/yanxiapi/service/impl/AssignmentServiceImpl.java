package com.yanxi.yanxiapi.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yanxi.yanxiapi.entity.Assignment;
import com.yanxi.yanxiapi.entity.User;
import com.yanxi.yanxiapi.mapper.AssignmentMapper;
import com.yanxi.yanxiapi.service.AssignmentService;
import com.yanxi.yanxiapi.utils.FileUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.time.LocalDateTime;
import java.io.IOException;

@Service
@RequiredArgsConstructor
public class AssignmentServiceImpl extends ServiceImpl<AssignmentMapper, Assignment> implements AssignmentService {

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
} 