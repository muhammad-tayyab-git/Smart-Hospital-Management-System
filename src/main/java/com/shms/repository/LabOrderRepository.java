package com.shms.repository;
import com.shms.entity.LabOrder;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
public interface LabOrderRepository extends JpaRepository<LabOrder, Long> { List<LabOrder> findByPatientId(Long patientId); }
