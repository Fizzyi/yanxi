package com.yanxi.yanxiapi.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.yanxi.yanxiapi.entity.AssignmentSubmission;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import java.util.List;

@Mapper
public interface AssignmentSubmissionMapper extends BaseMapper<AssignmentSubmission> {
    
    @Select("SELECT * FROM assignment_submission WHERE assignment_id = #{assignmentId}")
    List<AssignmentSubmission> selectByAssignmentId(Long assignmentId);
} 