package com.schoolmanager.graduation_backend.dto.response;

import com.schoolmanager.graduation_backend.entity.GraduationResult;
import com.schoolmanager.graduation_backend.entity.Student;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

@Getter
public class ResultTableRowDTO {
    private final UUID id;
    private final String studentId;
    private final String fullName;
    private final String className;
    private final String cohort;
    private final BigDecimal gpa;
    private final Integer totalCredits;
    private final Integer failedCredits;
    private final Byte result;
    private final String resultText;
    private final Byte classification;
    private final String classificationText;
    private final LocalDate decisionDate;
    private final UUID reviewer;
    private final String note;

    public ResultTableRowDTO(GraduationResult result, Student student) {
        this.id = result.getId();
        this.studentId = result.getStudentId();
        this.fullName = student != null ? student.getFullName() : null;
        this.className = student != null ? student.getClassName() : null;
        this.cohort = student != null ? student.getCohort() : inferCohort(result.getStudentId());
        this.gpa = result.getGpa();
        this.totalCredits = result.getTotalCredits();
        this.failedCredits = result.getFailedCredits();
        this.result = result.getResult();
        this.resultText = toResultText(result.getResult());
        this.classification = result.getClassification();
        this.classificationText = toClassificationText(result.getClassification());
        this.decisionDate = result.getDecisionDate();
        this.reviewer = result.getReviewer();
        this.note = result.getNote();
    }

    private String inferCohort(String studentId) {
        return studentId != null && studentId.length() >= 3 ? studentId.substring(0, 3) : null;
    }

    private String toResultText(Byte value) {
        if (value == null) {
            return "Chờ xét";
        }
        return value == 1 ? "Đạt" : "Không đạt";
    }

    private String toClassificationText(Byte value) {
        if (value == null) {
            return "";
        }
        return switch (value) {
            case 1 -> "Xuất sắc";
            case 2 -> "Giỏi";
            case 3 -> "Khá";
            case 4 -> "Trung bình";
            default -> "";
        };
    }
}
