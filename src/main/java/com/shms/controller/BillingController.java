package com.shms.controller;

import com.shms.entity.Bill;
import com.shms.entity.Patient;
import com.shms.entity.User;
import com.shms.repository.BillRepository;
import com.shms.repository.PatientRepository;
import com.shms.service.BillingService;
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

    public BillingController(BillingService billingService, PatientRepository patientRepository, BillRepository billRepo) {
        this.billingService = billingService;
        this.patientRepository = patientRepository;
        this.billRepo = billRepo;
    }
    @GetMapping("/my")
    public String myBills(HttpSession session, Model model) {
        User user = (User) session.getAttribute("currentUser");
        if (user == null) return "redirect:/login";
        Patient patient = patientRepository.findByUserId(user.getId()).orElseThrow();
        model.addAttribute("bills", billRepo.findByPatient(patient));
        return "my_bills";
    }

    // list unpaid bills for current patient
    @GetMapping("/listBills")
    public String listAllBills(@RequestParam(value = "status", required = false) String status,
                               @RequestParam(value = "keyword", required = false) String keyword,
                               Model model) {

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
        return "listBills";
    }
    @GetMapping("/view/{id}")
    public String viewBill(@PathVariable Long id, Model model) {
        Bill bill = billRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Bill not found"));

        model.addAttribute("bill", bill);
        return "bill_detail";
    }
    @PostMapping("/updateStatus/{id}")
    public String updateBillStatus(@PathVariable Long id,
                                   @RequestParam("status") String status) {

        Bill bill = billRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Bill not found"));

        bill.setStatus(status);
        billRepo.save(bill);

        return "redirect:/billing/listBills";
    }

    // bill detail and pay button
//    @GetMapping("/view/{id}")
//    public String viewBill(@PathVariable Long id, Model model, HttpSession session) {
//        Bill bill = billingService.getBill(id);
//        model.addAttribute("bill", bill);
//           return "bill_detail";
//    }

    // start payment: redirect to provider url
//    @PostMapping("/pay/{id}")
//    public String payBill(@PathVariable Long id) {
//        Bill bill = billingService.getBill(id);
//        String redirectUrl = billingService.startPayment(bill);
//        return "redirect:" + redirectUrl;
//    }

    // callback endpoints (Stripe success/cancel) -> mark paid if success
//    @GetMapping("/payment/success")
//    public String paymentSuccess(@RequestParam Long billId) {
//        billingService.markBillPaid(billId);
//        return "billing/payment_success";
//    }
}
