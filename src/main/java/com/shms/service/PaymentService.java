package com.shms.service;

import com.shms.entity.*;
import com.shms.repository.InvoiceRepository;
import com.shms.repository.PaymentRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.util.List;

@Service
public class PaymentService {
    private final PaymentRepository payments; private final InvoiceRepository invoices;
    public PaymentService(PaymentRepository payments,InvoiceRepository invoices){this.payments=payments;this.invoices=invoices;}
    public List<Payment> findForInvoice(Long invoiceId){return payments.findByInvoiceId(invoiceId);}
    @Transactional
    public Payment recordCompletedPayment(Invoice invoice,BigDecimal amount,Payment.PaymentMethod method,String reference){
        if(amount==null || amount.signum()<=0) throw new IllegalArgumentException("Payment amount must be positive");
        if(invoice.getStatus()==Invoice.Status.CANCELLED || invoice.getStatus()==Invoice.Status.PAID) throw new IllegalStateException("This invoice is not payable");
        BigDecimal alreadyPaid=payments.findByInvoiceId(invoice.getId()).stream().filter(x->x.getStatus()==Payment.Status.COMPLETED).map(Payment::getAmount).reduce(BigDecimal.ZERO,BigDecimal::add); if(alreadyPaid.add(amount).compareTo(invoice.getTotalAmount())>0) throw new IllegalArgumentException("Payment exceeds the remaining invoice balance");
        Payment p=new Payment();p.setInvoice(invoice);p.setAmount(amount);p.setPaymentMethod(method);p.setTransactionReference(reference);p.setStatus(Payment.Status.COMPLETED);payments.save(p);
        BigDecimal paid=payments.findByInvoiceId(invoice.getId()).stream().filter(x->x.getStatus()==Payment.Status.COMPLETED).map(Payment::getAmount).reduce(BigDecimal.ZERO,BigDecimal::add);
        invoice.setStatus(paid.compareTo(invoice.getTotalAmount())>=0?Invoice.Status.PAID:Invoice.Status.PARTIALLY_PAID);invoices.save(invoice);return p;
    }
}
