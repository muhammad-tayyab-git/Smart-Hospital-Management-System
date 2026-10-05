package com.shms.service;
import com.shms.entity.AppointmentSlot;
import com.shms.entity.Invoice;
import com.shms.repository.BillRepository;
import org.springframework.stereotype.Service;
import java.math.BigDecimal;
import java.time.LocalDate;
@Service
public class BillingService {
 private final BillRepository repo;
 public BillingService(BillRepository repo){this.repo=repo;}
 public Invoice createBillForAppointment(AppointmentSlot slot,double amount,String description){
   Invoice i=new Invoice(); i.setInvoiceNumber("INV-"+System.currentTimeMillis()); i.setPatient(slot.getPatient()); i.setAppointment(slot); i.setIssueDate(LocalDate.now()); i.setDueDate(LocalDate.now().plusDays(30)); i.setSubtotal(BigDecimal.valueOf(amount)); i.setTotalAmount(BigDecimal.valueOf(amount)); i.setStatus(Invoice.Status.PENDING); return repo.save(i);
 }
 public Invoice getBill(Long id){return repo.findById(id).orElse(null);}
}
