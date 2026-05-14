USE school23;
GO

DECLARE @conditionST23 UNIQUEIDENTIFIER;

SELECT TOP 1 @conditionST23 = id
FROM graduation_conditions
WHERE applied_cohort = N'ST23'
ORDER BY created_at DESC;

IF @conditionST23 IS NULL
BEGIN
    SET @conditionST23 = NEWID();

    INSERT INTO graduation_conditions (
        id,
        program_id,
        applied_cohort,
        min_total_credits,
        min_gpa,
        max_failed_credits,
        english_requirement,
        it_requirement,
        conduct_required,
        note,
        created_at,
        updated_at,
        is_active
    )
    VALUES (
        @conditionST23,
        NEWID(),
        N'ST23',
        120,
        2.00,
        3,
        N'TOEIC 450 hoặc B1',
        N'Đạt MOS/IC3',
        N'Trung bình trở lên',
        N'Điều kiện xét tốt nghiệp áp dụng cho lớp ST23',
        SYSDATETIME(),
        SYSDATETIME(),
        1
    );
END

DECLARE @i INT = 1;

WHILE @i <= 50
BEGIN
    DECLARE @studentCode NVARCHAR(20) = CONCAT(N'ST23-', RIGHT(CONCAT('0000', @i), 4));
    DECLARE @className NVARCHAR(50) =
        CASE (@i - 1) % 4
            WHEN 0 THEN N'ST23A'
            WHEN 1 THEN N'ST23B'
            WHEN 2 THEN N'ST23C'
            ELSE N'ST23D'
        END;
    DECLARE @fullName NVARCHAR(120) = CONCAT(N'Sinh viên ST23 ', RIGHT(CONCAT('000', @i), 3));
    DECLARE @email NVARCHAR(120) = CONCAT('st23', RIGHT(CONCAT('000', @i), 3), '@example.com');
    DECLARE @gpa DECIMAL(3,2) = CAST(1.80 + ((@i % 22) * 0.10) AS DECIMAL(3,2));
    DECLARE @totalCredits INT = 108 + (@i % 31);
    DECLARE @failedCredits INT = @i % 6;
    DECLARE @result TINYINT =
        CASE
            WHEN @gpa >= 2.00 AND @totalCredits >= 120 AND @failedCredits <= 3 THEN 1
            ELSE 0
        END;
    DECLARE @classification TINYINT =
        CASE
            WHEN @result = 0 THEN 4
            WHEN @gpa >= 3.60 THEN 1
            WHEN @gpa >= 3.20 THEN 2
            WHEN @gpa >= 2.50 THEN 3
            ELSE 4
        END;
    DECLARE @note NVARCHAR(MAX) =
        CASE
            WHEN @result = 1 THEN N'Đủ điều kiện tốt nghiệp'
            WHEN @gpa < 2.00 THEN N'GPA chưa đạt yêu cầu'
            WHEN @totalCredits < 120 THEN N'Thiếu tín chỉ tích lũy'
            WHEN @failedCredits > 3 THEN N'Tín chỉ rớt vượt mức cho phép'
            ELSE N'Chưa đủ điều kiện tốt nghiệp'
        END;

    IF NOT EXISTS (SELECT 1 FROM students WHERE student_code = @studentCode)
    BEGIN
        INSERT INTO students (
            id,
            student_code,
            full_name,
            email,
            date_of_birth,
            gender,
            class_name,
            cohort,
            gpa,
            total_credits,
            failed_credits,
            english_status,
            it_status,
            created_at,
            updated_at,
            is_active
        )
        VALUES (
            NEWID(),
            @studentCode,
            @fullName,
            @email,
            DATEADD(DAY, @i, '2005-01-01'),
            CASE WHEN @i % 2 = 0 THEN N'Nữ' ELSE N'Nam' END,
            @className,
            N'ST23',
            @gpa,
            @totalCredits,
            @failedCredits,
            CASE WHEN @i % 5 = 0 THEN N'Chưa đạt' ELSE N'TOEIC 450' END,
            CASE WHEN @i % 7 = 0 THEN N'Chưa đạt' ELSE N'Đạt' END,
            SYSDATETIME(),
            SYSDATETIME(),
            1
        );
    END

    IF NOT EXISTS (SELECT 1 FROM graduation_results WHERE student_id = @studentCode)
    BEGIN
        INSERT INTO graduation_results (
            id,
            student_id,
            condition_id,
            gpa,
            total_credits,
            failed_credits,
            result,
            classification,
            decision_date,
            reviewer,
            note,
            created_at,
            updated_at,
            is_active
        )
        VALUES (
            NEWID(),
            @studentCode,
            @conditionST23,
            @gpa,
            @totalCredits,
            @failedCredits,
            @result,
            @classification,
            CAST(GETDATE() AS DATE),
            NULL,
            @note,
            SYSDATETIME(),
            SYSDATETIME(),
            1
        );
    END

    SET @i += 1;
END
GO

SELECT COUNT(*) AS st23_students_count
FROM students
WHERE cohort = N'ST23';

SELECT COUNT(*) AS st23_results_count
FROM graduation_results
WHERE student_id LIKE N'ST23-%';
GO
