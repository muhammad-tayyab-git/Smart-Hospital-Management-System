package com.shms.controller;

import com.shms.entity.Bill;
import com.shms.entity.Patient;
import com.shms.entity.User;
import com.shms.repository.BillRepository;
import com.shms.repository.PatientRepository;
import com.shms.service.BillingService;
import com.shms.service.ActivityService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/billing")
public class BillingController {

    private final BillingService billingService;
    private final PatientRepository patientRepository;
    private final BillRepository billRepo;
    private final ActivityService activityService;

    public BillingController(BillingService billingService, PatientRepository patientRepository,
                             BillRepository billRepo, ActivityService activityService) {
        this.billingService = billingService;
        this.patientRepository = patientRepository;
        this.billRepo = billRepo;
        this.activityService = activityService;
    }

    @GetMapping("/my")
    public String myBills(HttpSession session, Model model) {
        User user = (User) session.getAttribute("currentUser");
        if (user == null) return "redirect:/login";

        Patient patient = patientRepository.findByUserId(user.getId()).orElseThrow();
        List<Bill> bills = billRepo.findByPatient(patient);
        model.addAttribute("bills", bills);

//        activityService.publish("View Bills", user.getUsername() + " viewed their bills", "info");

        return "my_bills";
    }

    @GetMapping("/listBills")
    public String listAllBills(@RequestParam(value = "status", required = false) String status,
                               @RequestParam(value = "keyword", required = false) String keyword,
                               Model model, HttpServletRequest request) {

        List<Bill> bills;

        if (keyword != null && !keyword.isEmpty()) {
            bills = billRepo.searchBills(keyword);
        } else if (status != null && !status.isEmpty()) {
            bills = billRepo.findByStatus(status);
        } else {
            bills = billRepo.findAll();
        }

        model.addAttribute("bills", bills);
        model.addAttribute("status", status);
        model.addAttribute("keyword", keyword);
        model.addAttribute("currentUri", request.getRequestURI());

//        activityService.publish("List Bills", "Viewed billing list page", "info");

        return "listBills";
    }

    @GetMapping("/view/{id}")
    public String viewBill(@PathVariable Long id, Model model, HttpServletRequest request) {
        Bill bill = billRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Bill not found"));

        model.addAttribute("bill", bill);
        model.addAttribute("currentUri", request.getRequestURI());

//        activityService.publish("View Bill", "Viewed bill #" + bill.getId() + " for patient " + bill.getPatient().getFullName(), "info");

        return "bill_detail";
    }

    @PostMapping("/updateStatus/{id}")
    public String updateBillStatus(@PathVariable Long id,
                                   @RequestParam("status") String status) {

        Bill bill = billRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Bill not found"));

        bill.setStatus(status);
        billRepo.save(bill);

        activityService.publish("Update Bill", "(ADMIN) Updated bill #" + bill.getId() + " status to " + status, "warning");

        return "redirect:/billing/listBills";
    }
}
