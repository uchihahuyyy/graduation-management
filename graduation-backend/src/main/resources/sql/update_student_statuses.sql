USE school23;
GO

IF COL_LENGTH('students', 'status') IS NULL
BEGIN
    ALTER TABLE students ADD status NVARCHAR(50) DEFAULT N'Đang học';
END
GO

UPDATE s
SET status =
    CASE
        WHEN gr.result = 1 THEN N'Đủ điều kiện TN'
        WHEN gr.result = 0 AND ISNULL(gr.failed_credits, s.failed_credits) > 0 THEN N'Nợ môn'
        WHEN gr.result = 0 AND ISNULL(gr.gpa, s.gpa) < 2.00 THEN N'Cảnh báo học vụ'
        WHEN gr.result IS NULL THEN N'Chờ xét TN'
        ELSE N'Đang học'
    END
FROM students s
LEFT JOIN graduation_results gr ON gr.student_id = s.id
WHERE s.status IS NULL
   OR LTRIM(RTRIM(s.status)) = N''
   OR s.status IN (N'Active', N'Inactive');
GO

SELECT student_code, full_name, status
FROM students
ORDER BY student_code;
GO
