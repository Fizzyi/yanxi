package com.yanxi.yanxiapi.service.impl;

import com.yanxi.yanxiapi.entity.ClassEntity;
import com.yanxi.yanxiapi.entity.ClassStudent;
import com.yanxi.yanxiapi.entity.User;
import com.yanxi.yanxiapi.repository.ClassRepository;
import com.yanxi.yanxiapi.repository.ClassStudentRepository;
import com.yanxi.yanxiapi.service.ClassService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ClassServiceImpl implements ClassService {
    private final ClassRepository classRepository;
    private final ClassStudentRepository classStudentRepository;

    @Override
    @Transactional(readOnly = true)
    public List<ClassEntity> getClassesByTeacher(User teacher) {
        return classRepository.findByTeacher(teacher);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ClassStudent> getClassesByStudent(User student) {
        return classStudentRepository.findByStudent(student);
    }

    @Override
    @Transactional
    public ClassEntity createClass(String name, User teacher) {
        ClassEntity classEntity = new ClassEntity();
        classEntity.setName(name);
        classEntity.setTeacher(teacher);
        classEntity.setCode(generateUniqueCode());
        return classRepository.save(classEntity);
    }

    @Override
    @Transactional
    public void deleteClass(Long classId) {
        classRepository.deleteById(classId);
    }

    @Override
    @Transactional
    public ClassStudent addStudentToClass(ClassEntity classEntity, User student) {
        if (classStudentRepository.existsByClassEntityAndStudent(classEntity, student)) {
            throw new IllegalStateException("Student is already in this class");
        }

        ClassStudent classStudent = new ClassStudent();
        classStudent.setClassEntity(classEntity);
        classStudent.setStudent(student);
        return classStudentRepository.save(classStudent);
    }

    @Override
    @Transactional
    public void removeStudentFromClass(ClassEntity classEntity, User student) {
        Optional<ClassStudent> classStudent = classStudentRepository.findByClassEntityAndStudent(classEntity, student);
        classStudent.ifPresent(classStudentRepository::delete);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<ClassEntity> getClassByCode(String code) {
        return classRepository.findByCode(code);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<ClassEntity> getClassById(Long id) {
        return classRepository.findById(id);
    }

    private String generateUniqueCode() {
        String code;
        do {
            code = UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        } while (classRepository.existsByCode(code));
        return code;
    }
} 