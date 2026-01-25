package com.shms.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "activities")
public class Activity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String description;
    private String user;
    private String type;         // for badge color (optional)
    private LocalDateTime timestamp;

    public Activity() {}

    @PrePersist
    protected void onCreate() {
        this.timestamp = LocalDateTime.now();
    }

    // getters and setters
    public Long getId() { return id; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getUser() { return user; }
    public void setUser(String user) { this.user = user; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public LocalDateTime getTimestamp() { return timestamp; }
}
