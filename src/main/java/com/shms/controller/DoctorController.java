package com.shms.controller;

import com.shms.entity.AppointmentSlot;
import com.shms.entity.Doctor;
import com.shms.entity.Patient;
import com.shms.entity.User;
import com.shms.repository.AppointmentSlotRepository;
import com.shms.repository.DoctorRepository;
import com.shms.repository.UserRepository;
import com.shms.service.AppointmentService;
import com.shms.service.BillingService;
import com.shms.service.ActivityService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ResourceLoader;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("/doctors")
public class DoctorController {
    private final DoctorRepository doctorRepository;
    private final ResourceLoader resourceLoader;

    @Autowired
    private AppointmentService appointmentService;

    @Autowired
    private BillingService billingService;

    @Autowired
    private AppointmentSlotRepository slotRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ActivityService activityService;

    @Autowired
    public DoctorController(DoctorRepository doctorRepository, ResourceLoader resourceLoader,
                            AppointmentService appointmentService) {
        this.doctorRepository = doctorRepository;
        this.resourceLoader = resourceLoader;
        this.appointmentService = appointmentService;
    }

    @GetMapping
    public String list(Model model) {
        model.addAttribute("doctors", doctorRepository.findAll());
//        activityService.publish("View Doctors", "Viewed the list of doctors", "info");
        return "doctors";
    }

    @GetMapping("/addDoctor")
    public String addDoctor(Model model) {
        model.addAttribute("doctor", new Doctor());
        model.addAttribute("user", new User());
        return "doctor_form";
    }

    @PostMapping("/addDoctor")
    public String saveDoctor(@ModelAttribute Doctor doctor,
                             @ModelAttribute User user,
                             @RequestParam("imageFile") MultipartFile imageFile) throws IOException {

        // Save image
        String uploadDir = new File("uploads/images/").getAbsolutePath();
        new File(uploadDir).mkdirs();
        String fileName = System.currentTimeMillis() + "_" + imageFile.getOriginalFilename();
        File dest = new File(uploadDir, fileName);
        imageFile.transferTo(dest);

        // Save user and doctor
        user.setRole("DOCTOR");
        User savedUser = userRepository.save(user);
        doctor.setUser(savedUser);
        doctor.setImagePath("/uploads/images/" + fileName);
        doctor.setActive(true);
        Doctor savedDoctor = doctorRepository.save(doctor);

        // Generate slots
        appointmentService.generateSlotsForDoctor(savedDoctor);

        activityService.publish("Add Doctor", "(ADMIN) Added new doctor: " + doctor.getFullName(), "success");

        return "redirect:/doctors";
    }

    @GetMapping("/edit/{id}")
    public String editDoctorForm(@PathVariable Long id, Model model) {
        Doctor doctor = doctorRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid doctor ID: " + id));
        model.addAttribute("doctor", doctor);
        model.addAttribute("user", doctor.getUser());
        return "doctor_form";
    }

    @PostMapping("/updateDoctor/{id}")
    public String updateDoctor(@PathVariable Long id,
                               @ModelAttribute Doctor doctor,
                               @ModelAttribute User user,
                               @RequestParam(value = "imageFile", required = false) MultipartFile imageFile) throws IOException {

        Doctor existingDoctor = doctorRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid doctor ID: " + id));

        User existingUser = existingDoctor.getUser();
        existingUser.setUsername(user.getUsername());
        existingUser.setEmail(user.getEmail());
        if (user.getPassword() != null && !user.getPassword().isEmpty()) {
            existingUser.setPassword(user.getPassword());
        }
        userRepository.save(existingUser);

        existingDoctor.setFullName(doctor.getFullName());
        existingDoctor.setSpecialization(doctor.getSpecialization());
        existingDoctor.setExperience(doctor.getExperience());
        existingDoctor.setActive(doctor.isActive());

        if (imageFile != null && !imageFile.isEmpty()) {
            String uploadDir = new File("uploads/images/").getAbsolutePath();
            new File(uploadDir).mkdirs();
            String fileName = System.currentTimeMillis() + "_" + imageFile.getOriginalFilename();
            File dest = new File(uploadDir, fileName);
            imageFile.transferTo(dest);
            if (existingDoctor.getImagePath() != null) {
                File oldFile = new File("." + existingDoctor.getImagePath());
                if (oldFile.exists()) oldFile.delete();
            }
            existingDoctor.setImagePath("/uploads/images/" + fileName);
        }

        doctorRepository.save(existingDoctor);
        activityService.publish("Update Doctor", "(ADMIN) Updated doctor: " + existingDoctor.getFullName(), "warning");

        return "redirect:/doctors";
    }

