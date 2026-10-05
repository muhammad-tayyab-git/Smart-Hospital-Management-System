package com.shms.repository;
import com.shms.entity.Admission;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
public interface AdmissionRepository extends JpaRepository<Admission, Long> { List<Admission> findByPatientId(Long patientId); }
