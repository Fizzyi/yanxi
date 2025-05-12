package com.yanxi.yanxiapi.service.impl;

import com.yanxi.yanxiapi.dto.UserLoginDTO;
import com.yanxi.yanxiapi.dto.UserRegisterDTO;
import com.yanxi.yanxiapi.entity.User;
import com.yanxi.yanxiapi.repository.UserRepository;
import com.yanxi.yanxiapi.service.UserService;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.Optional;

/**
 * 用户服务实现类
 */
@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private final String JWT_SECRET = "yanxi_secret_key";
    private final long JWT_EXPIRATION = 86400000L; // 24小时

    @Override
    public User register(UserRegisterDTO registerDTO) {
        // 检查用户名是否已存在
        if (userRepository.findByUsername(registerDTO.getUsername()).isPresent()) {
            throw new RuntimeException("用户名已存在");
        }

        // 创建新用户
        User user = new User();
        user.setUsername(registerDTO.getUsername());
        user.setPassword(passwordEncoder.encode(registerDTO.getPassword()));
        user.setUserType(registerDTO.getUserType());
        user.setRealName(registerDTO.getRealName());
        user.setEmail(registerDTO.getEmail());
        user.setPhone(registerDTO.getPhone());
        user.setCreateTime(System.currentTimeMillis());
        user.setUpdateTime(System.currentTimeMillis());

        return userRepository.save(user);
    }

    @Override
    public String login(UserLoginDTO loginDTO) {
        // 查找用户
        Optional<User> userOpt = userRepository.findByUsernameAndUserType(
            loginDTO.getUsername(), 
            loginDTO.getUserType()
        );

        if (!userOpt.isPresent()) {
            throw new RuntimeException("用户不存在");
        }

        User user = userOpt.get();

        // 验证密码
        if (!passwordEncoder.matches(loginDTO.getPassword(), user.getPassword())) {
            throw new RuntimeException("密码错误");
        }

        // 生成JWT token
        return generateToken(user);
    }

    /**
     * 生成JWT token
     */
    private String generateToken(User user) {
        return Jwts.builder()
                .setSubject(user.getUsername())
                .claim("userId", user.getId())
                .claim("userType", user.getUserType())
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + JWT_EXPIRATION))
                .signWith(SignatureAlgorithm.HS512, JWT_SECRET)
                .compact();
    }
} 