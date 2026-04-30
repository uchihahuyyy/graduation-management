package com.schoolmanager.graduation_backend.repository;

import com.schoolmanager.graduation_backend.entity.GraduationResult;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.UUID;

@Repository
public interface ResultRepository extends JpaRepository<GraduationResult, UUID> {
}