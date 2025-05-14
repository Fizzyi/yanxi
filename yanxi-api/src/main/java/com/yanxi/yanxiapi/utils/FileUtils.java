package com.yanxi.yanxiapi.utils;

import org.springframework.web.multipart.MultipartFile;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

public class FileUtils {
    
    // 文件上传的基础路径，可以根据需要修改
    private static final String UPLOAD_DIR = "uploads/assignments/";
    
    public static String saveFile(MultipartFile file) throws IOException {
        // 确保上传目录存在
        Path uploadPath = Paths.get(UPLOAD_DIR);
        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }
        
        // 生成唯一的文件名
        String originalFilename = file.getOriginalFilename();
        String extension = originalFilename.substring(originalFilename.lastIndexOf("."));
        String newFilename = UUID.randomUUID().toString() + extension;
        
        // 保存文件
        Path filePath = uploadPath.resolve(newFilename);
        Files.copy(file.getInputStream(), filePath);
        
        // 返回文件的相对路径
        return UPLOAD_DIR + newFilename;
    }
} 