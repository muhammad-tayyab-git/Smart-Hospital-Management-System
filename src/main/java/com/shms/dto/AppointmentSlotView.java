package com.shms.dto;
import java.time.*;
public record AppointmentSlotView(Long doctorId, LocalDate date, LocalTime startTime, LocalTime endTime, boolean available) {}
