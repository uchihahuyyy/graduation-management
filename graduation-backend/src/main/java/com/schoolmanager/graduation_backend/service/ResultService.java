package com.schoolmanager.graduation_backend.service;

import com.schoolmanager.graduation_backend.dto.request.ResultRequestDTO;
import com.schoolmanager.graduation_backend.entity.GraduationResult;
import com.schoolmanager.graduation_backend.entity.Student;
import com.schoolmanager.graduation_backend.repository.ResultRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.NonNull;
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

    public List<GraduationResult> findByStudentId(String studentId) {
        if (studentId == null || studentId.isBlank()) {
            return List.of();
        }

        try {
            return findByStudentId(UUID.fromString(studentId.trim()));
        } catch (IllegalArgumentException e) {
            return List.of();
        }
    }

    public List<GraduationResult> findByStudentId(UUID studentId) {
        if (studentId == null) {
            return List.of();
        }
        return resultRepository.findByStudentId(studentId);
    }

    public GraduationResult findById(@NonNull UUID id) {
        return resultRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Không tìm thấy kết quả xét tốt nghiệp với ID: " + id));
    }

    public GraduationResult save(ResultRequestDTO dto) {
        GraduationResult entity;

        if (dto.getId() != null && !dto.getId().isBlank()) {
            // Sửa: tìm entity cũ rồi cập nhật
            entity = findById(UUID.fromString(dto.getId()));
        } else {
            // Thêm mới
            entity = new GraduationResult();
            entity.setIsActive(true);
        }

        entity.setStudentId(parseUuid(dto.getStudentId()));
        entity.setConditionId(parseUuid(dto.getConditionId())); // conditionId vẫn là UUID
        entity.setPeriodId(parseUuid(dto.getPeriodId()));
        entity.setGpa(dto.getGpa());
        entity.setTotalCredits(dto.getTotalCredits());
        entity.setFailedCredits(dto.getFailedCredits());
        entity.setResult(dto.getResult());
        entity.setStatus(dto.getStatus());
        entity.setDecisionNumber(dto.getDecisionNumber());
        entity.setClassification(dto.getClassification());
        entity.setDecisionDate(dto.getDecisionDate());
        entity.setReviewer(parseUuid(dto.getReviewer())); // reviewer vẫn là UUID
        entity.setNote(dto.getNote());
        // createdAt/updatedAt và createdBy/updatedBy được JPA Auditing xử lý tự động

        return resultRepository.save(entity);
    }

    public void ensurePendingResult(Student student) {
        if (student == null || student.getId() == null) {
            return;
        }

        if (resultRepository.existsByStudentId(student.getId())) {
            return;
        }

        GraduationResult result = new GraduationResult();
        result.setStudentId(student.getId());
        result.setGpa(student.getGpa());
        result.setTotalCredits(student.getTotalCredits());
        result.setFailedCredits(student.getFailedCredits());
        result.setResult(null);
        result.setClassification(null);
        result.setNote("Chờ xét tốt nghiệp");
        result.setIsActive(true);
        resultRepository.save(result);
    }

    public void ensurePendingResults(List<Student> students) {
        students.forEach(this::ensurePendingResult);
    }

    public void softDelete(@NonNull UUID id) {
        GraduationResult entity = findById(id);
        entity.setIsActive(false);
        entity.setDeletedAt(LocalDateTime.now());
        resultRepository.save(entity);
    }

    public void hardDelete(@NonNull UUID id) {
        findById(id);
        resultRepository.deleteById(id);
    }

    public void relinkStudentCode(String oldStudentCode, String newStudentCode) {
        // graduation_results.student_id now stores students.id, so changing student_code no longer requires relinking.
    }

    public void hardDeleteByStudentId(UUID studentId) {
        if (studentId != null) {
            resultRepository.deleteByStudentId(studentId);
        }
    }

    public void hardDeleteByStudentCode(String studentCode) {
        // graduation_results.student_id stores students.id, so student_code cleanup is no longer needed.
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
