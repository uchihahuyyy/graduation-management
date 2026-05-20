# SQL Server setup for demo data

Use these scripts when a teammate wants to run the graduation module locally or on a shared SQL Server database and see data in the web UI.

## Run order

1. Create the database tables.

   Run the full schema script exported from SQL Server Management Studio. It must create these tables:

   - `programs`
   - `students`
   - `graduation_conditions`
   - `graduation_results`
   - `roles`
   - `users`
   - `user_roles`

2. Apply compatibility migration.

   ```sql
   :r migrate_graduation_schema.sql
   ```

3. Seed student data.

   ```sql
   :r seed_50_st23_students.sql
   ```

4. Verify the data.

   ```sql
   SELECT student_code, full_name, class_name, cohort, gpa, total_credits, status
   FROM students
   ORDER BY student_code;
   ```

5. Start the Spring Boot app and open:

   - `http://localhost:8080/students` to view the student table
   - `http://localhost:8080/results` to view graduation results
   - `http://localhost:8080/assessment` to view assessment rows

## Notes

- `seed_50_st23_students.sql` is safe to run more than once. It only inserts missing `ST23-*` students/results.
- `graduation_results.student_id` is a SQL Server `uniqueidentifier` and references `students.id`.
- The app also seeds login roles and the default `superadmin / superadmin` account on startup through `DataSeeder`.
