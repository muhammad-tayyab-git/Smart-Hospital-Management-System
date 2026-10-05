package com.shms.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name="departments")
public class Department {
    @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id;
    @Column(nullable=false, unique=true) private String name;
    @Column(nullable=false, unique=true) private String code;
    @Column(name="description", columnDefinition="TEXT") private String description;
    private String location;
    private String phone;
    @Enumerated(EnumType.STRING) private Status status=Status.ACTIVE;
    @Column(name="created_at") private LocalDateTime createdAt;
    @Column(name="updated_at") private LocalDateTime updatedAt;
    @PrePersist void pre(){createdAt=LocalDateTime.now(); updatedAt=createdAt;}
    @PreUpdate void upd(){updatedAt=LocalDateTime.now();}
    public enum Status{ACTIVE,INACTIVE}
    public Long getId(){return id;} public void setId(Long v){id=v;}
    public String getName(){return name;} public void setName(String v){name=v;}
    public String getCode(){return code;} public void setCode(String v){code=v;}
    public String getDescription(){return description;} public void setDescription(String v){description=v;}
    public String getLocation(){return location;} public void setLocation(String v){location=v;}
    public String getPhone(){return phone;} public void setPhone(String v){phone=v;}
    public Status getStatus(){return status;} public void setStatus(Status v){status=v;}
    public LocalDateTime getCreatedAt(){return createdAt;} public LocalDateTime getUpdatedAt(){return updatedAt;}
}
