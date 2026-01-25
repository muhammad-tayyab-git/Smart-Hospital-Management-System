package com.shms.controller;

import com.shms.entity.Patient;
import com.shms.entity.User;
import com.shms.repository.PatientRepository;
import com.shms.repository.UserRepository;
import com.shms.service.ActivityService;
import com.shms.service.PatientService;
import jakarta.servlet.http.HttpServletRequest;
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
    private final ActivityService activityService;

    public PatientController(PatientService patientService, PatientRepository patientRepository,
                             UserRepository userRepository, ActivityService activityService) {
        this.patientService = patientService;
        this.patientRepository = patientRepository;
        this.userRepository = userRepository;
        this.activityService = activityService;
    }

    @GetMapping
    public String listPatients(Model model, HttpServletRequest request) {
        model.addAttribute("patients", patientService.getAllPatients());
        model.addAttribute("currentUri", request.getRequestURI());
//        activityService.publish("View Patients", "Viewed list of all patients", "info");
        return "patients";
    }

    @GetMapping("/add")
    public String showAddForm(Model model) {
        model.addAttribute("patient", new Patient());
        model.addAttribute("user", new User());
        return "patient_form";
    }

    @PostMapping("/add")
    public String savePatient(@ModelAttribute Patient patient, @ModelAttribute User user) {
        user.setRole("PATIENT");
        User savedUser = userRepository.save(user);
        patient.setUser(savedUser);
        patientService.savePatient(patient);

        activityService.publish("Add Patient", "Added new patient: " + patient.getFullName(), "success");

        return "redirect:/patients";
    }

    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable Long id, Model model,RedirectAttributes ra) {
        Patient patient = patientService.getPatientById(id);
        model.addAttribute("patient", patient);
        User user = patient.getUser();
        model.addAttribute("user", user);

        return "patient_form";
    }

    @PostMapping("/update/{id}")
    public String updatePatient(@PathVariable Long id,
                                @ModelAttribute Patient patient,
                                @ModelAttribute User user) {

        Patient existing = patientService.getPatientById(id);
        User existingUser = existing.getUser();
        existingUser.setUsername(user.getUsername());
        existingUser.setEmail(user.getEmail());
        if (user.getPassword() != null && !user.getPassword().isEmpty()) {
            existingUser.setPassword(user.getPassword());
        }
        userRepository.save(existingUser);

        existing.setFullName(patient.getFullName());
        existing.setGender(patient.getGender());
        existing.setAge(patient.getAge());
        existing.setPhone(patient.getPhone());
        existing.setAddress(patient.getAddress());
        existing.setEmail(patient.getEmail());
        existing.setMedicalHistory(patient.getMedicalHistory());
        patientService.savePatient(existing);

        activityService.publish("Update Patient", "(ADMIN) Updated patient: " + existing.getFullName(), "warning");

        return "redirect:/patients";
    }

    @GetMapping("/delete/{id}")
    public String deletePatient(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        Optional<Patient> optPatient = patientRepository.findById(id);

        if (optPatient.isPresent()) {
            Patient patient = optPatient.get();
            User user = patient.getUser();

            try {
                patientRepository.delete(patient);
                if (user != null) userRepository.delete(user);
                activityService.publish("Delete Patient", "(ADMIN) Deleted patient: " + patient.getFullName(), "danger");
                redirectAttributes.addFlashAttribute("success", "Patient deleted successfully!");
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
