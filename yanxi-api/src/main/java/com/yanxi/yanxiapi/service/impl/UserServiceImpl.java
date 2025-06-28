package com.yanxi.yanxiapi.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yanxi.yanxiapi.dto.UserLoginDTO;
import com.yanxi.yanxiapi.dto.UserRegisterDTO;
import com.yanxi.yanxiapi.entity.User;
import com.yanxi.yanxiapi.mapper.UserMapper;
import com.yanxi.yanxiapi.service.UserService;
import com.yanxi.yanxiapi.utils.JwtUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

/**
 * 用户服务实现类
 */
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtUtils jwtUtils;

    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userMapper.findByUsername(username);
        if (user == null) {
            throw new UsernameNotFoundException("User not found with username: " + username);
        }
        List<GrantedAuthority> authorities = Arrays.asList(
            new SimpleGrantedAuthority("ROLE_" + user.getUserType()));
        return org.springframework.security.core.userdetails.User
                .withUsername(user.getUsername())
                .password(user.getPassword())
                .authorities(authorities)
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<User> getUserById(Long id) {
        return Optional.ofNullable(getById(id));
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<User> getUserByUsername(String username) {
        return Optional.ofNullable(userMapper.findByUsername(username));
    }

    @Override
    @Transactional
    public User register(UserRegisterDTO registerDTO) {
        // 检查用户名是否已存在
        if (userMapper.findByUsername(registerDTO.getUsername()) != null) {
            throw new RuntimeException("用户名已存在");
        }

        // 检查邮箱是否已存在
        if (userMapper.findByEmail(registerDTO.getEmail()) != null) {
            throw new RuntimeException("邮箱已被注册");
        }

        // 创建新用户
        User user = new User();
        user.setUsername(registerDTO.getUsername());
        user.setPassword(passwordEncoder.encode(registerDTO.getPassword()));
        user.setUserType(registerDTO.getUserType());
        user.setRealName(registerDTO.getRealName());
        user.setEmail(registerDTO.getEmail());
        user.setPhone(registerDTO.getPhone());

        // 保存用户
        save(user);
        return user;
    }

    @Override
    @Transactional
    public String login(UserLoginDTO loginDTO) {
        User user = userMapper.findByUsername(loginDTO.getUsername());
        if (user == null) {
            throw new RuntimeException("用户不存在");
        }
        if (!passwordEncoder.matches(loginDTO.getPassword(), user.getPassword())) {
            throw new RuntimeException("密码错误");
        }
        return jwtUtils.generateToken(user);
    }

    @Override
    public String generateToken(User user) {
        return jwtUtils.generateToken(user);
    }

    @Override
    @Transactional
    public Map<String, String> loginWithRole(UserLoginDTO loginDTO) {
        User user = userMapper.findByUsername(loginDTO.getUsername());
        if (user == null) {
            throw new RuntimeException("用户不存在");
        }
        if (!passwordEncoder.matches(loginDTO.getPassword(), user.getPassword())) {
            throw new RuntimeException("密码错误");
        }
        String token = jwtUtils.generateToken(user);
        Map<String, String> response = new HashMap<>();
        response.put("token", token);
        response.put("userRole", user.getUserType());
        response.put("realName", user.getRealName());
        return response;
    }

    @Override
    public User getByEmail(String email) {
        return userMapper.findByEmail(email);
    }
} 