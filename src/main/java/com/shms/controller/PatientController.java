package com.shms.controller;

import com.shms.entity.Patient;
import com.shms.entity.Role;
import com.shms.entity.User;
import com.shms.repository.PatientRepository;
import com.shms.repository.RoleRepository;
import com.shms.repository.UserRepository;
import com.shms.service.ActivityService;
import com.shms.service.PatientService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller @RequestMapping("/patients")
public class PatientController {
 private final PatientService service;private final PatientRepository patients;private final UserRepository users;private final RoleRepository roles;private final ActivityService activities;private final PasswordEncoder encoder;
 public PatientController(PatientService s,PatientRepository p,UserRepository u,RoleRepository r,ActivityService a,PasswordEncoder e){service=s;patients=p;users=u;roles=r;activities=a;encoder=e;}
 @GetMapping public String list(Model model,HttpServletRequest request){model.addAttribute("patients",service.getAllPatients());model.addAttribute("currentUri",request.getRequestURI());return "patients";}
 @GetMapping("/add") public String add(Model model){model.addAttribute("patient",new Patient());model.addAttribute("user",new User());return "patient_form";}
 @PostMapping("/add") public String save(@ModelAttribute Patient patient,@ModelAttribute User user,RedirectAttributes ra){
   try{user.setPassword(encoder.encode(user.getPassword()));user.setStatus(User.Status.ACTIVE);Role role=roles.findByName("PATIENT").orElseThrow();user.getRoles().clear();user.getRoles().add(role);User saved=users.save(user);patient.setUser(saved);patient.setPatientNumber("PAT-"+String.format("%06d",saved.getId()));patients.save(patient);activities.publish("Patient created: "+user.getFullName(),user.getEmail(),"SUCCESS");ra.addFlashAttribute("success","Patient created successfully.");}catch(Exception e){ra.addFlashAttribute("error","Could not create patient: "+e.getMessage());}return "redirect:/patients";
 }
 @GetMapping("/edit/{id}") public String edit(@PathVariable Long id,Model model){Patient p=service.getPatientById(id);model.addAttribute("patient",p);model.addAttribute("user",p.getUser());return "patient_form";}
 @PostMapping("/update/{id}") public String update(@PathVariable Long id,@ModelAttribute Patient form,@ModelAttribute User formUser,RedirectAttributes ra){Patient p=service.getPatientById(id);User u=p.getUser();if(formUser.getFirstName()!=null&&!formUser.getFirstName().isBlank())u.setFirstName(formUser.getFirstName());if(formUser.getLastName()!=null&&!formUser.getLastName().isBlank())u.setLastName(formUser.getLastName());if(formUser.getEmail()!=null&&!formUser.getEmail().isBlank())u.setEmail(formUser.getEmail());if(formUser.getPassword()!=null&&!formUser.getPassword().isBlank())u.setPassword(encoder.encode(formUser.getPassword()));if(formUser.getPhone()!=null)u.setPhone(formUser.getPhone());users.save(u);p.setGender(form.getGender());p.setDateOfBirth(form.getDateOfBirth());p.setAddress(form.getAddress());p.setAllergies(form.getAllergies());p.setEmergencyContactName(form.getEmergencyContactName());p.setEmergencyContactPhone(form.getEmergencyContactPhone());patients.save(p);ra.addFlashAttribute("success","Patient updated successfully.");return "redirect:/patients";}
 @PostMapping("/delete/{id}") public String delete(@PathVariable Long id,RedirectAttributes ra){patients.findById(id).ifPresent(p->{try{patients.delete(p);users.delete(p.getUser());ra.addFlashAttribute("success","Patient deleted successfully.");}catch(DataIntegrityViolationException e){ra.addFlashAttribute("error","Cannot delete this patient because related hospital records exist.");}});return "redirect:/patients";}
}
