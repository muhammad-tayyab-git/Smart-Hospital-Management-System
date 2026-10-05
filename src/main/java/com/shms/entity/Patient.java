package com.shms.entity;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Period;

@Entity @Table(name="patients")
public class Patient {
    @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id;
    @OneToOne(fetch=FetchType.EAGER) @JoinColumn(name="user_id", nullable=false, unique=true) private User user;
    @Column(name="patient_number", nullable=false, unique=true) private String patientNumber;
    @Column(name="date_of_birth") private LocalDate dateOfBirth;
    @Enumerated(EnumType.STRING) private Gender gender;
    @Column(name="blood_group") @Enumerated(EnumType.STRING) private BloodGroup bloodGroup;
    @Column(name="address", columnDefinition="TEXT") private String address;
    @Column(name="emergency_contact_name") private String emergencyContactName;
    @Column(name="emergency_contact_phone") private String emergencyContactPhone;
    @Column(name="allergies", columnDefinition="TEXT") private String allergies;
    @Enumerated(EnumType.STRING) private Status status=Status.ACTIVE;
    @Column(name="created_at") private LocalDateTime createdAt;
    @Column(name="updated_at") private LocalDateTime updatedAt;
    public enum Gender{MALE,FEMALE,OTHER,PREFER_NOT_TO_SAY}
    public enum BloodGroup{A_POSITIVE,A_NEGATIVE,B_POSITIVE,B_NEGATIVE,AB_POSITIVE,AB_NEGATIVE,O_POSITIVE,O_NEGATIVE}
    public enum Status{ACTIVE,INACTIVE}
    @PrePersist void pre(){createdAt=LocalDateTime.now();updatedAt=createdAt;}
    @PreUpdate void upd(){updatedAt=LocalDateTime.now();}
    public Long getId(){return id;} public void setId(Long v){id=v;}
    public User getUser(){return user;} public void setUser(User v){user=v;}
    public String getPatientNumber(){return patientNumber;} public void setPatientNumber(String v){patientNumber=v;}
    public LocalDate getDateOfBirth(){return dateOfBirth;} public void setDateOfBirth(LocalDate v){dateOfBirth=v;}
    public Gender getGender(){return gender;} public void setGender(Gender v){gender=v;}
    public BloodGroup getBloodGroup(){return bloodGroup;} public void setBloodGroup(BloodGroup v){bloodGroup=v;}
    public String getAddress(){return address;} public void setAddress(String v){address=v;}
    public String getEmergencyContactName(){return emergencyContactName;} public void setEmergencyContactName(String v){emergencyContactName=v;}
    public String getEmergencyContactPhone(){return emergencyContactPhone;} public void setEmergencyContactPhone(String v){emergencyContactPhone=v;}
    public String getAllergies(){return allergies;} public void setAllergies(String v){allergies=v;}
    public Status getStatus(){return status;} public void setStatus(Status v){status=v;}
    public LocalDateTime getCreatedAt(){return createdAt;} public LocalDateTime getUpdatedAt(){return updatedAt;}
    @Transient public String getFullName(){return user==null?"":user.getFullName();}
    public void setFullName(String ignored){}
    @Transient public String getEmail(){return user==null?null:user.getEmail();}
    public void setEmail(String ignored){}
    @Transient public String getPhone(){return user==null?null:user.getPhone();}
    public void setPhone(String v){if(user!=null)user.setPhone(v);}
    @Transient public int getAge(){return dateOfBirth==null?0:Period.between(dateOfBirth,LocalDate.now()).getYears();}
    public void setAge(int ignored){}
    @Transient public String getMedicalHistory(){return allergies;}
    public void setMedicalHistory(String v){allergies=v;}
}
