package com.schoolmanager.graduation_backend.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(name = "graduation_periods")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class GraduationPeriod extends BaseAuditEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "name", length = 200, nullable = false)
    private String name;

    @Column(name = "start_date")
    private LocalDate startDate;

    @Column(name = "end_date")
    private LocalDate endDate;

    @Column(name = "status", length = 50)
    private String status;
}
