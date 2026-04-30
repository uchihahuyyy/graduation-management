package com.schoolmanager.graduation_backend.service;

import com.schoolmanager.graduation_backend.dto.request.ResultRequestDTO;
import com.schoolmanager.graduation_backend.entity.GraduationResult;
import com.schoolmanager.graduation_backend.repository.ResultRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class ResultService {

    @Autowired
    private ResultRepository resultRepository;

    public List<GraduationResult> findAll() {
        return resultRepository.findAll();
    }

    public GraduationResult findById(UUID id) {
        return resultRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Không tìm thấy kết quả xét tốt nghiệp với ID: " + id));
    }

    public void save(ResultRequestDTO dto) {
        GraduationResult entity;

        if (dto.getId() != null && !dto.getId().isBlank()) {
            // Sửa: tìm entity cũ rồi cập nhật
            entity = findById(UUID.fromString(dto.getId()));
        } else {
            // Thêm mới
            entity = new GraduationResult();
            entity.setIsActive(true);
        }

        // studentId giờ là String thuần — set thẳng, không cần convert
        entity.setStudentId(dto.getStudentId() != null ? dto.getStudentId().trim() : null);
        entity.setConditionId(parseUuid(dto.getConditionId())); // conditionId vẫn là UUID
        entity.setGpa(dto.getGpa());
        entity.setTotalCredits(dto.getTotalCredits());
        entity.setFailedCredits(dto.getFailedCredits());
        entity.setResult(dto.getResult());
        entity.setClassification(dto.getClassification());
        entity.setDecisionDate(dto.getDecisionDate());
        entity.setReviewer(parseUuid(dto.getReviewer())); // reviewer vẫn là UUID
        entity.setNote(dto.getNote());
        // createdAt/updatedAt và createdBy/updatedBy được JPA Auditing xử lý tự động

        resultRepository.save(entity);
    }

    public void softDelete(UUID id) {
        GraduationResult entity = findById(id);
        entity.setIsActive(false);
        entity.setDeletedAt(LocalDateTime.now());
        resultRepository.save(entity);
    }

    /** Chuyển String → UUID an toàn; trả về null nếu chuỗi rỗng hoặc null */
    private UUID parseUuid(String value) {
        if (value == null || value.isBlank()) return null;
        try {
            return UUID.fromString(value);
        } catch (IllegalArgumentException e) {
            return null;
        }
    }
}
