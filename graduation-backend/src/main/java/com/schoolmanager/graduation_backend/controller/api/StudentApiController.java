package com.schoolmanager.graduation_backend.controller.api;

import com.schoolmanager.graduation_backend.dto.request.StudentRequestDTO;
import com.schoolmanager.graduation_backend.entity.GraduationResult;
import com.schoolmanager.graduation_backend.entity.Student;
import com.schoolmanager.graduation_backend.service.ResultService;
import com.schoolmanager.graduation_backend.service.StudentService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/students")
public class StudentApiController {

    private final StudentService studentService;
    private final ResultService resultService;

    public StudentApiController(StudentService studentService, ResultService resultService) {
        this.studentService = studentService;
        this.resultService = resultService;
    }

    @GetMapping
    public ResponseEntity<List<Student>> getAll() {
        return ResponseEntity.ok(studentService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getById(@PathVariable UUID id) {
        try {
            return ResponseEntity.ok(studentService.findById(id));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    @GetMapping("/code/{studentCode}/graduation-results")
    public ResponseEntity<List<GraduationResult>> getGraduationResultsByStudentCode(@PathVariable String studentCode) {
        return ResponseEntity.ok(resultService.findByStudentId(studentCode));
    }

    @PostMapping
    public ResponseEntity<Student> create(@RequestBody StudentRequestDTO dto) {
        dto.setId(null);
        return ResponseEntity.status(HttpStatus.CREATED).body(studentService.save(dto));
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable UUID id, @RequestBody StudentRequestDTO dto) {
        try {
            dto.setId(id);
            return ResponseEntity.ok(studentService.save(dto));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable UUID id) {
        try {
            studentService.hardDelete(id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }
}
