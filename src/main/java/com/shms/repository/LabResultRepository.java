package com.shms.repository;
import com.shms.entity.LabResult;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
public interface LabResultRepository extends JpaRepository<LabResult, Long> { List<LabResult> findByLabOrderId(Long labOrderId); }
