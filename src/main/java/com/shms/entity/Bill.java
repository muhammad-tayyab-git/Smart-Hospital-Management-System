package com.shms.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name="bills")
public class Bill {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name="appointment_id")
    private AppointmentSlot appointment; // optional link

    @ManyToOne
    @JoinColumn(name="patient_id", nullable=false)
    private Patient patient;

    private Double amount;
    private String currency = "EUR";
    private String description;

    private String status = "PENDING"; // UNPAID, PAID, REFUNDED

    private LocalDateTime createdAt = LocalDateTime.now();
    private LocalDateTime paidAt;

    // getters/setters

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public AppointmentSlot getAppointment() {
        return appointment;
    }

    public void setAppointment(AppointmentSlot appointment) {
        this.appointment = appointment;
    }

    public Patient getPatient() {
        return patient;
    }

    public void setPatient(Patient patient) {
        this.patient = patient;
    }

    public Double getAmount() {
        return amount;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getPaidAt() {
        return paidAt;
    }

    public void setPaidAt(LocalDateTime paidAt) {
        this.paidAt = paidAt;
    }
    // no-arg constructor
    public Bill() {

    }

}
