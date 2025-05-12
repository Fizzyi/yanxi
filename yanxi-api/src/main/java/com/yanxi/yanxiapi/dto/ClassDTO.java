package com.yanxi.yanxiapi.dto;

import lombok.Data;

@Data
public class ClassDTO {
    private Long id;
    private String name;
    private String code;
    private Long teacherId;
    private String teacherName;
    private Integer studentCount;
    private String createdAt;
} 