package com.shms.repository;

import com.shms.entity.Appointment;
import com.shms.entity.Doctor;
import org.springframework.data.jpa.repository.JpaRepository;
import java.time.LocalDate;
import java.util.List;

public interface AppointmentRepository extends JpaRepository<Appointment, Long> {
    List<Appointment> findByDoctorAndDate(Doctor doctor, LocalDate date);
    List<Appointment> findByPatientId(Long patientId);
    List<Appointment> findByDoctorIdAndDateAndStatus(Long doctorId, LocalDate date, Appointment.AppointmentStatus status);
    List<Appointment> findByDoctorIdAndDateBetween(Long doctorId, LocalDate from, LocalDate to);
    List<Appointment> findByDoctorId(Long doctorId);
    List<Appointment> findByStatus(Appointment.AppointmentStatus status);
}
