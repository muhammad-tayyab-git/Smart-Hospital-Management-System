package com.shms.entity;

import jakarta.persistence.*;

@Entity @Table(name="doctors")
public class Doctor {
    @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id;
    @OneToOne(fetch=FetchType.EAGER) @JoinColumn(name="user_id", nullable=false, unique=true) private User user;
    @Column(name="doctor_number", nullable=false, unique=true) private String doctorNumber;
    @ManyToOne(fetch=FetchType.EAGER) @JoinColumn(name="department_id", nullable=false) private Department department;
    private String specialization;
    private String qualification;
    @Column(name="license_number") private String licenseNumber;
    @Column(name="experience_years") private Integer experienceYears=0;
    @Column(name="consultation_fee") private java.math.BigDecimal consultationFee=java.math.BigDecimal.ZERO;
    @Column(name="bio", columnDefinition="TEXT") private String bio;
    @Enumerated(EnumType.STRING) private Status status=Status.ACTIVE;
    @Column(name="created_at") private java.time.LocalDateTime createdAt;
    @Column(name="updated_at") private java.time.LocalDateTime updatedAt;
    public enum Status{ACTIVE,INACTIVE,ON_LEAVE}
    @PrePersist void pre(){createdAt=java.time.LocalDateTime.now();updatedAt=createdAt;}
    @PreUpdate void upd(){updatedAt=java.time.LocalDateTime.now();}
    public Long getId(){return id;} public void setId(Long v){id=v;}
    public User getUser(){return user;} public void setUser(User v){user=v;}
    public String getDoctorNumber(){return doctorNumber;} public void setDoctorNumber(String v){doctorNumber=v;}
    public Department getDepartment(){return department;} public void setDepartment(Department v){department=v;}
    public String getSpecialization(){return specialization;} public void setSpecialization(String v){specialization=v;}
    public String getQualification(){return qualification;} public void setQualification(String v){qualification=v;}
    public String getLicenseNumber(){return licenseNumber;} public void setLicenseNumber(String v){licenseNumber=v;}
    public Integer getExperienceYears(){return experienceYears;} public void setExperienceYears(Integer v){experienceYears=v;}
    public java.math.BigDecimal getConsultationFee(){return consultationFee;} public void setConsultationFee(java.math.BigDecimal v){consultationFee=v;}
    public String getBio(){return bio;} public void setBio(String v){bio=v;}
    public Status getStatus(){return status;} public void setStatus(Status v){status=v;}
    @Transient public String getFullName(){return user==null?"":user.getFullName();}
    public void setFullName(String ignored){}
    @Transient public String getPhone(){return user==null?null:user.getPhone();}
    public void setPhone(String v){if(user!=null)user.setPhone(v);}
    @Transient public String getExperience(){return (experienceYears==null?0:experienceYears)+" years";}
    public void setExperience(String v){ if(v!=null){ try{experienceYears=Integer.parseInt(v.replaceAll("\\D+",""));}catch(Exception ignored){} } }
    @Transient public boolean isActive(){return status==Status.ACTIVE;}
    public void setActive(boolean v){status=v?Status.ACTIVE:Status.INACTIVE;}
    @Transient public String getImagePath(){return user==null?null:user.getProfileImageUrl();}
    public void setImagePath(String v){if(user!=null)user.setProfileImageUrl(v);}
}
