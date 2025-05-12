package com.yanxi.yanxiapi.repository;

import com.yanxi.yanxiapi.entity.ClassEntity;
import com.yanxi.yanxiapi.entity.ClassStudent;
import com.yanxi.yanxiapi.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ClassStudentRepository extends JpaRepository<ClassStudent, Long> {
    List<ClassStudent> findByStudent(User student);
    boolean existsByClassEntityAndStudent(ClassEntity classEntity, User student);
    Optional<ClassStudent> findByClassEntityAndStudent(ClassEntity classEntity, User student);
} 