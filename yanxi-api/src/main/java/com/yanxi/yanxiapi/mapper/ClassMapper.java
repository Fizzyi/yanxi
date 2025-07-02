package com.yanxi.yanxiapi.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.yanxi.yanxiapi.entity.ClassEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import java.util.List;

@Mapper
public interface ClassMapper extends BaseMapper<ClassEntity> {
    
    @Select("SELECT * FROM class WHERE teacher_id = #{teacherId}")
    List<ClassEntity> findByTeacherId(Long teacherId);
    
    @Select("SELECT * FROM class WHERE code = #{code}")
    ClassEntity findByCode(String code);
    
    /**
     * 批量根据ID查询班级 - 优化N+1查询问题
     */
    @Select("<script>" +
            "SELECT * FROM class WHERE id IN " +
            "<foreach collection='classIds' item='id' open='(' close=')' separator=','>" +
            "#{id}" +
            "</foreach>" +
            "</script>")
    List<ClassEntity> findByIds(@Param("classIds") List<Long> classIds);
    
    /**
     * 根据班级ID列表查询班级名称映射 - 专门用于性能优化
     */
    @Select("<script>" +
            "SELECT id, name FROM class WHERE id IN " +
            "<foreach collection='classIds' item='id' open='(' close=')' separator=','>" +
            "#{id}" +
            "</foreach>" +
            "</script>")
    List<ClassEntity> findNamesByIds(@Param("classIds") List<Long> classIds);
} 