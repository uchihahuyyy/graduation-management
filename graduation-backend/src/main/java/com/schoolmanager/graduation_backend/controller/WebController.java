package com.schoolmanager.graduation_backend.controller;

import com.schoolmanager.graduation_backend.dto.request.ConditionRequestDTO;
import com.schoolmanager.graduation_backend.dto.request.ResultRequestDTO;
import com.schoolmanager.graduation_backend.dto.request.StudentRequestDTO;
import com.schoolmanager.graduation_backend.dto.view.GraduationResultView;
import com.schoolmanager.graduation_backend.entity.GraduationCondition;
import com.schoolmanager.graduation_backend.entity.GraduationResult;
import com.schoolmanager.graduation_backend.entity.Student;
import com.schoolmanager.graduation_backend.service.ConditionService;
import com.schoolmanager.graduation_backend.service.ResultService;
import com.schoolmanager.graduation_backend.service.StudentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Controller
public class WebController {

    @Autowired
    private ConditionService conditionService;

    @Autowired
    private ResultService resultService;

    @Autowired
    private StudentService studentService;

    @GetMapping({"/", "/index", "/dashboard"})
    public String showDashboard(Model model) {
        List<GraduationResult> results = resultService.findAll();
        List<GraduationCondition> conditions = conditionService.findAll();

        model.addAttribute("totalStudents", studentService.findAll().size());
        model.addAttribute("qualifiedCount", results.stream().filter(result -> result.getResult() != null && result.getResult() == 1).count());
        model.addAttribute("notQualifiedCount", results.stream().filter(result -> result.getResult() != null && result.getResult() == 0).count());
        model.addAttribute("activeConditionCount", conditions.stream().filter(condition -> Boolean.TRUE.equals(condition.getIsActive())).count());
        model.addAttribute("totalResultCount", results.size());
        model.addAttribute("excellentCount", results.stream().filter(result -> result.getClassification() != null && result.getClassification() == 1).count());
        model.addAttribute("goodCount", results.stream().filter(result -> result.getClassification() != null && result.getClassification() == 2).count());
        model.addAttribute("fairCount", results.stream().filter(result -> result.getClassification() != null && result.getClassification() == 3).count());
        model.addAttribute("averageCount", results.stream().filter(result -> result.getClassification() != null && result.getClassification() == 4).count());
        return "dashboard";
    }

    @GetMapping("/students")
    public String showStudentsPage(Model model) {
        model.addAttribute("students", studentService.findAll());
        return "students";
    }

    @GetMapping("/students/new")
    public String showCreateStudentForm(Model model) {
        model.addAttribute("studentDTO", new StudentRequestDTO());
        model.addAttribute("isEdit", false);
        return "form_student";
    }

    @GetMapping("/students/edit/{id}")
    public String showEditStudentForm(@PathVariable UUID id, Model model) {
        model.addAttribute("studentDTO", toStudentDTO(studentService.findById(id)));
        model.addAttribute("isEdit", true);
        return "form_student";
    }

    @GetMapping("/students/detail/{id}")
    public String showStudentDetail(@PathVariable UUID id, Model model) {
        Student student = studentService.findById(id);
        model.addAttribute("student", student);
        model.addAttribute("graduationResult", resultService.findByStudentId(student.getStudentCode()).stream().findFirst().orElse(null));
        return "student_detail";
    }

    @PostMapping("/students/save")
    public String saveStudent(@ModelAttribute("studentDTO") StudentRequestDTO dto, RedirectAttributes redirectAttributes) {
        try {
            studentService.save(dto);
            redirectAttributes.addFlashAttribute("successMsg", dto.getId() != null ? "Cập nhật sinh viên thành công!" : "Thêm sinh viên thành công!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMsg", e.getMessage());
        }
        return "redirect:/students";
    }

    @GetMapping("/students/delete/{id}")
    public String deleteStudent(@PathVariable UUID id, RedirectAttributes redirectAttributes) {
        try {
            studentService.hardDelete(id);
            redirectAttributes.addFlashAttribute("successMsg", "Đã xóa sinh viên và kết quả liên quan!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMsg", e.getMessage());
        }
        return "redirect:/students";
    }

    @PostMapping("/students/import")
    public String importStudents(@RequestParam("file") MultipartFile file, RedirectAttributes redirectAttributes) {
        try {
            int imported = studentService.importExcel(file);
            redirectAttributes.addFlashAttribute("successMsg", "Import thành công " + imported + " sinh viên!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMsg", e.getMessage());
        }
        return "redirect:/students";
    }

