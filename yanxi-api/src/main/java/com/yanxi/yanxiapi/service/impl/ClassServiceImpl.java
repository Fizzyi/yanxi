package com.yanxi.yanxiapi.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yanxi.yanxiapi.entity.ClassEntity;
import com.yanxi.yanxiapi.entity.ClassStudent;
import com.yanxi.yanxiapi.entity.User;
import com.yanxi.yanxiapi.mapper.ClassMapper;
import com.yanxi.yanxiapi.mapper.ClassStudentMapper;
import com.yanxi.yanxiapi.mapper.UserMapper;
import com.yanxi.yanxiapi.service.ClassService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
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
    public List<ClassEntity> getClassesByTeacher(User teacher) {
        return classMapper.findByTeacherId(teacher.getId());
    }

    @Override
    @Transactional(readOnly = true)
    public List<ClassStudent> getClassesByStudent(User student) {
        return classStudentMapper.findByStudentId(student.getId());
    }

    @Override
    @Transactional
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
    public void deleteClass(Long classId) {
        removeById(classId);
    }

    @Override
    @Transactional
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
        
        // 使用 Set 存储学生，自动去重
        Set<User> uniqueStudents = new HashSet<>();
        
        // 如果指定了班级ID，只获取该班级的学生
        if (classId != null) {
            // 验证班级是否属于该教师
            boolean isTeacherClass = classes.stream()
                    .anyMatch(c -> c.getId().equals(classId));
            if (!isTeacherClass) {
                throw new IllegalArgumentException("Class not found or not authorized");
            }
            
            List<ClassStudent> classStudents = classStudentMapper.findByClassId(classId);
            for (ClassStudent classStudent : classStudents) {
                User student = userMapper.selectById(classStudent.getStudentId());
                if (student != null) {
                    uniqueStudents.add(student);
                }
            }
        } else {
            // 获取所有班级的学生
            for (ClassEntity classEntity : classes) {
                List<ClassStudent> classStudents = classStudentMapper.findByClassId(classEntity.getId());
                for (ClassStudent classStudent : classStudents) {
                    User student = userMapper.selectById(classStudent.getStudentId());
                    if (student != null) {
                        uniqueStudents.add(student);
                    }
                }
            }
        }
        
        // 转换为 List
        List<User> allStudents = new ArrayList<>(uniqueStudents);
        
        // 如果提供了邮箱，进行过滤
        if (email != null && !email.trim().isEmpty()) {
            return allStudents.stream()
                    .filter(student -> student.getEmail().toLowerCase().contains(email.toLowerCase()))
                    .collect(Collectors.toList());
        }
        
        return allStudents;
    }

    private String generateUniqueCode() {
        String code;
        do {
            code = UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        } while (classMapper.findByCode(code) != null);
        return code;
    }
} 