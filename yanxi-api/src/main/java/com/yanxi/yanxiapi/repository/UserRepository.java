package com.yanxi.yanxiapi.repository;

import com.yanxi.yanxiapi.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * 用户数据访问层
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    /**
     * 根据用户名查找用户
     * @param username 用户名
     * @return 用户信息
     */
    Optional<User> findByUsername(String username);

    /**
     * 根据用户名和用户类型查找用户
     * @param username 用户名
     * @param userType 用户类型
     * @return 用户信息
     */
    Optional<User> findByUsernameAndUserType(String username, String userType);
} 