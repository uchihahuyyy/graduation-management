package com.schoolmanager.graduation_backend.service;

import com.schoolmanager.graduation_backend.dto.request.StudentRequestDTO;
import com.schoolmanager.graduation_backend.entity.Student;
import com.schoolmanager.graduation_backend.repository.StudentRepository;
import jakarta.transaction.Transactional;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayOutputStream;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.UUID;

@Service
public class StudentService {

    private final StudentRepository studentRepository;
    private final ResultService resultService;

    public StudentService(StudentRepository studentRepository, ResultService resultService) {
        this.studentRepository = studentRepository;
        this.resultService = resultService;
    }

    public List<Student> findAll() {
        return studentRepository.findAll();
    }

    public Student findById(UUID id) {
        return studentRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Không tìm thấy sinh viên với ID: " + id));
    }

    public Student findByStudentCode(String studentCode) {
        if (studentCode == null || studentCode.isBlank()) {
            return null;
        }
        return studentRepository.findByStudentCode(studentCode.trim()).orElse(null);
    }

    @Transactional
    public Student save(StudentRequestDTO dto) {
        Student student = dto.getId() != null ? findById(dto.getId()) : new Student();
        String oldStudentCode = student.getStudentCode();
        student.setStudentCode(trim(dto.getStudentCode()));
        student.setFullName(trim(dto.getFullName()));
        student.setEmail(trim(dto.getEmail()));
        student.setDateOfBirth(dto.getDateOfBirth());
        student.setGender(trim(dto.getGender()));
        student.setClassName(trim(dto.getClassName()));
        student.setCohort(trim(dto.getCohort()));
        student.setGpa(dto.getGpa());
        student.setTotalCredits(dto.getTotalCredits());
        student.setFailedCredits(dto.getFailedCredits());
        student.setEnglishStatus(trim(dto.getEnglishStatus()));
        student.setItStatus(trim(dto.getItStatus()));
        student.setIsActive(dto.getIsActive() == null || dto.getIsActive());
        Student savedStudent = studentRepository.save(student);
        resultService.relinkStudentCode(oldStudentCode, savedStudent.getStudentCode());
        resultService.ensurePendingResult(savedStudent);
        return savedStudent;
    }

    public void softDelete(UUID id) {
        Student student = findById(id);
        student.setIsActive(false);
        student.setDeletedAt(LocalDateTime.now());
        studentRepository.save(student);
    }

    @Transactional
    public void hardDelete(UUID id) {
        Student student = findById(id);
        resultService.hardDeleteByStudentCode(student.getStudentCode());
        studentRepository.deleteById(id);
    }

    public int importExcel(MultipartFile file) {
        int imported = 0;
        try (Workbook workbook = new XSSFWorkbook(file.getInputStream())) {
            Sheet sheet = workbook.getSheetAt(0);
            for (int i = 1; i <= sheet.getLastRowNum(); i++) {
                Row row = sheet.getRow(i);
                if (row == null || readString(row, 0).isBlank()) {
                    continue;
                }
                String studentCode = readString(row, 0);
                Student student = studentRepository.findByStudentCode(studentCode).orElseGet(Student::new);
                student.setStudentCode(studentCode);
                student.setFullName(readString(row, 1));
                student.setEmail(readString(row, 2));
                student.setDateOfBirth(readDate(row, 3));
                student.setGender(readString(row, 4));
                student.setClassName(readString(row, 5));
                student.setCohort(readString(row, 6));
                student.setGpa(readBigDecimal(row, 7));
                student.setTotalCredits(readInteger(row, 8));
                student.setFailedCredits(readInteger(row, 9));
                student.setEnglishStatus(readString(row, 10));
                student.setItStatus(readString(row, 11));
                student.setIsActive(true);
                Student savedStudent = studentRepository.save(student);
                resultService.ensurePendingResult(savedStudent);
                imported++;
            }
        } catch (Exception e) {
            throw new RuntimeException("Import Excel thất bại: " + e.getMessage());
        }
        return imported;
    }

    public byte[] exportExcel() {
        try (Workbook workbook = new XSSFWorkbook(); ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            Sheet sheet = workbook.createSheet("students");
            String[] headers = {"Mã SV", "Họ tên", "Email", "Ngày sinh", "Giới tính", "Lớp", "Khóa", "GPA", "TC tích lũy", "TC rớt", "Tiếng Anh", "Tin học", "Trạng thái"};
            Row header = sheet.createRow(0);
            for (int i = 0; i < headers.length; i++) {
                header.createCell(i).setCellValue(headers[i]);
            }
            List<Student> students = findAll();
            for (int i = 0; i < students.size(); i++) {
                Student student = students.get(i);
                Row row = sheet.createRow(i + 1);
                row.createCell(0).setCellValue(value(student.getStudentCode()));
                row.createCell(1).setCellValue(value(student.getFullName()));
                row.createCell(2).setCellValue(value(student.getEmail()));
                row.createCell(3).setCellValue(student.getDateOfBirth() != null ? student.getDateOfBirth().toString() : "");
                row.createCell(4).setCellValue(value(student.getGender()));
                row.createCell(5).setCellValue(value(student.getClassName()));
                row.createCell(6).setCellValue(value(student.getCohort()));
                row.createCell(7).setCellValue(student.getGpa() != null ? student.getGpa().doubleValue() : 0);
                row.createCell(8).setCellValue(student.getTotalCredits() != null ? student.getTotalCredits() : 0);
                row.createCell(9).setCellValue(student.getFailedCredits() != null ? student.getFailedCredits() : 0);
                row.createCell(10).setCellValue(value(student.getEnglishStatus()));
                row.createCell(11).setCellValue(value(student.getItStatus()));
                row.createCell(12).setCellValue(Boolean.TRUE.equals(student.getIsActive()) ? "Active" : "Inactive");
            }
            workbook.write(out);
            return out.toByteArray();
        } catch (Exception e) {
            throw new RuntimeException("Export Excel thất bại: " + e.getMessage());
        }
    }

    private String trim(String value) {
        return value == null ? null : value.trim();
    }

    private String value(String value) {
        return value == null ? "" : value;
    }

    private String readString(Row row, int index) {
        Cell cell = row.getCell(index);
        if (cell == null) return "";
        return switch (cell.getCellType()) {
            case STRING -> cell.getStringCellValue().trim();
            case NUMERIC -> String.valueOf((long) cell.getNumericCellValue());
            case BOOLEAN -> String.valueOf(cell.getBooleanCellValue());
            default -> "";
        };
    }

    private Integer readInteger(Row row, int index) {
        Cell cell = row.getCell(index);
        if (cell == null) return null;
        if (cell.getCellType().name().equals("NUMERIC")) {
            return (int) cell.getNumericCellValue();
        }
        String value = readString(row, index);
        return value.isBlank() ? null : new BigDecimal(value).intValue();
    }

    private BigDecimal readBigDecimal(Row row, int index) {
        Cell cell = row.getCell(index);
        if (cell == null) return null;
        if (cell.getCellType().name().equals("NUMERIC")) {
            return BigDecimal.valueOf(cell.getNumericCellValue());
        }
        String value = readString(row, index);
        return value.isBlank() ? null : new BigDecimal(value);
    }

    private LocalDate readDate(Row row, int index) {
        Cell cell = row.getCell(index);
        if (cell == null) return null;
        if (cell.getCellType().name().equals("NUMERIC")) {
            return cell.getDateCellValue().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        }
        String value = readString(row, index);
        return value.isBlank() ? null : LocalDate.parse(value);
    }
}
