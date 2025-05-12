package com.yanxi.yanxiapi.repository;

import com.yanxi.yanxiapi.entity.ClassEntity;
import com.yanxi.yanxiapi.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ClassRepository extends JpaRepository<ClassEntity, Long> {
    List<ClassEntity> findByTeacher(User teacher);
    Optional<ClassEntity> findByCode(String code);
    boolean existsByCode(String code);
} 