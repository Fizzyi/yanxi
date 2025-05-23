package com.yanxi.yanxiapi.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.yanxi.yanxiapi.dto.UserLoginDTO;
import com.yanxi.yanxiapi.dto.UserRegisterDTO;
import com.yanxi.yanxiapi.entity.User;
import org.springframework.security.core.userdetails.UserDetailsService;

import java.util.Map;
import java.util.Optional;

/**
 * 用户服务接口
 */
public interface UserService extends IService<User>, UserDetailsService {
    /**
     * 根据ID获取用户
     */
    Optional<User> getUserById(Long id);

    /**
     * 根据用户名获取用户
     */
    Optional<User> getUserByUsername(String username);

    /**
     * 用户注册
     * @param registerDTO 注册信息
     * @return 注册成功的用户信息
     */
    User register(UserRegisterDTO registerDTO);

    /**
     * 用户登录
     * @param loginDTO 登录信息
     * @return JWT token
     */
    String login(UserLoginDTO loginDTO);

    /**
     * 生成JWT token
     */
    String generateToken(User user);

    /**
     * 登录并返回token和userRole
     */
    Map<String, String> loginWithRole(UserLoginDTO loginDTO);

    /**
     * 根据邮箱查找用户
     * @param email 邮箱
     * @return 用户对象，如果不存在返回null
     */
    User getByEmail(String email);
} 