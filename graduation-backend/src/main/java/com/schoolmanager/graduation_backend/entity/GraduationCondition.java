package com.schoolmanager.graduation_backend.entity;

import jakarta.persistence.*;
import lombok.*;
import com.schoolmanager.graduation_backend.util.VietnameseTextFixer;

import java.math.BigDecimal;
import java.util.UUID;


@Entity
@Table(name = "graduation_conditions")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class GraduationCondition extends BaseAuditEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "program_id")
    private UUID programId;
    @Column(name = "applied_cohort", length = 10)
    private String appliedCohort;

    @Column(name = "min_total_credits")
    private Integer minTotalCredits;

    @Column(name = "min_gpa", precision = 3, scale = 2)
    private BigDecimal minGpa;

    @Column(name = "max_failed_credits")
    private Integer maxFailedCredits;
    @Column(name = "english_requirement", length = 100)
    private String englishRequirement;
    @Column(name = "it_requirement", length = 100)
    private String itRequirement;
    @Column(name = "conduct_required", length = 50)
    private String conductRequired;
    @Column(name = "note", columnDefinition = "NVARCHAR(MAX)")
    private String note;

    @PostLoad
    @PrePersist
    @PreUpdate
    private void normalizeVietnameseText() {
        appliedCohort = VietnameseTextFixer.fix(appliedCohort);
        englishRequirement = VietnameseTextFixer.fix(englishRequirement);
        itRequirement = VietnameseTextFixer.fix(itRequirement);
        conductRequired = VietnameseTextFixer.fix(conductRequired);
        note = VietnameseTextFixer.fix(note);
    }
}
