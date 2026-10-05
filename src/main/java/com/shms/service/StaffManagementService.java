package com.shms.service;

import com.shms.dto.StaffCreateRequest;
import com.shms.entity.Doctor;
import com.shms.entity.Role;
import com.shms.entity.User;
import com.shms.repository.DepartmentRepository;
import com.shms.repository.DoctorRepository;
import com.shms.repository.RoleRepository;
import com.shms.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.Locale;
import java.util.Set;

@Service
public class StaffManagementService {
    private static final Set<String> STAFF_ROLES = Set.of(
            "ADMIN", "DOCTOR", "RECEPTIONIST", "NURSE", "PHARMACIST", "LAB_TECHNICIAN"
    );

    private final UserRepository users;
    private final RoleRepository roles;
    private final DepartmentRepository departments;
    private final DoctorRepository doctors;
    private final PasswordEncoder encoder;

    public StaffManagementService(UserRepository users, RoleRepository roles,
                                  DepartmentRepository departments, DoctorRepository doctors,
                                  PasswordEncoder encoder) {
        this.users = users;
        this.roles = roles;
        this.departments = departments;
        this.doctors = doctors;
        this.encoder = encoder;
    }

    @Transactional
    public User createStaff(StaffCreateRequest request) {
        String roleName = request.role().trim().toUpperCase(Locale.ROOT);
        if (!STAFF_ROLES.contains(roleName)) {
            throw new IllegalArgumentException("Invalid staff role: " + roleName);
        }
        if (users.existsByEmailIgnoreCase(request.email())) {
            throw new IllegalArgumentException("Email is already registered");
        }

        Role role = roles.findByName(roleName)
                .orElseThrow(() -> new IllegalStateException("Role is missing: " + roleName));

        User user = new User();
        user.setEmail(request.email());
        user.setPassword(encoder.encode(request.password()));
        user.setFirstName(request.firstName());
        user.setLastName(request.lastName());
        user.setPhone(request.phone());
        user.setStatus(User.Status.ACTIVE);
        user.setRoles(new HashSet<>(Set.of(role)));
        User saved = users.save(user);

        if ("DOCTOR".equals(roleName)) {
            if (request.departmentId() == null) {
                throw new IllegalArgumentException("Department is required for a doctor");
            }
            Doctor doctor = new Doctor();
            doctor.setUser(saved);
            doctor.setDoctorNumber("DOC-" + String.format("%06d", saved.getId()));
            doctor.setDepartment(departments.findById(request.departmentId())
                    .orElseThrow(() -> new IllegalArgumentException("Department not found")));
            doctor.setSpecialization(request.specialization());
            doctor.setQualification(request.qualification());
            doctor.setLicenseNumber(request.licenseNumber());
            doctor.setExperienceYears(request.experienceYears() == null ? 0 : request.experienceYears());
            doctor.setStatus(Doctor.Status.ACTIVE);
            doctors.save(doctor);
        }
        return saved;
    }
}
