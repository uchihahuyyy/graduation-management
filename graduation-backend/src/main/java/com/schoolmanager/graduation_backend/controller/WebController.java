package com.schoolmanager.graduation_backend.controller;

import com.schoolmanager.graduation_backend.dto.request.ConditionRequestDTO;
import com.schoolmanager.graduation_backend.dto.request.ResultRequestDTO;
import com.schoolmanager.graduation_backend.entity.GraduationCondition;
import com.schoolmanager.graduation_backend.entity.GraduationResult;
import com.schoolmanager.graduation_backend.service.ConditionService;
import com.schoolmanager.graduation_backend.service.ResultService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.UUID;

@Controller
public class WebController {

    @Autowired
    private ConditionService conditionService;

    @Autowired
    private ResultService resultService;

    // ==================== TRANG CHỦ (Conditions) ====================

    @GetMapping({"/", "/index"})
    public String showIndexPage(Model model) {
        model.addAttribute("listConditions", conditionService.findAll());
        return "index";
    }

    // ==================== CONDITION CRUD ====================

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
    public String saveCondition(@ModelAttribute("conditionDTO") ConditionRequestDTO dto,
                                RedirectAttributes redirectAttributes) {
        try {
            conditionService.save(dto);
            redirectAttributes.addFlashAttribute("successMsg",
                dto.getId() != null ? "Cập nhật điều kiện thành công!" : "Thêm điều kiện thành công!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMsg", e.getMessage());
        }
        return "redirect:/";
    }

    @GetMapping("/conditions/delete/{id}")
    public String deleteCondition(@PathVariable UUID id, RedirectAttributes redirectAttributes) {
        try {
            conditionService.softDelete(id);
            redirectAttributes.addFlashAttribute("successMsg", "Xóa điều kiện thành công!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMsg", e.getMessage());
        }
        return "redirect:/";
    }

    // ==================== RESULT CRUD ====================

    @GetMapping("/results")
    public String showResultsPage(Model model) {
        model.addAttribute("listResults", resultService.findAll());
        return "results";
    }

    @GetMapping("/results/new")
    public String showCreateResultForm(Model model) {
        model.addAttribute("resultDTO", new ResultRequestDTO());
        model.addAttribute("isEdit", false);
        model.addAttribute("conditions", conditionService.findAll());
        return "form_result";
    }

    @GetMapping("/results/edit/{id}")
    public String showEditResultForm(@PathVariable UUID id, Model model) {
        GraduationResult entity = resultService.findById(id);

        ResultRequestDTO dto = new ResultRequestDTO();
        dto.setId(entity.getId() != null ? entity.getId().toString() : null);
        dto.setStudentId(entity.getStudentId()); // String trực tiếp
        dto.setConditionId(entity.getConditionId() != null ? entity.getConditionId().toString() : null);
        dto.setGpa(entity.getGpa());
        dto.setTotalCredits(entity.getTotalCredits());
        dto.setFailedCredits(entity.getFailedCredits());
        dto.setResult(entity.getResult());
        dto.setClassification(entity.getClassification());
        dto.setDecisionDate(entity.getDecisionDate());
        dto.setReviewer(entity.getReviewer() != null ? entity.getReviewer().toString() : null);
        dto.setNote(entity.getNote());

        model.addAttribute("resultDTO", dto);
        model.addAttribute("isEdit", true);
        model.addAttribute("conditions", conditionService.findAll());
        return "form_result";
    }

    @PostMapping("/results/save")
    public String saveResult(@ModelAttribute("resultDTO") ResultRequestDTO dto,
                             RedirectAttributes redirectAttributes) {
        try {
            resultService.save(dto);
            boolean isEdit = dto.getId() != null && !dto.getId().isBlank();
            redirectAttributes.addFlashAttribute("successMsg",
                isEdit ? "Cập nhật kết quả thành công!" : "Thêm kết quả thành công!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMsg", e.getMessage());
        }
        return "redirect:/results";
    }

    @GetMapping("/results/delete/{id}")
    public String deleteResult(@PathVariable UUID id, RedirectAttributes redirectAttributes) {
        try {
            resultService.softDelete(id);
            redirectAttributes.addFlashAttribute("successMsg", "Xóa kết quả thành công!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMsg", e.getMessage());
        }
        return "redirect:/results";
    }
}