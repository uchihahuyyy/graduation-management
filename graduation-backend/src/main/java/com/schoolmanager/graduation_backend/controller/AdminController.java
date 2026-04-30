package com.schoolmanager.graduation_backend.controller;

import com.schoolmanager.graduation_backend.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.UUID;

@Controller
@RequestMapping("/admin/users")
public class AdminController {

    @Autowired
    private UserService userService;

    @GetMapping
    public String listUsers(Model model) {
        model.addAttribute("users", userService.getAllUsers());
        return "admin_users";
    }

    @PostMapping("/grant-admin/{id}")
    public String grantAdmin(@PathVariable UUID id, RedirectAttributes redirectAttributes) {
        try {
            userService.grantAdminRole(id);
            redirectAttributes.addFlashAttribute("successMsg", "Đã cấp quyền Admin thành công!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMsg", e.getMessage());
        }
        return "redirect:/admin/users";
    }

    @PostMapping("/revoke-admin/{id}")
    public String revokeAdmin(@PathVariable UUID id, RedirectAttributes redirectAttributes) {
        try {
            userService.revokeAdminRole(id);
            redirectAttributes.addFlashAttribute("successMsg", "Đã thu hồi quyền Admin thành công!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMsg", e.getMessage());
        }
        return "redirect:/admin/users";
    }

    @PostMapping("/delete/{id}")
    public String deleteUser(@PathVariable UUID id, RedirectAttributes redirectAttributes) {
        try {
            userService.deleteUser(id);
            redirectAttributes.addFlashAttribute("successMsg", "Đã xóa người dùng thành công!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMsg", e.getMessage());
        }
        return "redirect:/admin/users";
    }

    @PostMapping("/toggle-active/{id}")
    public String toggleActive(@PathVariable UUID id, RedirectAttributes redirectAttributes) {
        try {
            userService.toggleUserActive(id);
            redirectAttributes.addFlashAttribute("successMsg", "Đã thay đổi trạng thái tài khoản!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMsg", e.getMessage());
        }
        return "redirect:/admin/users";
    }
}
