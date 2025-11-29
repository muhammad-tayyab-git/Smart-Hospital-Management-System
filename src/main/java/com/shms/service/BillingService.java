package com.shms.service;

import com.shms.entity.AppointmentSlot;
import com.shms.entity.Bill;
import com.shms.entity.Patient;
import com.shms.repository.BillRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
public class BillingService {

    private final BillRepository billRepo;
//    private final PaymentService paymentService; // abstraction for payment providers

    public BillingService(BillRepository billRepo) {
        this.billRepo = billRepo;
    }
//    public BillingService(BillRepository billRepo, PaymentService paymentService) {
//        this.billRepo = billRepo;
//        this.paymentService = paymentService;
//    }

    public Bill createBillForAppointment(AppointmentSlot slot, double amount, String description) {
        Bill b = new Bill();
        b.setAppointment(slot);
        b.setPatient(slot.getPatient());
        b.setAmount(amount);
        b.setDescription(description);
        b.setCurrency("EUR");
        b.setStatus("UNPAID");
        return billRepo.save(b);
    }

//    public List<Bill> getUnpaidBillsForPatient(Patient p) {
//        return billRepo.findByPatientAndStatus(p, "UNPAID");
//    }

    public Bill getBill(Long id) { return billRepo.findById(id).orElse(null); }

//    @Transactional
//    public Bill markBillPaid(Long billId) {
//        Bill b = billRepo.findById(billId).orElseThrow();
//        b.setStatus("PAID");
//        b.setPaidAt(java.time.LocalDateTime.now());
//        return billRepo.save(b);
//    }

    // start payment: delegate to payment service (returns provider checkout/session id / url)


//    public String startPayment(Bill bill) {
//        return paymentService.createPaymentSession(bill);
//    }
}
