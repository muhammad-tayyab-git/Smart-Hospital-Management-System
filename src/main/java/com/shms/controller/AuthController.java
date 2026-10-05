package com.shms.controller;

import com.shms.entity.Patient;
import com.shms.entity.User;
import com.shms.repository.InvoiceRepository;
import com.shms.repository.AppointmentRepository;
import com.shms.repository.AdmissionRepository;
import com.shms.repository.BedRepository;
import com.shms.repository.LabOrderRepository;
import com.shms.repository.PaymentRepository;
import com.shms.repository.DoctorRepository;
import com.shms.repository.PatientRepository;
import com.shms.service.ActivityService;
import com.shms.service.AuthService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@Controller
public class AuthController {
 private final AuthService authService; private final PatientRepository patientRepo; private final DoctorRepository doctorRepo; private final InvoiceRepository billRepo; private final ActivityService activityService; private final AppointmentRepository appointmentRepo; private final AdmissionRepository admissionRepo; private final BedRepository bedRepo; private final LabOrderRepository labRepo; private final PaymentRepository paymentRepo;
 public AuthController(AuthService a,PatientRepository p,DoctorRepository d,InvoiceRepository b,ActivityService x,AppointmentRepository ar,AdmissionRepository adr,BedRepository br,LabOrderRepository lr,PaymentRepository pr){authService=a;patientRepo=p;doctorRepo=d;billRepo=b;activityService=x;appointmentRepo=ar;admissionRepo=adr;bedRepo=br;labRepo=lr;paymentRepo=pr;}
 @GetMapping("/register") public String showRegister(Model model){model.addAttribute("user",new User());model.addAttribute("patient",new Patient());return "register";}
 @PostMapping("/register") public String register(@ModelAttribute("user") User user,@ModelAttribute("patient") Patient patient,Model model){
   if(authService.registerPatient(user,patient)){model.addAttribute("success","Registration successful. You can now sign in.");activityService.publish("New patient registered: "+user.getFullName(),user.getEmail(),"INFO");return "login";}
   model.addAttribute("error","This email is already registered or required information is missing.");return "register";
 }
 @GetMapping("/login") public String login(HttpSession session){Object current=session.getAttribute("currentUser");return current instanceof User?"redirect:"+dashboardFor(((User)current).getRole()):"login";}
 @PostMapping("/login") public String login(@RequestParam String email,@RequestParam String password,HttpSession session,Model model, jakarta.servlet.http.HttpServletRequest request){
   var opt=authService.authenticate(email.trim(),password); if(opt.isEmpty()){model.addAttribute("error","Invalid email or password.");model.addAttribute("email",email);return "login";}
   User user=opt.get(); user.setLastLoginAt(java.time.LocalDateTime.now()); authService.updateLastLogin(user); session.invalidate(); HttpSession freshSession = request.getSession(true); freshSession.setAttribute("currentUser",user); freshSession.setAttribute("currentRole",user.getRole()); freshSession.setMaxInactiveInterval(60*60); activityService.publish("Login: "+user.getFullName(),user.getEmail(),"SUCCESS"); return "redirect:"+dashboardFor(user.getRole());
 }
 @GetMapping("/admin-dashboard") public String adminDashboard(Model model,HttpSession session){User u=current(session);if(u==null||!"ADMIN".equals(u.getRole()))return "redirect:/login";model.addAttribute("user",u);model.addAttribute("doctorsCount",doctorRepo.count());model.addAttribute("patientsCount",patientRepo.count());model.addAttribute("billsCount",billRepo.count());model.addAttribute("revenue",billRepo.totalRevenue());model.addAttribute("patientsMonthly",monthlyPatients());model.addAttribute("paidBills",List.of(billRepo.countPaid(),billRepo.countUnpaid()));model.addAttribute("activities",activityService.getRecentActivities()); model.addAttribute("appointmentsCount",appointmentRepo.count()); model.addAttribute("admissionsCount",admissionRepo.count()); model.addAttribute("bedsCount",bedRepo.count()); model.addAttribute("labsCount",labRepo.count()); model.addAttribute("paymentsCount",paymentRepo.count()); return "admin-dashboard";}
 @GetMapping("/logout") public String logout(HttpSession session){session.invalidate();return "redirect:/home";}
 private User current(HttpSession s){Object u=s.getAttribute("currentUser");return u instanceof User?(User)u:null;}
 private long[] monthlyPatients(){long[] m=new long[12];for(Object[] r:patientRepo.countPatientsPerMonth()){int month=((Number)r[0]).intValue();m[month-1]=((Number)r[1]).longValue();}return m;}
 private String dashboardFor(String role){return switch(role){case "ADMIN"->"/admin-dashboard";case "DOCTOR"->"/doctors/dashboard";case "RECEPTIONIST"->"/reception-dashboard";case "NURSE","PHARMACIST","LAB_TECHNICIAN"->"/staff-dashboard";default->"/patient-dashboard";};}
}
