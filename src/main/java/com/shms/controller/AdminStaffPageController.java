package com.shms.controller;

import com.shms.dto.StaffCreateRequest;
import com.shms.entity.Role;
import com.shms.repository.DepartmentRepository;
import com.shms.repository.RoleRepository;
import com.shms.repository.UserRepository;
import com.shms.service.StaffManagementService;
import jakarta.validation.ValidationException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/admin/staff")
public class AdminStaffPageController {
    private final StaffManagementService staff;
    private final DepartmentRepository departments;
    private final RoleRepository roles;
    private final UserRepository users;

    public AdminStaffPageController(StaffManagementService staff, DepartmentRepository departments,
                                    RoleRepository roles, UserRepository users) {
        this.staff = staff; this.departments = departments; this.roles = roles; this.users = users;
    }

    @GetMapping
    public String page(Model model) {
        model.addAttribute("departments", departments.findAll());
        model.addAttribute("roles", List.of("ADMIN","DOCTOR","RECEPTIONIST","NURSE","PHARMACIST","LAB_TECHNICIAN"));
        model.addAttribute("staffUsers", users.findAll().stream().filter(u -> !"PATIENT".equals(u.getRole())).toList());
        return "admin-staff";
    }

    @PostMapping
    public String create(@RequestParam String email, @RequestParam String password,
                         @RequestParam String firstName, @RequestParam String lastName,
                         @RequestParam(required=false) String phone, @RequestParam String role,
                         @RequestParam(required=false) Long departmentId,
                         @RequestParam(required=false) String specialization,
                         @RequestParam(required=false) String qualification,
                         @RequestParam(required=false) String licenseNumber,
                         @RequestParam(required=false) Integer experienceYears,
                         RedirectAttributes ra) {
        try {
            staff.createStaff(new StaffCreateRequest(email, password, firstName, lastName, phone, role,
                    departmentId, specialization, qualification, licenseNumber, experienceYears));
            ra.addFlashAttribute("success", "Staff account created successfully.");
        } catch (Exception ex) {
            ra.addFlashAttribute("error", ex.getMessage() == null ? "Could not create staff account." : ex.getMessage());
        }
        return "redirect:/admin/staff";
    }
}
