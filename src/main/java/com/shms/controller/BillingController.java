package com.shms.controller;
import com.shms.entity.Invoice;import com.shms.entity.Patient;import com.shms.entity.User;import com.shms.repository.BillRepository;import com.shms.repository.PatientRepository;import com.shms.service.ActivityService;import jakarta.servlet.http.HttpSession;import org.springframework.stereotype.Controller;import org.springframework.ui.Model;import org.springframework.web.bind.annotation.*;import java.util.*;
@Controller @RequestMapping("/billing") public class BillingController{
 private final BillRepository bills;private final PatientRepository patients;private final ActivityService activities;
 public BillingController(BillRepository b,PatientRepository p,ActivityService a){bills=b;patients=p;activities=a;}
 @GetMapping("/my") public String my(HttpSession s,Model m){User u=current(s);if(u==null)return "redirect:/login";Patient p=patients.findByUserId(u.getId()).orElseThrow();m.addAttribute("bills",bills.findByPatient(p));return "my_bills";}
 @GetMapping("/listBills") public String list(@RequestParam(required=false)String status,@RequestParam(required=false)String keyword,Model m){List<Invoice> list;if(keyword!=null&&!keyword.isBlank())list=bills.searchBills(keyword);else if(status!=null&&!status.isBlank())list=bills.findByStatus(Invoice.Status.valueOf(status));else list=bills.findAll();m.addAttribute("bills",list);m.addAttribute("status",status);m.addAttribute("keyword",keyword);return "listBills";}
 @GetMapping("/view/{id}") public String view(@PathVariable Long id,Model m){m.addAttribute("bill",bills.findById(id).orElseThrow());return "bill_detail";}
 @PostMapping("/updateStatus/{id}") public String update(@PathVariable Long id,@RequestParam String status){Invoice i=bills.findById(id).orElseThrow();i.setStatus(Invoice.Status.valueOf(status));bills.save(i);activities.publish("Invoice "+i.getInvoiceNumber()+" status changed to "+status,"SYSTEM","BILLING");return "redirect:/billing/listBills";}
 private User current(HttpSession s){Object o=s.getAttribute("currentUser");return o instanceof User?(User)o:null;}
}
