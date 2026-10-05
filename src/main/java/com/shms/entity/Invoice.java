package com.shms.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity @Table(name="invoices")
public class Invoice {
 @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id;
 @Column(name="invoice_number",nullable=false,unique=true) private String invoiceNumber;
 @ManyToOne(fetch=FetchType.EAGER) @JoinColumn(name="patient_id",nullable=false) private Patient patient;
 @ManyToOne(fetch=FetchType.EAGER) @JoinColumn(name="appointment_id") private Appointment appointment;
 @Column(name="issue_date",nullable=false) private LocalDate issueDate;
 @Column(name="due_date") private LocalDate dueDate;
 @Column(precision=12, scale=2) private BigDecimal subtotal=BigDecimal.ZERO;
 @Column(precision=12, scale=2) private BigDecimal discount=BigDecimal.ZERO;
 @Column(precision=12, scale=2) private BigDecimal tax=BigDecimal.ZERO;
 @Column(name="total_amount", precision=12, scale=2) private BigDecimal totalAmount=BigDecimal.ZERO;
 @Enumerated(EnumType.STRING) private Status status=Status.DRAFT;
 @Column(name="created_at") private LocalDateTime createdAt;
 @Column(name="updated_at") private LocalDateTime updatedAt;
 public enum Status{DRAFT,PENDING,PARTIALLY_PAID,PAID,CANCELLED,OVERDUE}
 @PrePersist void pre(){if(issueDate==null)issueDate=LocalDate.now();createdAt=LocalDateTime.now();updatedAt=createdAt;}
 @PreUpdate void upd(){updatedAt=LocalDateTime.now();}
 public Long getId(){return id;} public void setId(Long v){id=v;}
 public String getInvoiceNumber(){return invoiceNumber;} public void setInvoiceNumber(String v){invoiceNumber=v;}
 public Patient getPatient(){return patient;} public void setPatient(Patient v){patient=v;}
 public Appointment getAppointment(){return appointment;} public void setAppointment(Appointment v){appointment=v;}
 public LocalDate getIssueDate(){return issueDate;} public void setIssueDate(LocalDate v){issueDate=v;}
 public LocalDate getDueDate(){return dueDate;} public void setDueDate(LocalDate v){dueDate=v;}
 public BigDecimal getSubtotal(){return subtotal;} public void setSubtotal(BigDecimal v){subtotal=v;}
 public BigDecimal getDiscount(){return discount;} public void setDiscount(BigDecimal v){discount=v;}
 public BigDecimal getTax(){return tax;} public void setTax(BigDecimal v){tax=v;}
 public BigDecimal getTotalAmount(){return totalAmount;} public void setTotalAmount(BigDecimal v){totalAmount=v;}
 public Status getStatus(){return status;} public void setStatus(Status v){status=v;}
 public LocalDateTime getCreatedAt(){return createdAt;} public LocalDateTime getUpdatedAt(){return updatedAt;}
 // View compatibility
 @Transient public BigDecimal getAmount(){return totalAmount;} public void setAmount(double v){totalAmount=BigDecimal.valueOf(v);}
 @Transient public String getCurrency(){return "EUR";}
 @Transient public String getDescription(){return appointment==null?"Hospital service":"Consultation with "+appointment.getDoctor().getFullName();}
 @Transient public LocalDateTime getPaidAt(){return null;} public void setPaidAt(LocalDateTime ignored){}
 public String getStatusString(){return status.name();}
}
