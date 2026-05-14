package com.schoolmanager.graduation_backend.dto.request;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

@Data
public class StudentRequestDTO {
    private UUID id;
    private String studentCode;
    private String fullName;
    private String email;
    private LocalDate dateOfBirth;
    private String gender;
    private String className;
    private String cohort;
    private BigDecimal gpa;
    private Integer totalCredits;
    private Integer failedCredits;
    private String englishStatus;
    private String itStatus;
    private Boolean isActive = true;
}
