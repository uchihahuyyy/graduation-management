package com.schoolmanager.graduation_backend.controller.api;

import com.schoolmanager.graduation_backend.dto.request.ResultRequestDTO;
import com.schoolmanager.graduation_backend.dto.response.ResultTableRowDTO;
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
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/graduation-results")
public class GraduationResultApiController {

    private final ResultService resultService;
    private final StudentService studentService;

    public GraduationResultApiController(ResultService resultService, StudentService studentService) {
        this.resultService = resultService;
        this.studentService = studentService;
    }

    @GetMapping
    public ResponseEntity<List<GraduationResult>> getAll() {
        return ResponseEntity.ok(resultService.findAll());
    }

    @GetMapping("/table")
    public ResponseEntity<List<ResultTableRowDTO>> getTableRows() {
        return ResponseEntity.ok(resultService.findAll().stream()
            .map(result -> {
                Student student = resolveResultStudent(result);
                return new ResultTableRowDTO(result, student);
            })
            .collect(Collectors.toList()));
    }

    @GetMapping("/by-student/{studentCode}")
    public ResponseEntity<List<GraduationResult>> getByStudentCode(@PathVariable String studentCode) {
        Student student = studentService.findByStudentCode(studentCode);
        if (student == null) {
            return ResponseEntity.ok(List.of());
        }

        return ResponseEntity.ok(resultService.findByStudentId(student.getId()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getById(@PathVariable UUID id) {
        try {
            return ResponseEntity.ok(resultService.findById(id));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    @PostMapping
    public ResponseEntity<GraduationResult> create(@RequestBody ResultRequestDTO dto) {
        dto.setId(null);
        return ResponseEntity.status(HttpStatus.CREATED).body(resultService.save(dto));
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable UUID id, @RequestBody ResultRequestDTO dto) {
        try {
            dto.setId(id.toString());
            return ResponseEntity.ok(resultService.save(dto));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable UUID id) {
        try {
            resultService.hardDelete(id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    private Student resolveResultStudent(GraduationResult result) {
        if (result == null || result.getStudentId() == null) {
            return null;
        }

        return studentService.findById(result.getStudentId());
    }
}
