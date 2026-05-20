USE school23;
GO

IF OBJECT_ID('programs', 'U') IS NULL
BEGIN
    CREATE TABLE programs (
        id UNIQUEIDENTIFIER PRIMARY KEY DEFAULT NEWID(),
        program_code NVARCHAR(20) NOT NULL UNIQUE,
        program_name NVARCHAR(120) NOT NULL,
        major_name NVARCHAR(120),
        education_level NVARCHAR(50),
        total_required_credits INT,
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

DECLARE @defaultProgramId UNIQUEIDENTIFIER;

SELECT TOP 1 @defaultProgramId = id
FROM programs
WHERE program_code = N'CNTT';

IF @defaultProgramId IS NULL
BEGIN
    SET @defaultProgramId = NEWID();

    INSERT INTO programs (
        id,
        program_code,
        program_name,
        major_name,
        education_level,
        total_required_credits,
        created_at,
        updated_at,
        is_active
    )
    VALUES (
        @defaultProgramId,
        N'CNTT',
        N'Chương trình Công nghệ thông tin',
        N'Công nghệ thông tin',
        N'Đại học',
        120,
        SYSDATETIME(),
        SYSDATETIME(),
        1
    );
END

IF COL_LENGTH('students', 'status') IS NULL
BEGIN
    ALTER TABLE students ADD status NVARCHAR(50) DEFAULT N'Đang học';
END

UPDATE students
SET status = N'Đang học'
WHERE status IS NULL OR LTRIM(RTRIM(status)) = N'';

UPDATE graduation_conditions
SET program_id = @defaultProgramId
WHERE program_id IS NULL
   OR NOT EXISTS (
        SELECT 1
        FROM programs p
        WHERE p.id = graduation_conditions.program_id
   );
GO

DECLARE @studentIdType SYSNAME;

SELECT @studentIdType = t.name
FROM sys.columns c
JOIN sys.types t ON c.user_type_id = t.user_type_id
WHERE c.object_id = OBJECT_ID('graduation_results')
  AND c.name = 'student_id';

IF @studentIdType IS NOT NULL AND @studentIdType <> N'uniqueidentifier'
BEGIN
    IF COL_LENGTH('graduation_results', 'student_uuid') IS NULL
    BEGIN
        ALTER TABLE graduation_results ADD student_uuid UNIQUEIDENTIFIER NULL;
    END;

    EXEC sp_executesql N'
        UPDATE gr
        SET student_uuid = s.id
        FROM graduation_results gr
        JOIN students s ON s.student_code = gr.student_id
        WHERE gr.student_uuid IS NULL;
    ';

    IF COL_LENGTH('graduation_results', 'student_code_legacy') IS NULL
    BEGIN
        EXEC sp_rename 'graduation_results.student_id', 'student_code_legacy', 'COLUMN';
    END;

    IF COL_LENGTH('graduation_results', 'student_id') IS NULL
       AND COL_LENGTH('graduation_results', 'student_uuid') IS NOT NULL
    BEGIN
        EXEC sp_rename 'graduation_results.student_uuid', 'student_id', 'COLUMN';
    END;
END

IF @studentIdType IS NULL
   AND COL_LENGTH('graduation_results', 'student_id') IS NULL
   AND COL_LENGTH('graduation_results', 'student_uuid') IS NOT NULL
BEGIN
    EXEC sp_rename 'graduation_results.student_uuid', 'student_id', 'COLUMN';
END
GO

IF NOT EXISTS (
    SELECT 1
    FROM sys.check_constraints
    WHERE name = 'CK_graduation_results_result'
)
BEGIN
    ALTER TABLE graduation_results
    ADD CONSTRAINT CK_graduation_results_result
    CHECK (result IS NULL OR result IN (0, 1));
END
GO

IF NOT EXISTS (
    SELECT 1
    FROM sys.check_constraints
    WHERE name = 'CK_graduation_results_classification'
)
BEGIN
    ALTER TABLE graduation_results
    ADD CONSTRAINT CK_graduation_results_classification
    CHECK (classification IS NULL OR classification IN (1, 2, 3, 4));
END
GO

IF NOT EXISTS (
    SELECT 1
    FROM sys.foreign_keys
    WHERE name = 'FK_graduation_conditions_programs'
)
BEGIN
    ALTER TABLE graduation_conditions
    ADD CONSTRAINT FK_graduation_conditions_programs
    FOREIGN KEY (program_id) REFERENCES programs(id);
END
GO

DECLARE @studentIdTypeForFk SYSNAME;

SELECT @studentIdTypeForFk = t.name
FROM sys.columns c
JOIN sys.types t ON c.user_type_id = t.user_type_id
WHERE c.object_id = OBJECT_ID('graduation_results')
  AND c.name = 'student_id';

IF @studentIdTypeForFk = N'uniqueidentifier'
   AND NOT EXISTS (
        SELECT 1
        FROM sys.foreign_keys
        WHERE name = 'FK_graduation_results_students'
   )
   AND NOT EXISTS (
        SELECT 1
        FROM graduation_results gr
        WHERE gr.student_id IS NOT NULL
          AND NOT EXISTS (SELECT 1 FROM students s WHERE s.id = gr.student_id)
   )
BEGIN
    ALTER TABLE graduation_results
    ADD CONSTRAINT FK_graduation_results_students
    FOREIGN KEY (student_id) REFERENCES students(id);
END
GO

IF NOT EXISTS (
    SELECT 1
    FROM sys.foreign_keys
    WHERE name = 'FK_graduation_results_conditions'
)
AND NOT EXISTS (
    SELECT 1
    FROM graduation_results gr
    WHERE gr.condition_id IS NOT NULL
      AND NOT EXISTS (SELECT 1 FROM graduation_conditions gc WHERE gc.id = gr.condition_id)
)
BEGIN
    ALTER TABLE graduation_results
    ADD CONSTRAINT FK_graduation_results_conditions
    FOREIGN KEY (condition_id) REFERENCES graduation_conditions(id);
END
GO

IF NOT EXISTS (SELECT 1 FROM sys.indexes WHERE name = 'IX_graduation_results_student_id')
BEGIN
    CREATE INDEX IX_graduation_results_student_id ON graduation_results(student_id);
END
GO

IF NOT EXISTS (SELECT 1 FROM sys.indexes WHERE name = 'IX_graduation_results_condition_id')
BEGIN
    CREATE INDEX IX_graduation_results_condition_id ON graduation_results(condition_id);
END
GO

IF NOT EXISTS (SELECT 1 FROM sys.indexes WHERE name = 'IX_graduation_conditions_program_id')
BEGIN
    CREATE INDEX IX_graduation_conditions_program_id ON graduation_conditions(program_id);
END
GO

IF OBJECT_ID('graduation_periods', 'U') IS NULL
BEGIN
    CREATE TABLE graduation_periods (
        id UNIQUEIDENTIFIER PRIMARY KEY DEFAULT NEWID(),
        name NVARCHAR(200) NOT NULL,
        start_date DATE,
        end_date DATE,
        status NVARCHAR(50),
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

IF COL_LENGTH('graduation_conditions', 'require_tuition_cleared') IS NULL
BEGIN
    ALTER TABLE graduation_conditions ADD require_tuition_cleared BIT;
END
GO

IF COL_LENGTH('graduation_results', 'period_id') IS NULL
BEGIN
    ALTER TABLE graduation_results ADD period_id UNIQUEIDENTIFIER;
END
GO

IF COL_LENGTH('graduation_results', 'status') IS NULL
BEGIN
    ALTER TABLE graduation_results ADD status NVARCHAR(50);
END
GO

IF COL_LENGTH('graduation_results', 'decision_number') IS NULL
BEGIN
    ALTER TABLE graduation_results ADD decision_number NVARCHAR(100);
END
GO

IF NOT EXISTS (
    SELECT 1
    FROM sys.foreign_keys
    WHERE name = 'FK_graduation_results_periods'
)
BEGIN
    ALTER TABLE graduation_results
    ADD CONSTRAINT FK_graduation_results_periods
    FOREIGN KEY (period_id) REFERENCES graduation_periods(id);
END
GO
