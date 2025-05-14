package com.yanxi.yanxiapi.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.yanxi.yanxiapi.entity.ClassStudent;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import java.util.List;

@Mapper
public interface ClassStudentMapper extends BaseMapper<ClassStudent> {
    
    @Select("SELECT * FROM class_student WHERE student_id = #{studentId}")
    List<ClassStudent> findByStudentId(Long studentId);
    
    @Select("SELECT * FROM class_student WHERE class_id = #{classId}")
    List<ClassStudent> findByClassId(Long classId);
    
    @Select("SELECT * FROM class_student WHERE class_id = #{classId} AND student_id = #{studentId}")
    ClassStudent findByClassIdAndStudentId(Long classId, Long studentId);
    
    /**
     * 根据学生ID查询其所在的所有班级ID
     * @param studentId 学生ID
     * @return 班级ID列表
     */
    @Select("SELECT class_id FROM class_student WHERE student_id = #{studentId}")
    List<Long> selectClassIdsByStudentId(Long studentId);
} 