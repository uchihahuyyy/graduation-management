package com.schoolmanager.graduation_backend.entity;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(name = "graduation_results")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class GraduationResult extends BaseAuditEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "student_id")
    private UUID studentId;

    @Column(name = "condition_id")
    private UUID conditionId;

    @Column(name = "gpa", precision = 3, scale = 2)
    private BigDecimal gpa;

    @Column(name = "total_credits")
    private Integer totalCredits;

    @Column(name = "failed_credits")
    private Integer failedCredits;

    // null: Chờ xét, 0: Không đạt, 1: Đạt
    @Column(name = "result")
    private Byte result;

    // 1: Xuất sắc, 2: Giỏi, 3: Khá, 4: Trung bình
    @Column(name = "classification")
    private Byte classification;

    @Column(name = "decision_date")
    private LocalDate decisionDate;

    @Column(name = "reviewer")
    private UUID reviewer;

    @Column(name = "note", columnDefinition = "NVARCHAR(MAX)")
    private String note;
}
