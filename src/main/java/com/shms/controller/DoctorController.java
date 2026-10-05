package com.shms.controller;

import com.shms.entity.*;
import com.shms.repository.*;
import com.shms.service.ActivityService;
import com.shms.service.BillingService;
import jakarta.servlet.http.HttpSession;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.HashMap;
import java.util.Map;

@Controller @RequestMapping("/doctors")
public class DoctorController {
 private final DoctorRepository doctors; private final UserRepository users; private final DepartmentRepository departments; private final com.shms.repository.RoleRepository roles; private final AppointmentRepository appointments; private final PasswordEncoder encoder; private final ActivityService activities; private final BillingService billing; private final com.shms.service.NotificationService notifications;
 public DoctorController(DoctorRepository d,UserRepository u,DepartmentRepository dep,com.shms.repository.RoleRepository r,AppointmentRepository a,PasswordEncoder e,ActivityService x,BillingService b,com.shms.service.NotificationService n){doctors=d;users=u;departments=dep;roles=r;appointments=a;encoder=e;activities=x;billing=b;notifications=n;}
 @GetMapping public String list(Model model){model.addAttribute("doctors",doctors.findAll());return "doctors";}
 @GetMapping("/addDoctor") public String add(Model model){model.addAttribute("doctor",new Doctor());model.addAttribute("user",new User());model.addAttribute("departments",departments.findAll());return "doctor_form";}
 @PostMapping("/addDoctor") public String save(@ModelAttribute Doctor doctor,@ModelAttribute User user,@RequestParam(required=false) Long departmentId,@RequestParam(value="imageFile",required=false) MultipartFile image) throws Exception{
   user.setPassword(encoder.encode(user.getPassword()));user.setFirstName(user.getFirstName()==null?"Doctor":user.getFirstName());user.setLastName(user.getLastName()==null?"":user.getLastName());
   Role role=roles.findByName("DOCTOR").orElseThrow();user.getRoles().clear();user.getRoles().add(role);User saved=users.save(user);doctor.setUser(saved);doctor.setDoctorNumber("DOC-"+String.format("%06d",saved.getId()));doctor.setDepartment(departments.findById(departmentId==null?1L:departmentId).orElseThrow());doctor.setStatus(Doctor.Status.ACTIVE);doctors.save(doctor);activities.publish("Doctor created: "+doctor.getFullName(),saved.getEmail(),"SUCCESS");return "redirect:/doctors";
 }
 @GetMapping("/edit/{id}") public String edit(@PathVariable Long id,Model model){Doctor d=doctors.findById(id).orElseThrow();model.addAttribute("doctor",d);model.addAttribute("user",d.getUser());model.addAttribute("departments",departments.findAll());return "doctor_form";}
 @PostMapping("/updateDoctor/{id}") public String update(@PathVariable Long id,@ModelAttribute Doctor form,@ModelAttribute User formUser,@RequestParam(required=false) Long departmentId){Doctor d=doctors.findById(id).orElseThrow();User u=d.getUser();if(formUser.getFirstName()!=null&&!formUser.getFirstName().isBlank())u.setFirstName(formUser.getFirstName());if(formUser.getLastName()!=null&&!formUser.getLastName().isBlank())u.setLastName(formUser.getLastName());if(formUser.getEmail()!=null&&!formUser.getEmail().isBlank())u.setEmail(formUser.getEmail());if(formUser.getPhone()!=null)u.setPhone(formUser.getPhone());if(formUser.getPassword()!=null&&!formUser.getPassword().isBlank())u.setPassword(encoder.encode(formUser.getPassword()));users.save(u);d.setSpecialization(form.getSpecialization());d.setExperience(form.getExperience());d.setDepartment(departments.findById(departmentId==null?d.getDepartment().getId():departmentId).orElse(d.getDepartment()));d.setStatus(form.isActive()?Doctor.Status.ACTIVE:Doctor.Status.INACTIVE);doctors.save(d);return "redirect:/doctors";}
 @PostMapping("/delete/{id}") public String delete(@PathVariable Long id){doctors.findById(id).ifPresent(d->doctors.delete(d));return "redirect:/doctors";}
 @GetMapping("/dashboard") public String dashboard(HttpSession session,Model model,@RequestParam(required=false) @DateTimeFormat(iso=DateTimeFormat.ISO.DATE) LocalDate date){
   User u=current(session);if(u==null||!"DOCTOR".equals(u.getRole()))return "redirect:/login";
   Doctor d=doctors.findByUserId(u.getId());if(d==null)return "redirect:/login";
   LocalDate day=date==null?LocalDate.now():date;
   List<Appointment> dayAppointments=appointments.findByDoctorAndDate(d,day);
   Map<Long,Invoice> invoices=new HashMap<>();
   dayAppointments.forEach(a->{Invoice invoice=billing.getBillForAppointment(a);if(invoice!=null)invoices.put(a.getId(),invoice);});
   long confirmed=dayAppointments.stream().filter(a->a.getStatus()==Appointment.AppointmentStatus.CONFIRMED).count();
   long inProgress=dayAppointments.stream().filter(a->a.getStatus()==Appointment.AppointmentStatus.IN_PROGRESS).count();
   long completed=dayAppointments.stream().filter(a->a.getStatus()==Appointment.AppointmentStatus.COMPLETED).count();
   model.addAttribute("doctor",d);model.addAttribute("selectedDate",day);model.addAttribute("slots",dayAppointments);
   model.addAttribute("invoices",invoices);model.addAttribute("confirmedCount",confirmed);model.addAttribute("inProgressCount",inProgress);model.addAttribute("completedCount",completed);
   return "doctor-dashboard";
 }
 @PostMapping("/start-appointment/{id}") @ResponseBody public String start(@PathVariable Long id,HttpSession s){
   Doctor d=doctor(s);if(d==null)return "error";
   return appointments.findById(id).filter(a->a.getDoctor()!=null&&a.getDoctor().getId().equals(d.getId())&&a.getPatient()!=null&&a.getStatus()==Appointment.AppointmentStatus.CONFIRMED).map(a->{a.setStatus(Appointment.AppointmentStatus.IN_PROGRESS);appointments.save(a);return "success";}).orElse("error");
 }
 @PostMapping("/mark-attended/{id}") @ResponseBody public String attended(@PathVariable Long id,HttpSession s){
   Doctor d=doctor(s);if(d==null)return "error";
   return appointments.findById(id).filter(a->a.getDoctor()!=null&&a.getDoctor().getId().equals(d.getId())&&a.getPatient()!=null&&a.getStatus()!=Appointment.AppointmentStatus.CANCELLED&&a.getStatus()!=Appointment.AppointmentStatus.COMPLETED).map(a->{
      a.setStatus(Appointment.AppointmentStatus.COMPLETED);appointments.save(a);
      Invoice invoice=billing.createBillForAppointment(a,d.getConsultationFee()==null?0:d.getConsultationFee().doubleValue(),"Consultation");
      activities.publish("Appointment "+a.getAppointmentNumber()+" completed; invoice "+invoice.getInvoiceNumber()+" generated",d.getUser().getEmail(),"BILLING"); notifications.send(a.getPatient().getUser(),"Visit completed","Your visit "+a.getAppointmentNumber()+" has been completed. Invoice "+invoice.getInvoiceNumber()+" is available.",Notification.Type.BILLING);
      return "success";
   }).orElse("error");
 }
 @PostMapping("/cancel-appointment/{id}") @ResponseBody public String cancel(@PathVariable Long id,HttpSession s){Doctor d=doctor(s);if(d==null)return "error";return appointments.findById(id).filter(a->a.getDoctor().getId().equals(d.getId())).map(a->{a.setStatus(Appointment.AppointmentStatus.CANCELLED);appointments.save(a);return "success";}).orElse("error");}

 private User current(HttpSession s){Object o=s.getAttribute("currentUser");return o instanceof User?(User)o:null;}
 private Doctor doctor(HttpSession s){User u=current(s);return u==null?null:doctors.findByUserId(u.getId());}
}
