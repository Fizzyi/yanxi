package com.yanxi.yanxiapi.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.yanxi.yanxiapi.entity.Assignment;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import java.util.List;

@Mapper
public interface AssignmentMapper extends BaseMapper<Assignment> {
    
    /**
     * 查询学生已提交的作业ID列表
     * @param studentId 学生ID
     * @param assignmentIds 作业ID列表
     * @return 已提交的作业ID列表
     */
    @Select("SELECT assignment_id FROM assignment_submission WHERE student_id = #{studentId} AND assignment_id IN (#{assignmentIds})")
    List<Long> selectSubmittedAssignmentIds(Long studentId, List<Long> assignmentIds);
} 