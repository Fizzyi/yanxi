package com.yanxi.yanxiapi.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.yanxi.yanxiapi.entity.AssignmentSubmission;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Update;
import org.apache.ibatis.annotations.Param;
import java.util.List;

@Mapper
public interface AssignmentSubmissionMapper extends BaseMapper<AssignmentSubmission> {
    
    @Select("SELECT * FROM assignment_submission WHERE assignment_id = #{assignmentId}")
    List<AssignmentSubmission> selectByAssignmentId(Long assignmentId);

    /**
     * 根据作业ID和学生ID查找提交记录
     * @param assignmentId 作业ID
     * @param studentId 学生ID
     * @return 提交记录
     */
    @Select("SELECT * FROM assignment_submission WHERE assignment_id = #{assignmentId} AND student_id = #{studentId}")
    AssignmentSubmission selectByAssignmentIdAndStudentId(@Param("assignmentId") Long assignmentId, @Param("studentId") Long studentId);

    /**
     * 删除学生的作业提交记录
     * @param assignmentId 作业ID
     * @param studentId 学生ID
     * @return 影响的行数
     */
    @Delete("DELETE FROM assignment_submission WHERE assignment_id = #{assignmentId} AND student_id = #{studentId}")
    int deleteByAssignmentIdAndStudentId(@Param("assignmentId") Long assignmentId, @Param("studentId") Long studentId);

    /**
     * 更新学生的作业提交记录
     * @param assignmentId 作业ID
     * @param studentId 学生ID
     * @param fileUrl 新的文件URL
     * @param submittedAt 提交时间
     * @return 影响的行数
     */
    @Update("UPDATE assignment_submission SET file_url = #{fileUrl}, submitted_at = #{submittedAt} WHERE assignment_id = #{assignmentId} AND student_id = #{studentId}")
    int updateByAssignmentIdAndStudentId(@Param("assignmentId") Long assignmentId, @Param("studentId") Long studentId, @Param("fileUrl") String fileUrl, @Param("submittedAt") java.time.LocalDateTime submittedAt);

    /**
     * 删除作业的所有提交记录
     * @param assignmentId 作业ID
     * @return 影响的行数
     */
    @Delete("DELETE FROM assignment_submission WHERE assignment_id = #{assignmentId}")
    int deleteByAssignmentId(@Param("assignmentId") Long assignmentId);
} 