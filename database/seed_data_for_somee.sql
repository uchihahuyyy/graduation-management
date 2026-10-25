-- ==============================================================================
-- SCRIPT NẠP DỮ LIỆU ĐẦY ĐỦ CHO SOMEE (HOẶC BẤT KỲ DATABASE NÀO)
-- Chạy script này trong SSMS (khi đã kết nối tới Somee) hoặc giao diện web của Somee
-- ==============================================================================

-- 1. TẠO CÁC ROLE CƠ BẢN
IF NOT EXISTS (SELECT 1 FROM roles WHERE name = 'ROLE_SUPERADMIN')
    INSERT INTO roles (id, name, created_at, is_active) VALUES (NEWID(), 'ROLE_SUPERADMIN', SYSDATETIME(), 1);

IF NOT EXISTS (SELECT 1 FROM roles WHERE name = 'ROLE_ADMIN')
    INSERT INTO roles (id, name, created_at, is_active) VALUES (NEWID(), 'ROLE_ADMIN', SYSDATETIME(), 1);

IF NOT EXISTS (SELECT 1 FROM roles WHERE name = 'ROLE_USER')
    INSERT INTO roles (id, name, created_at, is_active) VALUES (NEWID(), 'ROLE_USER', SYSDATETIME(), 1);
GO

-- 2. TẠO TÀI KHOẢN SUPERADMIN (Pass: superadmin - mã hóa BCrypt)
DECLARE @superadminRoleId UNIQUEIDENTIFIER;
SELECT TOP 1 @superadminRoleId = id FROM roles WHERE name = 'ROLE_SUPERADMIN';

DECLARE @superadminUserId UNIQUEIDENTIFIER;
SELECT TOP 1 @superadminUserId = id FROM users WHERE username = 'superadmin';

IF @superadminUserId IS NULL
BEGIN
    SET @superadminUserId = NEWID();
    INSERT INTO users (id, username, password, full_name, email, is_active, created_at)
    VALUES (
        @superadminUserId,
        'superadmin',
        '$2a$10$wTqKj1023X83LqH0L95wpe1W28r7v1fK3o81Y8f9yZ2j1e7z4k6jS', -- superadmin
        N'System Super Administrator',
        'superadmin@graduation.edu.vn',
        1,
        SYSDATETIME()
    );
END

IF NOT EXISTS (SELECT 1 FROM user_roles WHERE user_id = @superadminUserId AND role_id = @superadminRoleId)
BEGIN
    INSERT INTO user_roles (user_id, role_id) VALUES (@superadminUserId, @superadminRoleId);
END
GO

-- 3. TẠO CHƯƠNG TRÌNH ĐÀO TẠO (PROGRAM)
DECLARE @programId UNIQUEIDENTIFIER;
SELECT TOP 1 @programId = id FROM programs WHERE program_code = N'CNTT';

IF @programId IS NULL
BEGIN
    SET @programId = NEWID();
    INSERT INTO programs (
        id, program_code, program_name, major_name, education_level,
        total_required_credits, created_at, updated_at, is_active
    )
    VALUES (
        @programId, N'CNTT', N'Chương trình Công nghệ thông tin', N'Công nghệ thông tin', N'Đại học',
        120, SYSDATETIME(), SYSDATETIME(), 1
    );
END

-- 4. TẠO ĐIỀU KIỆN TỐT NGHIỆP (GRADUATION_CONDITIONS)
DECLARE @conditionST23 UNIQUEIDENTIFIER;
SELECT TOP 1 @conditionST23 = id FROM graduation_conditions WHERE applied_cohort = N'ST23' ORDER BY created_at DESC;

IF @conditionST23 IS NULL
BEGIN
    SET @conditionST23 = NEWID();
    INSERT INTO graduation_conditions (
        id, program_id, applied_cohort, min_total_credits, min_gpa, max_failed_credits,
        english_requirement, it_requirement, conduct_required, note, created_at, updated_at, is_active
    )
    VALUES (
        @conditionST23, @programId, N'ST23', 120, 2.00, 3,
        N'TOEIC 450 hoặc B1', N'Đạt MOS/IC3', N'Trung bình trở lên',
        N'Điều kiện xét tốt nghiệp áp dụng cho lớp ST23', SYSDATETIME(), SYSDATETIME(), 1
    );
END
GO

-- 5. NẠP 50 SINH VIÊN VÀ KẾT QUẢ XÉT TỐT NGHIỆP
DECLARE @conditionST23 UNIQUEIDENTIFIER;
SELECT TOP 1 @conditionST23 = id FROM graduation_conditions WHERE applied_cohort = N'ST23' ORDER BY created_at DESC;

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
    DECLARE @studentStatus NVARCHAR(50) =
        CASE
            WHEN @result = 1 AND @i % 10 = 0 THEN N'Đã tốt nghiệp'
            WHEN @result = 1 THEN N'Đủ điều kiện TN'
            WHEN @totalCredits < 120 OR @failedCredits > 3 THEN N'Nợ môn'
            WHEN @gpa < 2.00 THEN N'Cảnh báo học vụ'
            WHEN @i % 17 = 0 THEN N'Bảo lưu'
            WHEN @i % 23 = 0 THEN N'Thôi học'
            ELSE N'Đang học'
        END;

    DECLARE @currentStudentId UNIQUEIDENTIFIER;
    SET @currentStudentId = NULL;
    SELECT @currentStudentId = id FROM students WHERE student_code = @studentCode;

    IF @currentStudentId IS NULL
    BEGIN
        SET @currentStudentId = NEWID();
        INSERT INTO students (
            id, student_code, full_name, email, date_of_birth, gender,
            class_name, cohort, gpa, total_credits, failed_credits,
            english_status, it_status, status, created_at, updated_at, is_active
        )
        VALUES (
            @currentStudentId, @studentCode, @fullName, @email,
            DATEADD(DAY, -((@i * 37) % 1200), '2005-09-01'),
            CASE WHEN @i % 2 = 0 THEN N'Nam' ELSE N'Nữ' END,
            @className, N'ST23', @gpa, @totalCredits, @failedCredits,
            CASE WHEN @failedCredits <= 2 THEN N'Đạt TOEIC 500' ELSE N'Chưa nộp' END,
            CASE WHEN @failedCredits <= 1 THEN N'Đạt IC3' ELSE N'Chưa nộp' END,
            @studentStatus, SYSDATETIME(), SYSDATETIME(), 1
        );
    END

    IF NOT EXISTS (SELECT 1 FROM graduation_results WHERE student_id = @currentStudentId)
    BEGIN
        INSERT INTO graduation_results (
            id, student_id, condition_id, gpa, total_credits, failed_credits,
            result, classification, decision_date, note, created_at, updated_at, is_active
        )
        VALUES (
            NEWID(), @currentStudentId, @conditionST23, @gpa, @totalCredits, @failedCredits,
            @result, @classification,
            CASE WHEN @result = 1 THEN '2026-05-15' ELSE NULL END,
            @note, SYSDATETIME(), SYSDATETIME(), 1
        );
    END

    SET @i = @i + 1;
END
GO

-- 6. KIỂM TRA KẾT QUẢ SAU KHI NẠP
SELECT COUNT(*) AS total_students FROM students;
SELECT COUNT(*) AS total_graduation_results FROM graduation_results;
SELECT COUNT(*) AS total_conditions FROM graduation_conditions;
GO
