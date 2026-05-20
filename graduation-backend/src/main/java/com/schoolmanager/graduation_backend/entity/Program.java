package com.schoolmanager.graduation_backend.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Entity
@Table(name = "programs")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Program extends BaseAuditEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "program_code", nullable = false, unique = true, length = 20)
    private String programCode;

    @Column(name = "program_name", nullable = false, length = 120)
    private String programName;

    @Column(name = "major_name", length = 120)
    private String majorName;

    @Column(name = "education_level", length = 50)
    private String educationLevel;

    @Column(name = "total_required_credits")
    private Integer totalRequiredCredits;
}
