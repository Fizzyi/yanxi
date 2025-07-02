package com.yanxi.yanxiapi.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.yanxi.yanxiapi.entity.ClassEntity;
import com.yanxi.yanxiapi.entity.ClassStudent;
import com.yanxi.yanxiapi.entity.User;

import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * 班级服务接口
 */
public interface ClassService extends IService<ClassEntity> {
    /**
     * 获取教师的所有班级
     */
    List<ClassEntity> getClassesByTeacher(User teacher);

    /**
     * 获取学生的所有班级
     */
    List<ClassStudent> getClassesByStudent(User student);

    /**
     * 创建新班级
     */
    ClassEntity createClass(String name, User teacher);

    /**
     * 删除班级
     */
    void deleteClass(Long classId);

    /**
     * 将学生添加到班级
     */
    ClassStudent addStudentToClass(ClassEntity classEntity, User student);

    /**
     * 从班级中移除学生
     */
    void removeStudentFromClass(ClassEntity classEntity, User student);

    /**
     * 根据班级代码获取班级
     */
    Optional<ClassEntity> getClassByCode(String code);

    /**
     * 根据ID获取班级
     */
    Optional<ClassEntity> getClassById(Long id);

    /**
     * 获取班级的学生数量
     */
    Integer getStudentCount(Long classId);

    /**
     * 获取老师所有班级的学生列表
     * @param teacher 教师
     * @param email 学生邮箱（可选）
     * @param classId 班级ID（可选）
     * @return 学生列表
     */
    List<User> getTeacherAllStudents(User teacher, String email, Long classId);

    /**
     * 批量获取班级信息 - 性能优化方法
     */
    List<ClassEntity> getClassesByIds(List<Long> classIds);

    /**
     * 批量获取班级名称映射 - 专门用于性能优化
     * @param classIds 班级ID列表
     * @return Map<classId, className>
     */
    Map<Long, String> getClassNamesMap(List<Long> classIds);

    /**
     * 批量获取用户信息 - 性能优化方法
     */
    List<User> getUsersByIds(List<Long> userIds);
}
