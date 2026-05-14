package com.schoolmanager.graduation_backend.controller.api;

import com.schoolmanager.graduation_backend.dto.request.ConditionRequestDTO;
import com.schoolmanager.graduation_backend.entity.GraduationCondition;
import com.schoolmanager.graduation_backend.service.ConditionService;
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
@RequestMapping("/api/graduation-conditions")
public class GraduationConditionApiController {

    private final ConditionService conditionService;

    public GraduationConditionApiController(ConditionService conditionService) {
        this.conditionService = conditionService;
    }

    @GetMapping
    public ResponseEntity<List<GraduationCondition>> getAll() {
        return ResponseEntity.ok(conditionService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getById(@PathVariable UUID id) {
        try {
            return ResponseEntity.ok(conditionService.findById(id));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    @PostMapping
    public ResponseEntity<GraduationCondition> create(@RequestBody ConditionRequestDTO dto) {
        dto.setId(null);
        return ResponseEntity.status(HttpStatus.CREATED).body(conditionService.save(dto));
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable UUID id, @RequestBody ConditionRequestDTO dto) {
        try {
            dto.setId(id);
            return ResponseEntity.ok(conditionService.save(dto));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable UUID id) {
        try {
            conditionService.hardDelete(id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }
}
