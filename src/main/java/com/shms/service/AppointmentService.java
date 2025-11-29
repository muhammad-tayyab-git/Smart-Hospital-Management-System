package com.shms.service;

import com.shms.entity.AppointmentSlot;
import com.shms.entity.Doctor;
import com.shms.repository.AppointmentSlotRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.*;
import java.util.ArrayList;
import java.util.List;

@Service
public class AppointmentService {

    @Autowired
    private AppointmentSlotRepository slotRepo;

    // Generate slots for a doctor for the next year (9AM–5PM, 30-min each)
    public void generateSlotsForDoctor(Doctor doctor) {
        LocalDate startDate = LocalDate.now();
        LocalDate endDate = startDate.plusYears(1);

        List<AppointmentSlot> slots = new ArrayList<>();

        for (LocalDate date = startDate; date.isBefore(endDate); date = date.plusDays(1)) {
            // Skip weekends
            if (date.getDayOfWeek() == DayOfWeek.SATURDAY || date.getDayOfWeek() == DayOfWeek.SUNDAY)
                continue;

            LocalTime startTime = LocalTime.of(9, 0);
            LocalTime endTime = LocalTime.of(17, 0);

            while (startTime.isBefore(endTime)) {
                AppointmentSlot slot = new AppointmentSlot();
                slot.setDoctor(doctor);
                slot.setDate(date);
                slot.setStartTime(startTime);
                slot.setEndTime(startTime.plusMinutes(30));
                slot.setAvailable(true);
                slot.setBooked(false);

                slots.add(slot);
                startTime = startTime.plusMinutes(30);
            }
        }

        slotRepo.saveAll(slots);
        System.out.println("✅ Generated " + slots.size() + " slots for " + doctor.getFullName());
    }
}
