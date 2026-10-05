package com.shms.entity;
import jakarta.persistence.*;import java.time.*;
@Entity @Table(name="doctor_schedules") public class DoctorSchedule{
 @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id;
 @ManyToOne(fetch=FetchType.EAGER) @JoinColumn(name="doctor_id",nullable=false) private Doctor doctor;
 @Enumerated(EnumType.STRING) @Column(name="day_of_week",nullable=false) private DayOfWeek dayOfWeek;
 @Column(name="start_time",nullable=false) private LocalTime startTime;@Column(name="end_time",nullable=false) private LocalTime endTime;
 @Column(name="slot_duration_minutes",nullable=false) private Integer slotDurationMinutes=30;
 @Enumerated(EnumType.STRING) private Status status=Status.AVAILABLE;
 public enum Status{AVAILABLE,UNAVAILABLE}
 public Long getId(){return id;} public Doctor getDoctor(){return doctor;} public DayOfWeek getDayOfWeek(){return dayOfWeek;} public LocalTime getStartTime(){return startTime;} public LocalTime getEndTime(){return endTime;} public Integer getSlotDurationMinutes(){return slotDurationMinutes;} public Status getStatus(){return status;}
}
