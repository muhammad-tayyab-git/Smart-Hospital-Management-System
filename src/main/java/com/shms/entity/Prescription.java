package com.shms.entity;
import jakarta.persistence.*; import java.time.*; import java.util.*;
@Entity @Table(name="prescriptions") public class Prescription {
 @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id;
 @ManyToOne(fetch=FetchType.LAZY) @JoinColumn(name="medical_record_id",nullable=false) private MedicalRecord medicalRecord;
 @ManyToOne(fetch=FetchType.LAZY) @JoinColumn(name="doctor_id",nullable=false) private Doctor doctor;
 @ManyToOne(fetch=FetchType.LAZY) @JoinColumn(name="patient_id",nullable=false) private Patient patient;
 @Column(name="prescription_date",nullable=false) private LocalDateTime prescriptionDate; @Column(columnDefinition="TEXT") private String instructions;
 @Enumerated(EnumType.STRING) private Status status=Status.ACTIVE; @Column(name="created_at") private LocalDateTime createdAt;
 @OneToMany(mappedBy="prescription",cascade=CascadeType.ALL,orphanRemoval=true) private List<PrescriptionItem> items=new ArrayList<>();
 public enum Status{ACTIVE,COMPLETED,CANCELLED} @PrePersist void pre(){if(prescriptionDate==null)prescriptionDate=LocalDateTime.now();createdAt=LocalDateTime.now();}
 public Long getId(){return id;} public void setId(Long v){id=v;} public MedicalRecord getMedicalRecord(){return medicalRecord;} public void setMedicalRecord(MedicalRecord v){medicalRecord=v;} public Doctor getDoctor(){return doctor;} public void setDoctor(Doctor v){doctor=v;} public Patient getPatient(){return patient;} public void setPatient(Patient v){patient=v;} public LocalDateTime getPrescriptionDate(){return prescriptionDate;} public void setPrescriptionDate(LocalDateTime v){prescriptionDate=v;} public String getInstructions(){return instructions;} public void setInstructions(String v){instructions=v;} public Status getStatus(){return status;} public void setStatus(Status v){status=v;} public LocalDateTime getCreatedAt(){return createdAt;} public List<PrescriptionItem> getItems(){return items;} public void setItems(List<PrescriptionItem> v){items=v;}
}
