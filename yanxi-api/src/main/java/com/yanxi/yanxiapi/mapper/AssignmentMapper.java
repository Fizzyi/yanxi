package com.yanxi.yanxiapi.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.yanxi.yanxiapi.entity.Assignment;
import com.yanxi.yanxiapi.entity.AssignmentSubmission;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface AssignmentMapper extends BaseMapper<Assignment> {

    /**
     * 查询学生已提交的作业ID列表
     *
     * @param studentId 学生ID
     * @return 已提交的作业ID列表
     */
    @Select("SELECT assignment_id FROM assignment_submission WHERE student_id = #{studentId}")
    List<Long> selectSubmittedAssignmentIds(@Param("studentId") Long studentId);

    /**
     * 插入作业提交记录
     *
     * @param submission 作业提交记录
     * @return 影响的行数
     */
    @Insert("INSERT INTO assignment_submission (assignment_id, student_id, file_url, submitted_at) " +
            "VALUES (#{assignmentId}, #{studentId}, #{fileUrl}, #{submittedAt})")
    int insertSubmission(AssignmentSubmission submission);
} 