    @GetMapping("/delete/{id}")
    public String deleteDoctor(@PathVariable Long id) {
        doctorRepository.findById(id).ifPresent(doctor -> {
            if (doctor.getImagePath() != null) {
                File imageFile = new File("." + doctor.getImagePath());
                if (imageFile.exists()) imageFile.delete();
            }
            doctorRepository.delete(doctor);
            activityService.publish("Delete Doctor", "(ADMIN) Deleted doctor: " + doctor.getFullName(), "danger");
        });
        return "redirect:/doctors";
    }
    @GetMapping("/dashboard")
    public String doctorDashboard(Model model, HttpSession session,
                                  @RequestParam(value = "date", required = false)
                                  @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        // 1️⃣ Get logged-in user from session
        User currentUser = (User) session.getAttribute("currentUser");
        if (currentUser == null || !"DOCTOR".equals(currentUser.getRole())) {
            return "redirect:/login";
            // not logged in or not a doctor
        }
            // 2️⃣ Fetch doctor linked to user
        Doctor doctor = doctorRepository.findByUserId(currentUser.getId());
        if (doctor == null) { return "redirect:/login";
            // doctor not found
        }
        model.addAttribute("doctor", doctor);
        // 3️⃣ Get appointment slots for selected date
        List<AppointmentSlot> slots = new ArrayList<>();
        if (date != null) {
            slots = slotRepository.findByDoctorAndDate(doctor, date);
            model.addAttribute("selectedDate", date);
        } else {
            model.addAttribute("selectedDate", LocalDate.now());
        }
        model.addAttribute("slots", slots);
        return "doctor-dashboard";
    }
    @PostMapping("/mark-attended/{slotId}")
    @ResponseBody
    public String markAsAttended(@PathVariable Long slotId, HttpSession session) {
        AppointmentSlot slot = slotRepository.findById(slotId).orElse(null);
        User currentUser = (User) session.getAttribute("currentUser");
        Doctor doctor = doctorRepository.findByUserId(currentUser.getId());
        if (slot != null && slot.isBooked()) {
            slot.setAttended(true);
            slotRepository.save(slot);
            // create bill (amount strategy: fixed fee or doctor-specific)
            double amount = 25.0;
            // or derive from doctor/department
            String desc = "Consultation with " + slot.getDoctor().getFullName() + " on " + slot.getDate();
            billingService.createBillForAppointment(slot, amount, desc);
            activityService.publish("Appointment Attended", "Dr. "+ doctor.getFullName()+" has marked slot # " + slotId+" as Attended.", "danger");
            return "success";
        }
        return "error";
    }
    @PostMapping("/cancel-appointment/{slotId}")
    @ResponseBody
    public String cancelAppointment(@PathVariable Long slotId, HttpSession session) {
        AppointmentSlot slot = slotRepository.findById(slotId).orElse(null);
        User currentUser = (User) session.getAttribute("currentUser");
        Doctor doctor = doctorRepository.findByUserId(currentUser.getId());
        if (slot != null && slot.isBooked()) {
            slot.setBooked(false);
            slot.setAvailable(true);
            slot.setPatient(null);
            slotRepository.save(slot);
            activityService.publish("Appointment Cancelled", "Dr. "+ doctor.getFullName()+" has marked slot # " + slotId+" as Cancelled.", "info");
            return "success";
        }
        return "error";
    }
    // Toggle slot availability
    @PostMapping("/toggle-slot/{slotId}")
    @ResponseBody
    public String toggleSlot(@PathVariable Long slotId) {
        AppointmentSlot slot = slotRepository.findById(slotId).orElse(null);
        if (slot != null) {
            slot.setAvailable(!slot.isAvailable());
            slotRepository.save(slot);
            return "success";
        }
        return "error";
    }
}
