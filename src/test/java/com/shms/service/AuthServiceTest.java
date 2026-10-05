package com.shms.service;

import com.shms.entity.Patient;
import com.shms.entity.Role;
import com.shms.entity.User;
import com.shms.repository.PatientRepository;
import com.shms.repository.RoleRepository;
import com.shms.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {
    @Mock UserRepository users;
    @Mock PatientRepository patients;
    @Mock RoleRepository roles;
    @Mock PasswordEncoder encoder;
    @InjectMocks AuthService authService;

    @Test
    void registrationCreatesPatientAndUsesPatientRole() {
        User user = new User();
        user.setEmail("new@example.com");
        user.setPassword("plain-password");
        user.setFirstName("New");
        user.setLastName("Patient");
        Patient patient = new Patient();

        Role role = new Role();
        role.setName("PATIENT");
        when(users.existsByEmailIgnoreCase("new@example.com")).thenReturn(false);
        when(roles.findByName("PATIENT")).thenReturn(Optional.of(role));
        when(encoder.encode("plain-password")).thenReturn("hashed");
        when(users.save(any(User.class))).thenAnswer(invocation -> {
            User saved = invocation.getArgument(0);
            saved.setId(42L);
            return saved;
        });

        assertTrue(authService.registerPatient(user, patient));
        assertEquals("hashed", user.getPassword());
        assertEquals("PAT-000042", patient.getPatientNumber());
        assertSame(user, patient.getUser());
        assertTrue(user.getRoles().contains(role));
        verify(patients).save(patient);
    }

    @Test
    void duplicateEmailIsRejected() {
        User user = new User();
        user.setEmail("existing@example.com");
        user.setPassword("x");
        user.setFirstName("Existing");
        user.setLastName("Patient");

        when(users.existsByEmailIgnoreCase("existing@example.com")).thenReturn(true);

        assertFalse(authService.registerPatient(user, new Patient()));
        verify(users, never()).save(any(User.class));
        verifyNoInteractions(patients, roles, encoder);
    }
}
