package com.schoolmanager.graduation_backend.dto.view;

import com.schoolmanager.graduation_backend.entity.GraduationResult;
import com.schoolmanager.graduation_backend.entity.Student;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class GraduationResultView {
    private GraduationResult result;
    private Student student;
}
