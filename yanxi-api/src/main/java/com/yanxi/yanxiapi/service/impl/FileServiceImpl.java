package com.yanxi.yanxiapi.service.impl;

import com.yanxi.yanxiapi.service.FileService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

@Service
public class FileServiceImpl implements FileService {

    private final Path fileStorageLocation;

    public FileServiceImpl(@Value("${file.upload-dir:./uploads}") String uploadDir) {
        this.fileStorageLocation = Paths.get(uploadDir).toAbsolutePath().normalize();
        try {
            Files.createDirectories(this.fileStorageLocation);
        } catch (IOException ex) {
            throw new RuntimeException("无法创建文件上传目录", ex);
        }
    }

    @Override
    public String storeFile(MultipartFile file) throws IOException {
        // 生成唯一文件名
        String originalFileName = StringUtils.cleanPath(file.getOriginalFilename());
        String fileExtension = originalFileName.substring(originalFileName.lastIndexOf("."));
        String fileName = UUID.randomUUID().toString() + fileExtension;

        // 检查文件名是否包含非法字符
        if (fileName.contains("..")) {
            throw new IOException("文件名包含非法字符");
        }

        // 创建assignments子目录
        Path assignmentsDir = this.fileStorageLocation.resolve("assignments");
        Files.createDirectories(assignmentsDir);

        // 复制文件到目标位置
        Path targetLocation = assignmentsDir.resolve(fileName);
        Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);

        // 返回相对路径
        return "uploads/assignments/" + fileName;
    }

    @Override
    public Resource loadFileAsResource(String fileUrl) throws IOException {
        try {
            // 如果fileUrl包含完整路径，直接使用
            Path filePath;
            if (fileUrl.startsWith("uploads/")) {
                filePath = Paths.get(fileUrl).normalize();
            } else {
                // 否则假设只有文件名，添加assignments目录
                filePath = this.fileStorageLocation.resolve("assignments").resolve(fileUrl).normalize();
            }
            
            Resource resource = new UrlResource(filePath.toUri());
            
            if (resource.exists()) {
                return resource;
            } else {
                throw new IOException("文件不存在: " + fileUrl);
            }
        } catch (MalformedURLException ex) {
            throw new IOException("文件不存在: " + fileUrl, ex);
        }
    }
} 