package com.shms.entity;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "patients")
public class Patient {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String fullName;
    private String gender;
    private int age;
    private String phone;
    private String address;
    private String email;
    @Column(name = "created_at")
    private LocalDateTime createdAt=LocalDateTime.now();



    @Lob
    private String medicalHistory;

    @OneToOne
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    private User user; // link to user table

    public Patient() {}

    public Patient(String fullName, String gender, int age, String phone, String address, String email, String medicalHistory, User user) {
        this.fullName = fullName;
        this.gender = gender;
        this.age = age;
        this.phone = phone;
        this.address = address;
        this.email = email;
        this.medicalHistory = medicalHistory;
        this.user = user;
    }

    // Getters & Setters
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }

    public String getGender() { return gender; }
    public void setGender(String gender) { this.gender = gender; }

    public int getAge() { return age; }
    public void setAge(int age) { this.age = age; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getMedicalHistory() { return medicalHistory; }
    public void setMedicalHistory(String medicalHistory) { this.medicalHistory = medicalHistory; }

    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }
}
