package com.shms.controller;

import com.shms.dto.StaffCreateRequest;
import com.shms.entity.User;
import com.shms.service.StaffManagementService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/admin/api/staff")
public class AdminStaffController {
    private final StaffManagementService staff;
    public AdminStaffController(StaffManagementService staff) { this.staff = staff; }

    @PostMapping
    public ResponseEntity<?> create(@Valid @RequestBody StaffCreateRequest request) {
        try {
            User user = staff.createStaff(request);
            return ResponseEntity.status(HttpStatus.CREATED).body(Map.of(
                    "id", user.getId(), "email", user.getEmail(), "role", user.getRole()
            ));
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.badRequest().body(Map.of("error", ex.getMessage()));
        }
    }
}
