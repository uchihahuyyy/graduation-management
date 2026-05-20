package com.schoolmanager.graduation_backend.service;

import com.schoolmanager.graduation_backend.dto.request.ConditionRequestDTO;
import com.schoolmanager.graduation_backend.entity.GraduationCondition;
import com.schoolmanager.graduation_backend.repository.ConditionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class ConditionService {

    @Autowired
    private ConditionRepository conditionRepository;

    public List<GraduationCondition> findAll() {
        return conditionRepository.findAll();
    }

    public GraduationCondition findById(UUID id) {
        return conditionRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Không tìm thấy điều kiện xét tốt nghiệp với ID: " + id));
    }

    public GraduationCondition save(ConditionRequestDTO dto) {
        GraduationCondition entity;

        if (dto.getId() != null) {
            // Sửa: tìm entity cũ rồi cập nhật
            entity = findById(dto.getId());
        } else {
            // Thêm mới
            entity = new GraduationCondition();
            entity.setIsActive(true);
        }

        entity.setProgramId(dto.getProgramId());
        entity.setAppliedCohort(dto.getAppliedCohort());
        entity.setMinTotalCredits(dto.getMinTotalCredits());
        entity.setMinGpa(dto.getMinGpa());
        entity.setMaxFailedCredits(dto.getMaxFailedCredits());
        entity.setEnglishRequirement(dto.getEnglishRequirement());
        entity.setItRequirement(dto.getItRequirement());
        entity.setConductRequired(dto.getConductRequired());
        entity.setNote(dto.getNote());
        // createdAt/updatedAt và createdBy/updatedBy được JPA Auditing xử lý tự động

        return conditionRepository.save(entity);
    }

    public void softDelete(UUID id) {
        GraduationCondition entity = findById(id);
        entity.setIsActive(false);
        entity.setDeletedAt(LocalDateTime.now());
        conditionRepository.save(entity);
    }

    public void hardDelete(UUID id) {
        GraduationCondition entity = findById(id);
        conditionRepository.delete(entity);
    }
}
