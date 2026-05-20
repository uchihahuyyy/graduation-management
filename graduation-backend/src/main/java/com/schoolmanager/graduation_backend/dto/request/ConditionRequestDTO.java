package com.schoolmanager.graduation_backend.dto.request;

import lombok.Data;
import java.math.BigDecimal;
import java.util.UUID;

@Data
public class ConditionRequestDTO {
    private UUID id; // Dùng cho trường hợp Sửa (Edit)
    private UUID programId;
    private String appliedCohort;
    private Integer minTotalCredits;
    private BigDecimal minGpa;
    private Integer maxFailedCredits;
    private String englishRequirement;
    private String itRequirement;
    private String conductRequired;
    private Boolean requireTuitionCleared;
    private String note;
}