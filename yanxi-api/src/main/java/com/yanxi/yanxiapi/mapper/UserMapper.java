package com.yanxi.yanxiapi.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.yanxi.yanxiapi.entity.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface UserMapper extends BaseMapper<User> {
    
    @Select("SELECT id, username, password, role as userType, email, created_at, updated_at, real_name FROM user WHERE username = #{username}")
    User findByUsername(String username);
    
    @Select("SELECT id, username, password, role as userType, email, created_at, updated_at, real_name FROM user WHERE email = #{email}")
    User findByEmail(String email);
    
    /**
     * 批量根据ID查询用户 - 优化N+1查询问题
     */
    @Select("<script>" +
            "SELECT id, username, password, role as userType, email, created_at, updated_at, real_name FROM user WHERE id IN " +
            "<foreach collection='userIds' item='id' open='(' close=')' separator=','>" +
            "#{id}" +
            "</foreach>" +
            "</script>")
    List<User> findByIds(@Param("userIds") List<Long> userIds);
    
    /**
     * 根据用户类型批量查询
     */
    @Select("SELECT id, username, password, role as userType, email, created_at, updated_at, real_name FROM user WHERE role = #{userType}")
    List<User> findByUserType(@Param("userType") String userType);
} 