package com.shms.repository;
import com.shms.entity.MedicalRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
public interface MedicalRecordRepository extends JpaRepository<MedicalRecord, Long> { List<MedicalRecord> findByPatientId(Long patientId); MedicalRecord findByAppointmentId(Long appointmentId); }
