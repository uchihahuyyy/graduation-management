package com.schoolmanager.graduation_backend.dto.request;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class ResultRequestDTO {
    private String id;           // UUID dạng String, null/rỗng = Thêm mới
    private String studentId;    // UUID sinh viên dạng String
    private String conditionId;  // UUID dạng String
    private BigDecimal gpa;
    private Integer totalCredits;
    private Integer failedCredits;
    private Byte result;           // null: Chờ xét, 0: Không đạt, 1: Đạt
    private Byte classification;   // 1: Xuất sắc, 2: Giỏi, 3: Khá, 4: Trung bình
    private LocalDate decisionDate;
    private String reviewer;       // UUID dạng String
    private String note;
}
