package com.shms.entity;
import jakarta.persistence.*; import java.time.*;
@Entity @Table(name="diagnoses") public class Diagnosis {
 @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id;
 @ManyToOne(fetch=FetchType.LAZY) @JoinColumn(name="medical_record_id",nullable=false) private MedicalRecord medicalRecord;
 @Column(name="diagnosis_code") private String diagnosisCode; @Column(name="diagnosis_name",nullable=false) private String diagnosisName; @Column(columnDefinition="TEXT") private String description;
 @Enumerated(EnumType.STRING) private Severity severity=Severity.MILD; @Column(name="created_at") private LocalDateTime createdAt;
 public enum Severity{MILD,MODERATE,SEVERE,CRITICAL} @PrePersist void pre(){createdAt=LocalDateTime.now();}
 public Long getId(){return id;} public void setId(Long v){id=v;} public MedicalRecord getMedicalRecord(){return medicalRecord;} public void setMedicalRecord(MedicalRecord v){medicalRecord=v;} public String getDiagnosisCode(){return diagnosisCode;} public void setDiagnosisCode(String v){diagnosisCode=v;} public String getDiagnosisName(){return diagnosisName;} public void setDiagnosisName(String v){diagnosisName=v;} public String getDescription(){return description;} public void setDescription(String v){description=v;} public Severity getSeverity(){return severity;} public void setSeverity(Severity v){severity=v;} public LocalDateTime getCreatedAt(){return createdAt;}
}
