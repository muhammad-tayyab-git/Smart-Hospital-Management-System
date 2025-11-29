package com.shms.controller;

import com.shms.entity.AppointmentSlot;
import com.shms.entity.Doctor;
import com.shms.entity.Patient;
import com.shms.entity.User;
import com.shms.repository.AppointmentSlotRepository;
import com.shms.repository.DoctorRepository;
import com.shms.repository.PatientRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@Controller
@RequestMapping("/appointments")
public class AppointmentController {

    @Autowired
    private AppointmentSlotRepository slotRepo;

    @Autowired
    private DoctorRepository doctorRepo;
    @Autowired
    private PatientRepository patientRepo;
    // 1️⃣  Show booking page with date form
    @GetMapping("/book")
    public String showBookingPage(
            @RequestParam Long doctorId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            Model model) {

        Doctor doctor = doctorRepo.findById(doctorId).orElseThrow();
        model.addAttribute("doctor", doctor);
        model.addAttribute("selectedDate", date);

        if (date != null && !date.isBefore(LocalDate.now())) {
            List<AppointmentSlot> slots = slotRepo.findByDoctorIdAndDateAndAvailableTrueAndBookedFalse(doctorId, date);
            model.addAttribute("slots", slots);
        } else {
            model.addAttribute("error", "You can only book future appointments.");
        }


        return "listAppointments";
    }

    // 2️⃣  Book a slot (form submission)
    @PostMapping("/bookSlot/{slotId}")
    public String bookSlot(@PathVariable Long slotId, HttpSession session, Model model,@RequestHeader(value = "Referer", required = false) String referer) {
        User currentUser = (User) session.getAttribute("currentUser");
        if (currentUser == null) {
            return "redirect:/login";
        }

        Patient currentPatient = patientRepo.findByUserId(currentUser.getId())
                .orElseThrow(() -> new RuntimeException("Patient profile not found"));

        AppointmentSlot slot = slotRepo.findById(slotId).orElse(null);
        if (slot == null || slot.isBooked() || !slot.isAvailable()) {
            model.addAttribute("error", "This slot is no longer available.");
            return "redirect:/appointments/my";
        }

        slot.setBooked(true);
        slot.setAvailable(false);
        slot.setPatient(currentPatient);
        slotRepo.save(slot);

        return "redirect:" + (referer != null ? referer : "/appointments/my");
    }


    // 3️⃣  View my booked appointments
    @GetMapping("/my")
    public String myAppointments(HttpSession session, Model model) {
        User currentUser = (User) session.getAttribute("currentUser");
        if (currentUser == null) {
            return "redirect:/login";
        }

        // Fetch the Patient entity linked to this User
        Patient currentPatient = patientRepo.findByUserId(currentUser.getId())
                .orElseThrow(() -> new RuntimeException("Patient profile not found"));

        // Fetch appointments
        List<AppointmentSlot> appointments = slotRepo.findByPatientId(currentPatient.getId());
        model.addAttribute("appointments", appointments);
        return "my_appointments";
    }


    @PostMapping("/cancel/{slotId}")
    public String cancelAppointment(@PathVariable Long slotId, HttpSession session) {
        User currentUser = (User) session.getAttribute("currentUser");
        if (currentUser == null) {
            return "redirect:/login";
        }

        // Fetch the Patient entity linked to this User
        Patient current = patientRepo.findByUserId(currentUser.getId())
                .orElseThrow(() -> new RuntimeException("Patient profile not found"));


        AppointmentSlot slot = slotRepo.findById(slotId).orElse(null);
        if (slot != null && slot.getPatient() != null && slot.getPatient().getId().equals(current.getId())) {
            slot.setBooked(false);
            slot.setAvailable(true);
            slot.setPatient(null);
            slotRepo.save(slot);
        }

        return "redirect:/appointments/my";
    }
}
