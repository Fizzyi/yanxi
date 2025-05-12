package com.yanxi.yanxiapi.controller;

import com.yanxi.yanxiapi.dto.UserLoginDTO;
import com.yanxi.yanxiapi.dto.UserRegisterDTO;
import com.yanxi.yanxiapi.entity.User;
import com.yanxi.yanxiapi.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * 用户控制器
 */
@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private UserService userService;

    /**
     * 学生注册
     */
    @PostMapping("/register/student")
    public ResponseEntity<?> registerStudent(@RequestBody UserRegisterDTO registerDTO) {
        try {
            registerDTO.setUserType("STUDENT");
            User user = userService.register(registerDTO);
            return ResponseEntity.ok(user);
        } catch (Exception e) {
            Map<String, String> response = new HashMap<>();
            response.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    /**
     * 教师注册
     */
    @PostMapping("/register/teacher")
    public ResponseEntity<?> registerTeacher(@RequestBody UserRegisterDTO registerDTO) {
        try {
            registerDTO.setUserType("TEACHER");
            User user = userService.register(registerDTO);
            return ResponseEntity.ok(user);
        } catch (Exception e) {
            Map<String, String> response = new HashMap<>();
            response.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    /**
     * 用户登录
     */
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody UserLoginDTO loginDTO) {
        try {
            String token = userService.login(loginDTO);
            Map<String, String> response = new HashMap<>();
            response.put("token", token);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, String> response = new HashMap<>();
            response.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }
} 