    @GetMapping("/students/export")
    public ResponseEntity<byte[]> exportStudents() {
        byte[] data = studentService.exportExcel();
        return ResponseEntity.ok()
            .header(HttpHeaders.CONTENT_DISPOSITION, ContentDisposition.attachment().filename("students.xlsx").build().toString())
            .contentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
            .body(data);
    }

    @GetMapping("/conditions")
    public String showConditionsPage(Model model) {
        model.addAttribute("listConditions", conditionService.findAll());
        model.addAttribute("conditionDTO", new ConditionRequestDTO());
        return "conditions";
    }

    @GetMapping("/conditions/new")
    public String showCreateConditionForm(Model model) {
        model.addAttribute("conditionDTO", new ConditionRequestDTO());
        model.addAttribute("isEdit", false);
        return "form_condition";
    }

    @GetMapping("/conditions/edit/{id}")
    public String showEditConditionForm(@PathVariable UUID id, Model model) {
        GraduationCondition entity = conditionService.findById(id);
        ConditionRequestDTO dto = new ConditionRequestDTO();
        dto.setId(entity.getId());
        dto.setProgramId(entity.getProgramId());
        dto.setAppliedCohort(entity.getAppliedCohort());
        dto.setMinTotalCredits(entity.getMinTotalCredits());
        dto.setMinGpa(entity.getMinGpa());
        dto.setMaxFailedCredits(entity.getMaxFailedCredits());
        dto.setEnglishRequirement(entity.getEnglishRequirement());
        dto.setItRequirement(entity.getItRequirement());
        dto.setConductRequired(entity.getConductRequired());
        dto.setNote(entity.getNote());

        model.addAttribute("conditionDTO", dto);
        model.addAttribute("isEdit", true);
        return "form_condition";
    }

    @PostMapping("/conditions/save")
    public String saveCondition(@ModelAttribute("conditionDTO") ConditionRequestDTO dto, RedirectAttributes redirectAttributes) {
        try {
            conditionService.save(dto);
            redirectAttributes.addFlashAttribute("successMsg", dto.getId() != null ? "Cập nhật điều kiện thành công!" : "Thêm điều kiện thành công!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMsg", e.getMessage());
        }
        return "redirect:/conditions";
    }

    @GetMapping("/conditions/delete/{id}")
    public String deleteCondition(@PathVariable UUID id, RedirectAttributes redirectAttributes) {
        try {
            conditionService.hardDelete(id);
            redirectAttributes.addFlashAttribute("successMsg", "Đã xóa điều kiện khỏi database!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMsg", e.getMessage());
        }
        return "redirect:/conditions";
    }

    @GetMapping("/assessment")
    public String showAssessmentPage(Model model) {
        resultService.ensurePendingResults(studentService.findAll());
        model.addAttribute("listResults", resultService.findAll());
        model.addAttribute("resultViews", buildStudentResultViews());
        model.addAttribute("students", studentService.findAll());
        model.addAttribute("conditions", conditionService.findAll());
        return "assessment";
    }

    @GetMapping("/results")
    public String showResultsPage(Model model) {
        resultService.ensurePendingResults(studentService.findAll());
        model.addAttribute("listResults", resultService.findAll());
        model.addAttribute("resultViews", buildStudentResultViews());
        model.addAttribute("resultDTO", new ResultRequestDTO());
        model.addAttribute("students", studentService.findAll());
        model.addAttribute("conditions", conditionService.findAll());
        return "results";
    }

    @GetMapping("/results/new")
    public String showCreateResultForm(@RequestParam(value = "studentCode", required = false) String studentCode, Model model) {
        ResultRequestDTO dto = new ResultRequestDTO();
        Student student = studentService.findByStudentCode(studentCode);
        if (student != null) {
            dto.setStudentId(student.getStudentCode());
            dto.setGpa(student.getGpa());
            dto.setTotalCredits(student.getTotalCredits());
            dto.setFailedCredits(student.getFailedCredits());
        }
        model.addAttribute("resultDTO", dto);
        model.addAttribute("isEdit", false);
        model.addAttribute("students", studentService.findAll());
        model.addAttribute("conditions", conditionService.findAll());
        return "form_result";
    }

    @GetMapping("/results/edit/{id}")
    public String showEditResultForm(@PathVariable UUID id, Model model) {
        GraduationResult entity = resultService.findById(id);
        ResultRequestDTO dto = toResultDTO(entity);
        model.addAttribute("resultDTO", dto);
        model.addAttribute("isEdit", true);
        model.addAttribute("students", studentService.findAll());
        model.addAttribute("conditions", conditionService.findAll());
        return "form_result";
    }

