package com.shms.dto;

import java.time.LocalDateTime;

public class ActivityDTO {
    private String title;
    private String description;
    private String type; // e.g., "success", "warning", "danger"
    private LocalDateTime timestamp;

    // Constructor
    public ActivityDTO(String title, String description, String type, LocalDateTime timestamp) {
        this.title = title;
        this.description = description;
        this.type = type;
        this.timestamp = timestamp;
    }

    // Getters & Setters
    public String getTitle() { return title; }
    public String getDescription() { return description; }
    public String getType() { return type; }
    public LocalDateTime getTimestamp() { return timestamp; }
}
