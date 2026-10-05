package com.shms.repository;
import com.shms.entity.Prescription;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
public interface PrescriptionRepository extends JpaRepository<Prescription, Long> { List<Prescription> findByPatientId(Long patientId); }
