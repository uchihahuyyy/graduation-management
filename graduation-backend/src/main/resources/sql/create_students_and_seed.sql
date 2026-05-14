USE school23;
GO

IF OBJECT_ID('students', 'U') IS NULL
BEGIN
    CREATE TABLE students (
        id UNIQUEIDENTIFIER PRIMARY KEY DEFAULT NEWID(),
        student_code NVARCHAR(20) NOT NULL UNIQUE,
        full_name NVARCHAR(120) NOT NULL,
        email NVARCHAR(120),
        date_of_birth DATE,
        gender NVARCHAR(20),
        class_name NVARCHAR(50),
        cohort NVARCHAR(10),
        gpa DECIMAL(3,2),
        total_credits INT,
        failed_credits INT,
        english_status NVARCHAR(100),
        it_status NVARCHAR(100),
        created_at DATETIME2,
        updated_at DATETIME2,
        created_by UNIQUEIDENTIFIER,
        updated_by UNIQUEIDENTIFIER,
        deleted_at DATETIME2,
        deleted_by UNIQUEIDENTIFIER,
        is_active BIT DEFAULT 1
    );
END
GO

IF NOT EXISTS (SELECT 1 FROM students WHERE student_code = N'K21-000001')
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
    VALUES
    (NEWID(), N'K21-000001', N'Nguyễn Văn An', 'an.nguyen@example.com', '2003-01-15', N'Nam', N'DCT121A', N'K21', 3.60, 128, 0, N'B1', N'Đạt', SYSDATETIME(), SYSDATETIME(), 1),
    (NEWID(), N'K21-000002', N'Trần Thị Bình', 'binh.tran@example.com', '2003-03-22', N'Nữ', N'DCT121A', N'K21', 3.20, 125, 0, N'TOEIC 520', N'Đạt', SYSDATETIME(), SYSDATETIME(), 1),
    (NEWID(), N'K21-000003', N'Lê Quốc Cường', 'cuong.le@example.com', '2003-07-10', N'Nam', N'DCT121B', N'K21', 2.75, 121, 0, N'B1', N'Đạt', SYSDATETIME(), SYSDATETIME(), 1),
    (NEWID(), N'K21-000004', N'Phạm Minh Duy', 'duy.pham@example.com', '2003-05-18', N'Nam', N'DCT121B', N'K21', 1.95, 126, 0, N'B1', N'Đạt', SYSDATETIME(), SYSDATETIME(), 1),
    (NEWID(), N'K22-000001', N'Đặng Hoàng Khang', 'khang.dang@example.com', '2004-02-11', N'Nam', N'DCT122A', N'K22', 3.45, 130, 1, N'TOEIC 510', N'Đạt', SYSDATETIME(), SYSDATETIME(), 1),
    (NEWID(), N'K22-000002', N'Bùi Ngọc Lan', 'lan.bui@example.com', '2004-04-04', N'Nữ', N'DCT122A', N'K22', 2.80, 126, 2, N'B1', N'Đạt', SYSDATETIME(), SYSDATETIME(), 1),
    (NEWID(), N'K22-000003', N'Hoàng Gia Minh', 'minh.hoang@example.com', '2004-08-20', N'Nam', N'DCT122B', N'K22', 2.10, 124, 2, N'B1', N'Đạt', SYSDATETIME(), SYSDATETIME(), 1),
    (NEWID(), N'K22-000004', N'Ngô Phương Nhi', 'nhi.ngo@example.com', '2004-10-12', N'Nữ', N'DCT122B', N'K22', 2.65, 128, 5, N'TOEIC 530', N'Đạt', SYSDATETIME(), SYSDATETIME(), 1),
    (NEWID(), N'K23-000001', N'Đỗ Thành Phúc', 'phuc.do@example.com', '2004-12-01', N'Nam', N'DCT123A', N'K23', 3.85, 135, 0, N'TOEIC 700', N'Đạt', SYSDATETIME(), SYSDATETIME(), 1),
    (NEWID(), N'K23-000002', N'Võ Thị Hạnh', 'hanh.vo@example.com', '2004-09-02', N'Nữ', N'DCT123A', N'K23', 2.15, 132, 0, N'B1', N'Đạt', SYSDATETIME(), SYSDATETIME(), 1);
END
GO

SELECT * FROM students;
GO
