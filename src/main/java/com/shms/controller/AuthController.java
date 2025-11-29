package com.shms.controller;

import com.shms.entity.Patient;
import com.shms.entity.User;
import com.shms.repository.BillRepository;
import com.shms.repository.DoctorRepository;
import com.shms.repository.PatientRepository;
import com.shms.service.AuthService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Controller
public class AuthController {

    private final AuthService authService;
    private final PatientRepository patientRepo;
    @Autowired
    private final DoctorRepository doctorRepo;
    private final BillRepository billRepo;
    public AuthController(AuthService authService, PatientRepository patientRepo, DoctorRepository doctorRepo, BillRepository billRepo) { this.authService = authService;
        this.patientRepo = patientRepo;
        this.doctorRepo = doctorRepo;
        this.billRepo = billRepo;
    }
    @GetMapping("/register")
    public String showRegisterForm(Model model) {
        model.addAttribute("user", new User());
        model.addAttribute("patient", new Patient());
        return "register";
    }

    @PostMapping("/register")
    public String register(@ModelAttribute("user") User user,
                           @ModelAttribute("patient") Patient patient,
                           Model model) {

        boolean success = authService.registerPatient(user, patient);

        if (success) {
            model.addAttribute("success", "Registration successful! Please login.");
            return "login";
        } else {
            model.addAttribute("error", "Username or Email already exists!");
            return "register";
        }
    }
    @GetMapping("/login")
    public String login() {
        return "login";
    }
    @PostMapping("/login")
    public String login(@RequestParam String username,
                        @RequestParam String password,
                        HttpSession session,
                        Model model) {

        var opt = authService.authenticate(username, password);
        if (opt.isPresent()) {
            User u = opt.get();
            session.setAttribute("currentUser", u);
            model.addAttribute("user", u);
            if(Objects.equals(u.getRole(), "PATIENT")){
                return "home";
            } if(Objects.equals(u.getRole(), "DOCTOR")){

                return "redirect:/doctors/dashboard";

            }
            return "redirect:/admin-dashboard";
        } else {
            model.addAttribute("error", "Invalid credentials");
            return "login";
        }
    }


    @GetMapping("/admin-dashboard")
    public String dashboard(Model m) {
        m.addAttribute("doctorsCount", doctorRepo.count());
        m.addAttribute("patientsCount", patientRepo.count());
        m.addAttribute("billsCount", billRepo.count());
        m.addAttribute("revenue", billRepo.totalRevenue());

        // Example data — replace with real query
        m.addAttribute("patientsMonthly", List.of(10,12,15,9,20,18,22,30,25,28,35,40));

//        List<Object[]> monthlyData = patientRepo.countPatientsPerMonth();
//        List<Long> monthlyCounts = new ArrayList<>();
//
//        // Initialize 12 months with default 0
//        for (int i = 0; i < 12; i++) monthlyCounts.add(0L);
//
//        // Fill available data
//        for (Object[] row : monthlyData) {
//            int month = ((Integer) row[0]) - 1; // Month index 0–11
//            Long count = (Long) row[1];
//            monthlyCounts.set(month, count);
//        }
//
//        m.addAttribute("patientsMonthly", monthlyCounts);



        m.addAttribute("paidBills", List.of(
                billRepo.countPaid(),
                billRepo.countUnpaid()
        ));

        return "admin-dashboard";
    }



//    @GetMapping("/dashboard")
//    public String dashboard(HttpSession session, Model model) {
//        User u = (User) session.getAttribute("currentUser");
//        model.addAttribute("user", u);
//        return "dashboard";
//    }

    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/home";
    }
}
