package com.yanxi.yanxiapi.service;

import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface FileService {
    String storeFile(MultipartFile file) throws IOException;
    Resource loadFileAsResource(String fileName) throws IOException;
} 