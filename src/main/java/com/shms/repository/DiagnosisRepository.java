package com.shms.repository;
import com.shms.entity.Diagnosis;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
public interface DiagnosisRepository extends JpaRepository<Diagnosis, Long> { List<Diagnosis> findByMedicalRecordId(Long medicalRecordId); }
