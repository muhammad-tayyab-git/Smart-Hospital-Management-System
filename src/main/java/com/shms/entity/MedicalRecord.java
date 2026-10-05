package com.shms.entity;
import jakarta.persistence.*; import java.time.*;
@Entity @Table(name="medical_records") public class MedicalRecord {
 @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id;
 @ManyToOne(fetch=FetchType.LAZY) @JoinColumn(name="patient_id",nullable=false) private Patient patient;
 @ManyToOne(fetch=FetchType.LAZY) @JoinColumn(name="doctor_id",nullable=false) private Doctor doctor;
 @OneToOne(fetch=FetchType.LAZY) @JoinColumn(name="appointment_id",unique=true) private Appointment appointment;
 @Column(name="visit_date",nullable=false) private LocalDateTime visitDate;
 @Column(name="chief_complaint",columnDefinition="TEXT") private String chiefComplaint;
 @Column(columnDefinition="TEXT") private String symptoms;
 @Column(name="clinical_notes",columnDefinition="TEXT") private String clinicalNotes;
 @Column(name="diagnosis_summary",columnDefinition="TEXT") private String diagnosisSummary;
 @Column(name="treatment_plan",columnDefinition="TEXT") private String treatmentPlan;
 @Column(name="follow_up_date") private LocalDate followUpDate;
 @Column(name="created_at") private LocalDateTime createdAt; @Column(name="updated_at") private LocalDateTime updatedAt;
 @PrePersist void pre(){if(visitDate==null)visitDate=LocalDateTime.now();createdAt=updatedAt=LocalDateTime.now();} @PreUpdate void upd(){updatedAt=LocalDateTime.now();}
 public Long getId(){return id;} public void setId(Long v){id=v;} public Patient getPatient(){return patient;} public void setPatient(Patient v){patient=v;} public Doctor getDoctor(){return doctor;} public void setDoctor(Doctor v){doctor=v;} public Appointment getAppointment(){return appointment;} public void setAppointment(Appointment v){appointment=v;} public LocalDateTime getVisitDate(){return visitDate;} public void setVisitDate(LocalDateTime v){visitDate=v;} public String getChiefComplaint(){return chiefComplaint;} public void setChiefComplaint(String v){chiefComplaint=v;} public String getSymptoms(){return symptoms;} public void setSymptoms(String v){symptoms=v;} public String getClinicalNotes(){return clinicalNotes;} public void setClinicalNotes(String v){clinicalNotes=v;} public String getDiagnosisSummary(){return diagnosisSummary;} public void setDiagnosisSummary(String v){diagnosisSummary=v;} public String getTreatmentPlan(){return treatmentPlan;} public void setTreatmentPlan(String v){treatmentPlan=v;} public LocalDate getFollowUpDate(){return followUpDate;} public void setFollowUpDate(LocalDate v){followUpDate=v;} public LocalDateTime getCreatedAt(){return createdAt;} public LocalDateTime getUpdatedAt(){return updatedAt;}
}
