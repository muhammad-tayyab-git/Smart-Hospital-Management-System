package com.shms.service;

import com.shms.entity.Appointment;
import com.shms.entity.Invoice;
import com.shms.repository.InvoiceRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;

@Service
public class BillingService {
    private final InvoiceRepository repo;

    public BillingService(InvoiceRepository repo) {
        this.repo = repo;
    }

    /** Creates one consultation invoice per completed appointment. Idempotent by appointment. */
    @Transactional
    public Invoice createBillForAppointment(Appointment appointment, double amount, String description) {
        Invoice existing = repo.findByAppointment(appointment);
        if (existing != null) {
            return existing;
        }

        BigDecimal fee = BigDecimal.valueOf(Math.max(0d, amount));
        Invoice invoice = new Invoice();
        invoice.setInvoiceNumber("INV-" + System.currentTimeMillis());
        invoice.setPatient(appointment.getPatient());
        invoice.setAppointment(appointment);
        invoice.setIssueDate(LocalDate.now());
        invoice.setDueDate(LocalDate.now().plusDays(30));
        invoice.setSubtotal(fee);
        invoice.setDiscount(BigDecimal.ZERO);
        invoice.setTax(BigDecimal.ZERO);
        invoice.setTotalAmount(fee);
        invoice.setStatus(Invoice.Status.PENDING);
        return repo.save(invoice);
    }

    public Invoice getBill(Long id) {
        return repo.findById(id).orElse(null);
    }

    public Invoice getBillForAppointment(Appointment appointment) {
        return repo.findByAppointment(appointment);
    }
}
