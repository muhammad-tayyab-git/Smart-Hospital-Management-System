package com.shms.controller;

import com.shms.entity.*;
import com.shms.repository.*;
import com.shms.service.*;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDate;
import java.util.List;

@Controller
public class ClinicalController {
    private final PatientRepository patients; private final DoctorRepository doctors;
    private final AppointmentRepository appointments; private final MedicalRecordRepository records;
    private final PrescriptionRepository prescriptions; private final LabOrderRepository labs;
    private final LabResultRepository results; private final AdmissionRepository admissions;
    private final WardRepository wards; private final BedRepository beds;
    private final NotificationRepository notifications; private final MedicalRecordService recordService;
    private final PrescriptionService prescriptionService; private final LaboratoryService labService;
    private final AdmissionService admissionService;

    public ClinicalController(PatientRepository p, DoctorRepository d, AppointmentRepository a,
      MedicalRecordRepository r, PrescriptionRepository pr, LabOrderRepository l, LabResultRepository lr,
      AdmissionRepository ad, WardRepository w, BedRepository b, NotificationRepository n,
      MedicalRecordService rs, PrescriptionService ps, LaboratoryService ls, AdmissionService ads){
        patients=p; doctors=d; appointments=a; records=r; prescriptions=pr; labs=l; results=lr;
        admissions=ad; wards=w; beds=b; notifications=n; recordService=rs; prescriptionService=ps;
        labService=ls; admissionService=ads;
    }

    @GetMapping("/patient/health")
    public String patientHealth(HttpSession s, Model m){
        User u=current(s); if(u==null) return "redirect:/login";
        Patient p=patients.findByUserId(u.getId()).orElseThrow();
        m.addAttribute("patient",p); m.addAttribute("records",records.findByPatientId(p.getId()));
        m.addAttribute("prescriptions",prescriptions.findByPatientId(p.getId()));
        m.addAttribute("labOrders",labs.findByPatientId(p.getId()));
        m.addAttribute("admissions",admissions.findByPatientId(p.getId()));
        m.addAttribute("notifications",notifications.findByUserId(u.getId()));
        return "patient-health";
    }

    @GetMapping("/doctors/appointment/{id}/clinical")
    public String clinical(@PathVariable Long id,HttpSession s,Model m){
        User u=current(s); if(u==null || !"DOCTOR".equals(u.getRole())) return "redirect:/login";
        Doctor d=doctors.findByUserId(u.getId()); Appointment a=appointments.findById(id).orElseThrow();
        if(a.getDoctor()==null || !a.getDoctor().getId().equals(d.getId())) return "redirect:/doctors/dashboard";
        MedicalRecord record=records.findByAppointmentId(id);
        m.addAttribute("appointment",a);m.addAttribute("record",record==null?new MedicalRecord():record);
        m.addAttribute("prescription",new Prescription());m.addAttribute("labOrder",new LabOrder());
        return "clinical-visit";
    }

    @PostMapping("/doctors/appointment/{id}/clinical")
    public String saveClinical(@PathVariable Long id,@RequestParam(required=false) String complaint,
      @RequestParam(required=false) String symptoms,@RequestParam(required=false) String notes,
      @RequestParam(required=false) String diagnosis,@RequestParam(required=false) String treatment,
      @RequestParam(required=false) String followUp,HttpSession s){
        User u=current(s); if(u==null || !"DOCTOR".equals(u.getRole())) return "redirect:/login";
        Doctor d=doctors.findByUserId(u.getId()); Appointment a=appointments.findById(id).orElseThrow();
        if(a.getDoctor()==null || !a.getDoctor().getId().equals(d.getId())) return "redirect:/doctors/dashboard";
        if(records.findByAppointmentId(id)==null){
            Patient p=a.getPatient(); LocalDate fd=null; if(followUp!=null&&!followUp.isBlank()) fd=LocalDate.parse(followUp);
            recordService.create(p,d,a,complaint,symptoms,notes,diagnosis,treatment,fd);
        }
        return "redirect:/doctors/appointment/"+id+"/clinical";
    }

    @PostMapping("/doctors/appointment/{id}/lab")
    public String createLab(@PathVariable Long id,@RequestParam String testName,@RequestParam(required=false) String priority,HttpSession s){
        User u=current(s); if(u==null || !"DOCTOR".equals(u.getRole())) return "redirect:/login";
        Doctor d=doctors.findByUserId(u.getId()); Appointment a=appointments.findById(id).orElseThrow();
        if(a.getDoctor()==null || !a.getDoctor().getId().equals(d.getId())) return "redirect:/doctors/dashboard";
        LabOrder.Priority p=priority==null?LabOrder.Priority.NORMAL:LabOrder.Priority.valueOf(priority);
        labService.createOrder(a.getPatient(),d,a,testName,p); return "redirect:/doctors/appointment/"+id+"/clinical";
    }

    @GetMapping("/lab/dashboard")
    public String labDashboard(HttpSession s,Model m){
        User u=current(s); if(u==null || !"LAB_TECHNICIAN".equals(u.getRole())) return "redirect:/staff-dashboard";
        m.addAttribute("orders",labs.findAll()); m.addAttribute("results",results.findAll()); return "lab-dashboard";
    }

    @PostMapping("/lab/{id}/status")
    public String labStatus(@PathVariable Long id,@RequestParam String status,HttpSession s){
        User u=current(s); if(u==null || !"LAB_TECHNICIAN".equals(u.getRole())) return "redirect:/login";
        LabOrder o=labs.findById(id).orElseThrow();o.setStatus(LabOrder.Status.valueOf(status));labs.save(o);return "redirect:/lab/dashboard";
    }

    @PostMapping("/lab/{id}/result")
    public String labResult(@PathVariable Long id,@RequestParam String value,@RequestParam(required=false) String unit,
      @RequestParam(required=false) String reference,@RequestParam(required=false) String text,HttpSession s){
        User u=current(s); if(u==null || !"LAB_TECHNICIAN".equals(u.getRole())) return "redirect:/login";
        LabOrder o=labs.findById(id).orElseThrow();labService.recordResult(o,value,unit,reference,text,u);return "redirect:/lab/dashboard";
    }

    @GetMapping("/admissions/manage")
    public String admissions(HttpSession s,Model m){
        User u=current(s);if(u==null||!("ADMIN".equals(u.getRole())||"RECEPTIONIST".equals(u.getRole())||"NURSE".equals(u.getRole())||"DOCTOR".equals(u.getRole())))return "redirect:/login";
        m.addAttribute("admissions",admissions.findAll());m.addAttribute("wards",wards.findAll());m.addAttribute("beds",beds.findAll());return "admissions";
    }

    @GetMapping("/notifications")
    public String notificationCenter(HttpSession s,Model m){
        User u=current(s);if(u==null)return "redirect:/login";m.addAttribute("notifications",notifications.findByUserId(u.getId()));return "notifications";
    }

    @PostMapping("/notifications/{id}/read")
    public String read(@PathVariable Long id,HttpSession s){
        User u=current(s);if(u==null)return "redirect:/login";notifications.findById(id).filter(n->n.getUser()!=null&&n.getUser().getId().equals(u.getId())).ifPresent(n->{n.setRead(true);notifications.save(n);});return "redirect:/notifications";
    }

    private User current(HttpSession s){Object o=s.getAttribute("currentUser");return o instanceof User?(User)o:null;}
}
