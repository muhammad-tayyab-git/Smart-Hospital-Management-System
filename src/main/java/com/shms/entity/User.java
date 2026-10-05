package com.shms.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@Entity
@Table(name="users")
public class User {
    @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id;
    @Column(nullable=false, unique=true, length=255) private String email;
    @Column(name="password_hash", nullable=false, length=255) private String password;
    @Column(name="first_name", nullable=false, length=100) private String firstName;
    @Column(name="last_name", nullable=false, length=100) private String lastName;
    private String phone;
    @Column(name="profile_image_url") private String profileImageUrl;
    @Enumerated(EnumType.STRING) private Status status=Status.ACTIVE;
    @Column(name="last_login_at") private LocalDateTime lastLoginAt;
    @Column(name="created_at") private LocalDateTime createdAt;
    @Column(name="updated_at") private LocalDateTime updatedAt;
    @ManyToMany(fetch=FetchType.EAGER)
    @JoinTable(name="user_roles", joinColumns=@JoinColumn(name="user_id"), inverseJoinColumns=@JoinColumn(name="role_id"))
    private Set<Role> roles=new HashSet<>();
    public enum Status{ACTIVE,INACTIVE,SUSPENDED,PENDING}
    @PrePersist void pre(){createdAt=LocalDateTime.now(); updatedAt=createdAt;}
    @PreUpdate void upd(){updatedAt=LocalDateTime.now();}
    public Long getId(){return id;} public void setId(Long v){id=v;}
    public String getEmail(){return email;} public void setEmail(String v){email=v==null?null:v.trim().toLowerCase();}
    public String getPassword(){return password;} public void setPassword(String v){password=v;}
    public String getFirstName(){return firstName;} public void setFirstName(String v){firstName=v;}
    public String getLastName(){return lastName;} public void setLastName(String v){lastName=v;}
    public String getPhone(){return phone;} public void setPhone(String v){phone=v;}
    public String getProfileImageUrl(){return profileImageUrl;} public void setProfileImageUrl(String v){profileImageUrl=v;}
    public Status getStatus(){return status;} public void setStatus(Status v){status=v;}
    public LocalDateTime getLastLoginAt(){return lastLoginAt;} public void setLastLoginAt(LocalDateTime v){lastLoginAt=v;}
    public LocalDateTime getCreatedAt(){return createdAt;} public LocalDateTime getUpdatedAt(){return updatedAt;}
    public Set<Role> getRoles(){return roles;} public void setRoles(Set<Role> v){roles=v;}
    public String getRole(){
        if (roles == null || roles.isEmpty()) return "PATIENT";
        // Roles are normally exclusive. Keep the result deterministic if legacy data contains duplicates.
        String[] priority = {"ADMIN","DOCTOR","RECEPTIONIST","NURSE","PHARMACIST","LAB_TECHNICIAN","PATIENT"};
        for (String candidate : priority) {
            if (roles.stream().anyMatch(r -> candidate.equals(r.getName()))) return candidate;
        }
        return "PATIENT";
    }
    @Transient public boolean hasRole(String role){ return role != null && role.equals(getRole()); }
    // Backward-compatible view: the application no longer stores usernames.
    @Transient public String getUsername(){return email;}
    public void setUsername(String ignored){ /* username removed in V2 */ }
    @Transient public String getFullName(){return ((firstName==null?"":firstName)+" "+(lastName==null?"":lastName)).trim();}
}
