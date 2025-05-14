package com.yanxi.yanxiapi.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.yanxi.yanxiapi.entity.ClassEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import java.util.List;

@Mapper
public interface ClassMapper extends BaseMapper<ClassEntity> {
    
    @Select("SELECT * FROM class WHERE teacher_id = #{teacherId}")
    List<ClassEntity> findByTeacherId(Long teacherId);
    
    @Select("SELECT * FROM class WHERE code = #{code}")
    ClassEntity findByCode(String code);
} 