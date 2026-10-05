package com.shms.entity;

import org.junit.jupiter.api.Test;

import java.util.HashSet;

import static org.junit.jupiter.api.Assertions.*;

class UserTest {
    @Test
    void roleSelectionIsDeterministic() {
        User user = new User();
        Role patient = new Role(); patient.setName("PATIENT");
        Role doctor = new Role(); doctor.setName("DOCTOR");
        user.setRoles(new HashSet<>());
        user.getRoles().add(patient);
        user.getRoles().add(doctor);

        assertEquals("DOCTOR", user.getRole());
    }

    @Test
    void emailIsNormalized() {
        User user = new User();
        user.setEmail("  TEST@Example.COM ");
        assertEquals("test@example.com", user.getEmail());
    }
}
