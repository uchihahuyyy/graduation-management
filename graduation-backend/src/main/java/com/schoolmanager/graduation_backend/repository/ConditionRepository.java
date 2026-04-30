package com.schoolmanager.graduation_backend.repository;

import com.schoolmanager.graduation_backend.entity.GraduationCondition;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.UUID;

@Repository
public interface ConditionRepository extends JpaRepository<GraduationCondition, UUID> {
    // Kế thừa sẵn các hàm tìm kiếm, thêm, sửa, xóa
}