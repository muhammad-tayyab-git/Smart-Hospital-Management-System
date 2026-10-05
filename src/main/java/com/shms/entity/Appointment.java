package com.shms.entity;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Entity
@Table(name = "appointments")
public class Appointment {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "appointment_number", nullable = false, unique = true)
    private String appointmentNumber;

    @ManyToOne(fetch = FetchType.EAGER) @JoinColumn(name = "patient_id", nullable = false)
    private Patient patient;

    @ManyToOne(fetch = FetchType.EAGER) @JoinColumn(name = "doctor_id", nullable = false)
    private Doctor doctor;

    @ManyToOne(fetch = FetchType.EAGER) @JoinColumn(name = "department_id", nullable = false)
    private Department department;

    @Column(name = "appointment_date", nullable = false)
    private LocalDate date;

    @Column(name = "start_time", nullable = false) private LocalTime startTime;
    @Column(name = "end_time", nullable = false) private LocalTime endTime;

    @Enumerated(EnumType.STRING) @Column(name = "appointment_type")
    private AppointmentType appointmentType = AppointmentType.CONSULTATION;

    @Column(name = "reason", columnDefinition = "TEXT") private String reason;

    @Enumerated(EnumType.STRING)
    private AppointmentStatus status = AppointmentStatus.REQUESTED;

    @Column(name = "notes", columnDefinition = "TEXT") private String notes;
    @Column(name = "created_at") private LocalDateTime createdAt;
    @Column(name = "updated_at") private LocalDateTime updatedAt;

    public enum AppointmentType { CONSULTATION, FOLLOW_UP, EMERGENCY, CHECKUP }
    public enum AppointmentStatus { REQUESTED, CONFIRMED, IN_PROGRESS, COMPLETED, CANCELLED, NO_SHOW }

    @PrePersist void prePersist() {
        if (createdAt == null) createdAt = LocalDateTime.now();
        updatedAt = createdAt;
    }
    @PreUpdate void preUpdate() { updatedAt = LocalDateTime.now(); }

    public Long getId(){return id;} public void setId(Long v){id=v;}
    public String getAppointmentNumber(){return appointmentNumber;} public void setAppointmentNumber(String v){appointmentNumber=v;}
    public Patient getPatient(){return patient;} public void setPatient(Patient v){patient=v;}
    public Doctor getDoctor(){return doctor;} public void setDoctor(Doctor v){doctor=v;}
    public Department getDepartment(){return department;} public void setDepartment(Department v){department=v;}
    public LocalDate getDate(){return date;} public void setDate(LocalDate v){date=v;}
    public LocalTime getStartTime(){return startTime;} public void setStartTime(LocalTime v){startTime=v;}
    public LocalTime getEndTime(){return endTime;} public void setEndTime(LocalTime v){endTime=v;}
    public AppointmentType getAppointmentType(){return appointmentType;} public void setAppointmentType(AppointmentType v){appointmentType=v;}
    public String getReason(){return reason;} public void setReason(String v){reason=v;}
    public AppointmentStatus getStatus(){return status;} public void setStatus(AppointmentStatus v){status=v;}
    public String getNotes(){return notes;} public void setNotes(String v){notes=v;}
    public LocalDateTime getCreatedAt(){return createdAt;} public LocalDateTime getUpdatedAt(){return updatedAt;}

    @Transient public boolean isBooked(){return patient!=null;}
    public void setBooked(boolean ignored){}
    @Transient public boolean isAvailable(){return status==AppointmentStatus.REQUESTED && patient==null;}
    public void setAvailable(boolean ignored){}
    @Transient public boolean isAttended(){return status==AppointmentStatus.COMPLETED;}
    public void setAttended(Boolean v){if(Boolean.TRUE.equals(v))status=AppointmentStatus.COMPLETED;}
    @Transient public boolean isAutoMarked(){return false;}
    public void setAutoMarked(Boolean ignored){}
}