    @GetMapping("/results/detail/{id}")
    public String showResultDetail(@PathVariable UUID id, Model model) {
        GraduationResult result = resultService.findById(id);
        GraduationCondition condition = result.getConditionId() != null ? conditionService.findById(result.getConditionId()) : null;
        model.addAttribute("result", result);
        model.addAttribute("student", studentService.findByStudentCode(result.getStudentId()));
        model.addAttribute("condition", condition);
        model.addAttribute("gpaPassed", condition != null && result.getGpa() != null && condition.getMinGpa() != null && result.getGpa().compareTo(condition.getMinGpa()) >= 0);
        model.addAttribute("creditPassed", condition != null && result.getTotalCredits() != null && condition.getMinTotalCredits() != null && result.getTotalCredits() >= condition.getMinTotalCredits());
        model.addAttribute("failedCreditPassed", condition != null && result.getFailedCredits() != null && condition.getMaxFailedCredits() != null && result.getFailedCredits() <= condition.getMaxFailedCredits());
        model.addAttribute("englishPassed", condition == null || condition.getEnglishRequirement() == null || !condition.getEnglishRequirement().isBlank());
        model.addAttribute("itPassed", condition == null || condition.getItRequirement() == null || !condition.getItRequirement().isBlank());
        return "result_detail";
    }

    @PostMapping("/results/save")
    public String saveResult(@ModelAttribute("resultDTO") ResultRequestDTO dto, RedirectAttributes redirectAttributes) {
        try {
            resultService.save(dto);
            boolean isEdit = dto.getId() != null && !dto.getId().isBlank();
            redirectAttributes.addFlashAttribute("successMsg", isEdit ? "Cập nhật kết quả thành công!" : "Thêm kết quả thành công!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMsg", e.getMessage());
        }
        return "redirect:/results";
    }

    @GetMapping("/results/delete/{id}")
    public String deleteResult(@PathVariable UUID id, RedirectAttributes redirectAttributes) {
        try {
            resultService.hardDelete(id);
            redirectAttributes.addFlashAttribute("successMsg", "Đã xóa kết quả khỏi database!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMsg", e.getMessage());
        }
        return "redirect:/results";
    }

    @GetMapping("/reports")
    public String showReports(Model model) {
        model.addAttribute("students", studentService.findAll());
        model.addAttribute("listResults", resultService.findAll());
        return "reports";
    }

    private List<GraduationResultView> buildStudentResultViews() {
        Map<String, GraduationResult> resultByStudentCode = resultService.findAll().stream()
            .filter(result -> result.getStudentId() != null && !result.getStudentId().isBlank())
            .collect(Collectors.toMap(GraduationResult::getStudentId, result -> result, (first, second) -> first));

        return studentService.findAll().stream()
            .map(student -> new GraduationResultView(resultByStudentCode.get(student.getStudentCode()), student))
            .collect(Collectors.toList());
    }

    private StudentRequestDTO toStudentDTO(Student student) {
        StudentRequestDTO dto = new StudentRequestDTO();
        dto.setId(student.getId());
        dto.setStudentCode(student.getStudentCode());
        dto.setFullName(student.getFullName());
        dto.setEmail(student.getEmail());
        dto.setDateOfBirth(student.getDateOfBirth());
        dto.setGender(student.getGender());
        dto.setClassName(student.getClassName());
        dto.setCohort(student.getCohort());
        dto.setGpa(student.getGpa());
        dto.setTotalCredits(student.getTotalCredits());
        dto.setFailedCredits(student.getFailedCredits());
        dto.setEnglishStatus(student.getEnglishStatus());
        dto.setItStatus(student.getItStatus());
        dto.setIsActive(student.getIsActive());
        return dto;
    }

    private ResultRequestDTO toResultDTO(GraduationResult entity) {
        ResultRequestDTO dto = new ResultRequestDTO();
        dto.setId(entity.getId() != null ? entity.getId().toString() : null);
        dto.setStudentId(entity.getStudentId());
        dto.setConditionId(entity.getConditionId() != null ? entity.getConditionId().toString() : null);
        dto.setGpa(entity.getGpa());
        dto.setTotalCredits(entity.getTotalCredits());
        dto.setFailedCredits(entity.getFailedCredits());
        dto.setResult(entity.getResult());
        dto.setClassification(entity.getClassification());
        dto.setDecisionDate(entity.getDecisionDate());
        dto.setReviewer(entity.getReviewer() != null ? entity.getReviewer().toString() : null);
        dto.setNote(entity.getNote());
        return dto;
    }
}
