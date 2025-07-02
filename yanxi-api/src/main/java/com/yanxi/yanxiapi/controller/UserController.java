package com.yanxi.yanxiapi.controller;

import com.yanxi.yanxiapi.dto.UserLoginDTO;
import com.yanxi.yanxiapi.dto.UserRegisterDTO;
import com.yanxi.yanxiapi.entity.User;
import com.yanxi.yanxiapi.service.UserService;
import com.yanxi.yanxiapi.utils.JwtUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 用户控制器
 */
@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:5173")
public class UserController {

    private final UserService userService;

    @Autowired
    private JwtUtils jwtUtils;

    // Rate limiting for token refresh - stores last refresh time per user
    private final Map<Long, Long> lastRefreshTime = new ConcurrentHashMap<>();
    private static final long REFRESH_RATE_LIMIT = 1000 * 60 * 2; // 2分钟限制

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
            Map<String, String> response = userService.loginWithRole(loginDTO);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, String> response = new HashMap<>();
            response.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    /**
     * 刷新JWT Token
     * 只有在token即将过期且距离上次刷新超过2分钟时才允许刷新
     */
    @PostMapping("/refresh-token")
    public ResponseEntity<Map<String, Object>> refreshToken(@RequestHeader("Authorization") String authHeader) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            // 提取token
            String token = authHeader.substring(7); // 移除 "Bearer " 前缀
            
            // 检查token是否有效
            if (jwtUtils.isTokenExpired(token)) {
                response.put("success", false);
                response.put("message", "Token已过期，请重新登录");
                return ResponseEntity.status(401).body(response);
            }
            
            // 检查是否需要刷新
            if (!jwtUtils.shouldRefreshToken(token)) {
                response.put("success", false);
                response.put("message", "Token还未到刷新时间");
                response.put("timeUntilExpiration", jwtUtils.getTimeUntilExpiration(token));
                return ResponseEntity.ok(response);
            }
            
            // 提取用户信息
            Long userId = jwtUtils.extractUserId(token);
            
            // 检查刷新频率限制
            Long lastRefresh = lastRefreshTime.get(userId);
            long currentTime = System.currentTimeMillis();
            
            if (lastRefresh != null && (currentTime - lastRefresh) < REFRESH_RATE_LIMIT) {
                response.put("success", false);
                response.put("message", "刷新过于频繁，请稍后再试");
                response.put("timeUntilNextRefresh", REFRESH_RATE_LIMIT - (currentTime - lastRefresh));
                return ResponseEntity.status(429).body(response); // Too Many Requests
            }
            
            // 刷新token
            String newToken = jwtUtils.refreshToken(token);
            if (newToken == null) {
                response.put("success", false);
                response.put("message", "Token刷新失败");
                return ResponseEntity.status(500).body(response);
            }
            
            // 更新最后刷新时间
            lastRefreshTime.put(userId, currentTime);
            
            // 返回新token
            response.put("success", true);
            response.put("message", "Token刷新成功");
            response.put("token", newToken);
            response.put("expiresIn", 30 * 60 * 1000); // 30分钟，以毫秒为单位
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Token刷新失败: " + e.getMessage());
            return ResponseEntity.status(500).body(response);
        }
    }

    /**
     * 检查token状态
     */
    @GetMapping("/token-status")
    public ResponseEntity<Map<String, Object>> getTokenStatus(@RequestHeader("Authorization") String authHeader) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            String token = authHeader.substring(7);
            
            response.put("success", true);
            response.put("isExpired", jwtUtils.isTokenExpired(token));
            response.put("shouldRefresh", jwtUtils.shouldRefreshToken(token));
            response.put("timeUntilExpiration", jwtUtils.getTimeUntilExpiration(token));
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "获取token状态失败: " + e.getMessage());
            return ResponseEntity.status(500).body(response);
        }
    }
} 