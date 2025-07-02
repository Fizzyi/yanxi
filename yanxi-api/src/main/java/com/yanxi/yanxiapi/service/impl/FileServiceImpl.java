package com.yanxi.yanxiapi.service.impl;

import com.yanxi.yanxiapi.service.FileService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;
import io.micrometer.core.annotation.Timed;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

@Service
@Slf4j
public class FileServiceImpl implements FileService {

    private final Path fileStorageLocation;
    
    // 允许的文件扩展名
    private static final Set<String> ALLOWED_EXTENSIONS = new HashSet<>(Arrays.asList(
        ".pdf", ".doc", ".docx", ".txt", ".jpg", ".jpeg", ".png", ".gif", 
        ".zip", ".rar", ".7z", ".ppt", ".pptx", ".xls", ".xlsx", ".mp4", ".avi"
    ));
    
    // 最大文件大小 (10MB)
    private static final long MAX_FILE_SIZE = 10 * 1024 * 1024;

    @Value("${app.file.max-size:10485760}") // 10MB
    private long maxFileSize;

    public FileServiceImpl(@Value("${file.upload-dir:./uploads}") String uploadDir) {
        this.fileStorageLocation = Paths.get(uploadDir).toAbsolutePath().normalize();
        try {
            Files.createDirectories(this.fileStorageLocation);
            log.info("File storage location initialized: {}", this.fileStorageLocation);
        } catch (IOException ex) {
            log.error("Failed to create file upload directory: {}", uploadDir, ex);
            throw new RuntimeException("无法创建文件上传目录", ex);
        }
    }

    @Override
    @Timed(value = "file.store", description = "Time taken to store file")
    public String storeFile(MultipartFile file) throws IOException {
        log.debug("Storing file: {}, size: {} bytes", file.getOriginalFilename(), file.getSize());
        
        // 验证文件
        validateFile(file);
        
        // 生成唯一文件名
        String originalFileName = StringUtils.cleanPath(file.getOriginalFilename());
        String fileExtension = getFileExtension(originalFileName);
        String fileName = generateUniqueFileName(fileExtension);

        // 检查文件名是否包含非法字符
        if (fileName.contains("..")) {
            throw new IOException("文件名包含非法字符");
        }

        // 创建按日期分类的子目录结构
        String dateFolder = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy/MM/dd"));
        Path assignmentsDir = this.fileStorageLocation.resolve("assignments").resolve(dateFolder);
        Files.createDirectories(assignmentsDir);

        // 复制文件到目标位置
        Path targetLocation = assignmentsDir.resolve(fileName);
        Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);

        // 返回相对路径
        String relativePath = "uploads/assignments/" + dateFolder + "/" + fileName;
        log.info("File stored successfully: {} -> {}", originalFileName, relativePath);
        return relativePath;
    }

    @Async("fileTaskExecutor")
    public CompletableFuture<String> storeFileAsync(MultipartFile file) {
        try {
            String filePath = storeFile(file);
            return CompletableFuture.completedFuture(filePath);
        } catch (IOException e) {
            log.error("Async file storage failed", e);
            return CompletableFuture.failedFuture(e);
        }
    }

    @Override
    @Timed(value = "file.load", description = "Time taken to load file")
    @Cacheable(value = "fileResources", key = "#fileUrl")
    public Resource loadFileAsResource(String fileUrl) throws IOException {
        log.debug("Loading file resource: {}", fileUrl);
        
        try {
            // 如果fileUrl包含完整路径，直接使用
            Path filePath;
            if (fileUrl.startsWith("uploads/")) {
                filePath = Paths.get(fileUrl).normalize();
            } else {
                // 否则假设只有文件名，添加assignments目录
                filePath = this.fileStorageLocation.resolve("assignments").resolve(fileUrl).normalize();
            }
            
            // 安全检查：确保文件路径在允许的目录内
            if (!filePath.normalize().startsWith(this.fileStorageLocation.normalize())) {
                throw new IOException("文件路径不安全: " + fileUrl);
            }
            
            Resource resource = new UrlResource(filePath.toUri());
            
            if (resource.exists() && resource.isReadable()) {
                log.debug("File resource loaded successfully: {}", fileUrl);
                return resource;
            } else {
                throw new IOException("文件不存在或不可读: " + fileUrl);
            }
        } catch (MalformedURLException ex) {
            log.error("Malformed file URL: {}", fileUrl, ex);
            throw new IOException("文件URL格式错误: " + fileUrl, ex);
        }
    }

    /**
     * 验证上传的文件
     */
    private void validateFile(MultipartFile file) throws IOException {
        if (file.isEmpty()) {
            throw new IOException("文件为空");
        }
        
        if (file.getSize() > maxFileSize) {
            throw new IOException("文件大小超过限制 (" + (maxFileSize / 1024 / 1024) + "MB)");
        }
        
        String originalFileName = file.getOriginalFilename();
        if (originalFileName == null || originalFileName.trim().isEmpty()) {
            throw new IOException("文件名不能为空");
        }
        
        String extension = getFileExtension(originalFileName).toLowerCase();
        if (!ALLOWED_EXTENSIONS.contains(extension)) {
            throw new IOException("不支持的文件类型: " + extension);
        }
    }

    /**
     * 获取文件扩展名
     */
    private String getFileExtension(String fileName) {
        if (fileName == null || !fileName.contains(".")) {
            return "";
        }
        return fileName.substring(fileName.lastIndexOf(".")).toLowerCase();
    }

    /**
     * 生成唯一文件名
     */
    private String generateUniqueFileName(String extension) {
        return UUID.randomUUID().toString() + extension;
    }

    /**
     * 检查文件是否存在
     */
    public boolean fileExists(String fileUrl) {
        try {
            Resource resource = loadFileAsResource(fileUrl);
            return resource.exists();
        } catch (IOException e) {
            return false;
        }
    }

    /**
     * 获取文件大小
     */
    public long getFileSize(String fileUrl) throws IOException {
        Resource resource = loadFileAsResource(fileUrl);
        return resource.contentLength();
    }

    /**
     * 删除文件
     */
    @Timed(value = "file.delete", description = "Time taken to delete file")
    public boolean deleteFile(String fileUrl) {
        try {
            Path filePath;
            if (fileUrl.startsWith("uploads/")) {
                filePath = Paths.get(fileUrl).normalize();
            } else {
                filePath = this.fileStorageLocation.resolve("assignments").resolve(fileUrl).normalize();
            }
            
            // 安全检查
            if (!filePath.normalize().startsWith(this.fileStorageLocation.normalize())) {
                log.warn("Attempted to delete file outside storage location: {}", fileUrl);
                return false;
            }
            
            boolean deleted = Files.deleteIfExists(filePath);
            if (deleted) {
                log.info("File deleted successfully: {}", fileUrl);
            } else {
                log.warn("File not found for deletion: {}", fileUrl);
            }
            return deleted;
        } catch (IOException e) {
            log.error("Error deleting file: {}", fileUrl, e);
            return false;
        }
    }

    /**
     * 清理过期文件 (可以通过定时任务调用)
     */
    @Async("taskExecutor")
    public CompletableFuture<Void> cleanupExpiredFiles(int daysOld) {
        // 实现文件清理逻辑
        log.info("Starting cleanup of files older than {} days", daysOld);
        // TODO: 实现具体的清理逻辑
        return CompletableFuture.completedFuture(null);
    }
} 