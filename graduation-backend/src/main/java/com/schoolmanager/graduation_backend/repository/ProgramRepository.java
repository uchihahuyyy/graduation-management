package com.schoolmanager.graduation_backend.repository;

import com.schoolmanager.graduation_backend.entity.Program;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface ProgramRepository extends JpaRepository<Program, UUID> {
    Optional<Program> findByProgramCode(String programCode);
    boolean existsByProgramCode(String programCode);
}
