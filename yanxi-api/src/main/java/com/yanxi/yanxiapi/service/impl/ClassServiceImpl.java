package com.yanxi.yanxiapi.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yanxi.yanxiapi.entity.ClassEntity;
import com.yanxi.yanxiapi.entity.ClassStudent;
import com.yanxi.yanxiapi.entity.User;
import com.yanxi.yanxiapi.mapper.ClassMapper;
import com.yanxi.yanxiapi.mapper.ClassStudentMapper;
import com.yanxi.yanxiapi.service.ClassService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class ClassServiceImpl extends ServiceImpl<ClassMapper, ClassEntity> implements ClassService {
    
    private final ClassMapper classMapper;
    private final ClassStudentMapper classStudentMapper;

    public ClassServiceImpl(ClassMapper classMapper, ClassStudentMapper classStudentMapper) {
        this.classMapper = classMapper;
        this.classStudentMapper = classStudentMapper;
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

    private String generateUniqueCode() {
        String code;
        do {
            code = UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        } while (classMapper.findByCode(code) != null);
        return code;
    }
} 