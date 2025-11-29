package com.shms.controller;

import com.shms.entity.Patient;
import com.shms.entity.User;
import com.shms.repository.PatientRepository;
import com.shms.repository.UserRepository;
import com.shms.service.PatientService;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Optional;

@Controller
@RequestMapping("/patients")
public class PatientController {

    private final PatientService patientService;
    private final PatientRepository patientRepository;
    private final UserRepository userRepository;

    public PatientController(PatientService patientService, PatientRepository patientRepository, UserRepository userRepository) {
        this.patientService = patientService;
        this.patientRepository = patientRepository;
        this.userRepository = userRepository;
    }

    // 🩺 List all patients
    @GetMapping
    public String listPatients(Model model) {
        model.addAttribute("patients", patientService.getAllPatients());
        return "patients";  //
    }

    // ➕ Show form to add a patient
    @GetMapping("/add")
    public String showAddForm(Model model) {
        model.addAttribute("patient", new Patient());
        model.addAttribute("user", new User());
        return "patient_form";
    }

    // 💾 Save a new patient
    @PostMapping("/add")
    public String savePatient(@ModelAttribute("patient") Patient patient,
                              @ModelAttribute("user") User user) {

        user.setRole("PATIENT");
        User savedUser = userRepository.save(user);

        patient.setUser(savedUser);
        patientService.savePatient(patient);

        return "redirect:/patients";
    }

    // ✏️ Edit patient form
    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable Long id, Model model) {
        Patient patient = patientService.getPatientById(id);
        model.addAttribute("patient", patient);
        model.addAttribute("user", patient.getUser());
        return "patient_form";
    }

    // 💾 Update patient
    @PostMapping("/update/{id}")
    public String updatePatient(@PathVariable Long id,
                                @ModelAttribute("patient") Patient patient,
                                @ModelAttribute("user") User user) {

        Patient existing = patientService.getPatientById(id);
        User existingUser = existing.getUser();

        // Update user info
        existingUser.setUsername(user.getUsername());
        existingUser.setEmail(user.getEmail());
        if (user.getPassword() != null && !user.getPassword().isEmpty()) {
            existingUser.setPassword(user.getPassword());
        }
        userRepository.save(existingUser);

        // Update patient info
        existing.setFullName(patient.getFullName());
        existing.setGender(patient.getGender());
        existing.setAge(patient.getAge());
        existing.setPhone(patient.getPhone());
        existing.setAddress(patient.getAddress());
        existing.setEmail(patient.getEmail());
        existing.setMedicalHistory(patient.getMedicalHistory());

        patientService.savePatient(existing);
        return "redirect:/patients";
    }

    // ❌ Delete patient
    @GetMapping("/delete/{id}")
    public String deletePatient(@PathVariable("id") Long id, RedirectAttributes redirectAttributes) {
        Optional<Patient> optPatient = patientRepository.findById(id);

        if (optPatient.isPresent()) {
            Patient patient = optPatient.get();
            User user = patient.getUser();






            try {
                // Delete patient first (foreign key constraint)
                patientRepository.delete(patient);
                redirectAttributes.addFlashAttribute("success", "Patient deleted successfully!");
                // Delete linked user
                if (user != null) {
                    userRepository.delete(user);
                }
            } catch (DataIntegrityViolationException e) {
                redirectAttributes.addFlashAttribute("error",
                        "Cannot delete patient. They have scheduled appointments.");
            }

       } else {
            redirectAttributes.addFlashAttribute("error", "Patient not found for ID: " + id);
        }

        return "redirect:/patients";
    }



}
