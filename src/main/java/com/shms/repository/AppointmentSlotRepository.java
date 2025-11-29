package com.shms.repository;

import com.shms.entity.AppointmentSlot;
import com.shms.entity.Doctor;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface AppointmentSlotRepository extends JpaRepository<AppointmentSlot, Long> {
    List<AppointmentSlot> findByDoctorAndDate(Doctor doctor, LocalDate date);
    List<AppointmentSlot> findByPatientId(Long patientId);

    List<AppointmentSlot> findByDoctorIdAndDateAndAvailableTrueAndBookedFalse(Long doctorId, LocalDate date);
}

