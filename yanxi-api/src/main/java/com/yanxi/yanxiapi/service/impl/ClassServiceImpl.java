package com.yanxi.yanxiapi.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yanxi.yanxiapi.entity.ClassEntity;
import com.yanxi.yanxiapi.entity.ClassStudent;
import com.yanxi.yanxiapi.entity.User;
import com.yanxi.yanxiapi.mapper.ClassMapper;
import com.yanxi.yanxiapi.mapper.ClassStudentMapper;
import com.yanxi.yanxiapi.mapper.UserMapper;
import com.yanxi.yanxiapi.service.ClassService;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class ClassServiceImpl extends ServiceImpl<ClassMapper, ClassEntity> implements ClassService {
    
    private final ClassMapper classMapper;
    private final ClassStudentMapper classStudentMapper;
    private final UserMapper userMapper;

    public ClassServiceImpl(ClassMapper classMapper, ClassStudentMapper classStudentMapper, UserMapper userMapper) {
        this.classMapper = classMapper;
        this.classStudentMapper = classStudentMapper;
        this.userMapper = userMapper;
    }

    @Override
    @Transactional(readOnly = true)
    @Cacheable(value = "classes", key = "'teacher_' + #teacher.id")
    public List<ClassEntity> getClassesByTeacher(User teacher) {
        return classMapper.findByTeacherId(teacher.getId());
    }

    @Override
    @Transactional(readOnly = true)
    @Cacheable(value = "classes", key = "'student_' + #student.id")
    public List<ClassStudent> getClassesByStudent(User student) {
        return classStudentMapper.findByStudentId(student.getId());
    }

    @Override
    @Transactional
    @CacheEvict(value = "classes", key = "'teacher_' + #teacher.id")
    public ClassEntity createClass(String name, User teacher) {
        ClassEntity classEntity = new ClassEntity();
        classEntity.setName(name);
        classEntity.setTeacherId(teacher.getId());
        classEntity.setCode(generateUniqueCode());
        save(classEntity);
        return classEntity;
    }

    @Override
    @Transactional
    @CacheEvict(value = "classes", allEntries = true)
    public void deleteClass(Long classId) {
        // First, delete all student enrollments for this class
        List<ClassStudent> classStudents = classStudentMapper.findByClassId(classId);
        for (ClassStudent classStudent : classStudents) {
            classStudentMapper.deleteById(classStudent.getId());
        }
        
        // TODO: Also delete all assignments for this class when assignment deletion is implemented
        // This would require an AssignmentMapper method like: assignmentMapper.deleteByClassId(classId);
        
        // Finally, delete the class itself
        removeById(classId);
    }

    @Override
    @Transactional
    @CacheEvict(value = "classes", key = "'student_' + #student.id")
    public ClassStudent addStudentToClass(ClassEntity classEntity, User student) {
        if (classStudentMapper.findByClassIdAndStudentId(classEntity.getId(), student.getId()) != null) {
            throw new IllegalStateException("Student is already in this class");
        }

        ClassStudent classStudent = new ClassStudent();
        classStudent.setClassId(classEntity.getId());
        classStudent.setStudentId(student.getId());
        classStudentMapper.insert(classStudent);
        return classStudent;
    }

    @Override
    @Transactional
    @CacheEvict(value = "classes", key = "'student_' + #student.id")
    public void removeStudentFromClass(ClassEntity classEntity, User student) {
        ClassStudent classStudent = classStudentMapper.findByClassIdAndStudentId(classEntity.getId(), student.getId());
        if (classStudent != null) {
            classStudentMapper.deleteById(classStudent.getId());
        }
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<ClassEntity> getClassByCode(String code) {
        return Optional.ofNullable(classMapper.findByCode(code));
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<ClassEntity> getClassById(Long id) {
        return Optional.ofNullable(getById(id));
    }

    @Override
    @Transactional(readOnly = true)
    public Integer getStudentCount(Long classId) {
        return classStudentMapper.findByClassId(classId).size();
    }

    @Override
    @Transactional(readOnly = true)
    public List<User> getTeacherAllStudents(User teacher, String email, Long classId) {
        // 获取教师的所有班级
        List<ClassEntity> classes = getClassesByTeacher(teacher);
        
        // 使用 Set 存储学生ID，自动去重
        Set<Long> uniqueStudentIds = new HashSet<>();
        
        // 如果指定了班级ID，只获取该班级的学生
        if (classId != null) {
            // 验证班级是否属于该教师
            boolean isTeacherClass = classes.stream()
                    .anyMatch(c -> c.getId().equals(classId));
            if (!isTeacherClass) {
                throw new IllegalArgumentException("Class not found or not authorized");
            }
            
            List<Long> studentIds = classStudentMapper.selectStudentIdsByClassId(classId);
            uniqueStudentIds.addAll(studentIds);
        } else {
            // 获取所有班级的学生ID - 优化：批量查询
            for (ClassEntity classEntity : classes) {
                List<Long> studentIds = classStudentMapper.selectStudentIdsByClassId(classEntity.getId());
                uniqueStudentIds.addAll(studentIds);
            }
        }
        
        if (uniqueStudentIds.isEmpty()) {
            return new ArrayList<>();
        }
        
        // 批量查询所有学生信息 - 消除N+1查询问题
        List<User> allStudents = userMapper.findByIds(new ArrayList<>(uniqueStudentIds));
        
        // 如果提供了邮箱，进行过滤
        if (email != null && !email.trim().isEmpty()) {
            return allStudents.stream()
                    .filter(student -> student.getEmail().toLowerCase().contains(email.toLowerCase()))
                    .collect(Collectors.toList());
        }
        
        return allStudents;
    }

    @Override
    @Transactional(readOnly = true)
    @Cacheable(value = "classes", key = "'batch_' + #classIds.hashCode()")
    public List<ClassEntity> getClassesByIds(List<Long> classIds) {
        if (classIds == null || classIds.isEmpty()) {
            return new ArrayList<>();
        }
        return classMapper.findByIds(classIds);
    }

    @Override
    @Transactional(readOnly = true)
    @Cacheable(value = "classNames", key = "#classIds.hashCode()")
    public Map<Long, String> getClassNamesMap(List<Long> classIds) {
        if (classIds == null || classIds.isEmpty()) {
            return Map.of();
        }
        
        List<ClassEntity> classes = classMapper.findNamesByIds(classIds);
        return classes.stream()
                .collect(Collectors.toMap(ClassEntity::getId, ClassEntity::getName));
    }

    @Override
    @Transactional(readOnly = true)
    @Cacheable(value = "users", key = "'batch_' + #userIds.hashCode()")
    public List<User> getUsersByIds(List<Long> userIds) {
        if (userIds == null || userIds.isEmpty()) {
            return new ArrayList<>();
        }
        return userMapper.findByIds(userIds);
    }

    private String generateUniqueCode() {
        String code;
        do {
            code = UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        } while (classMapper.findByCode(code) != null);
        return code;
    }
} 