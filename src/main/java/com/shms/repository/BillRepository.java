package com.shms.repository;
import com.shms.entity.Invoice;
import com.shms.entity.Patient;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.math.BigDecimal;
import java.util.*;
public interface BillRepository extends JpaRepository<Invoice,Long>{
 List<Invoice> findByPatient(Patient patient);
 List<Invoice> findByStatus(Invoice.Status status);
 @Query("SELECT i FROM Invoice i WHERE LOWER(i.patient.user.firstName) LIKE LOWER(CONCAT('%',:k,'%')) OR LOWER(i.patient.user.lastName) LIKE LOWER(CONCAT('%',:k,'%')) OR LOWER(i.invoiceNumber) LIKE LOWER(CONCAT('%',:k,'%')) OR LOWER(i.status) LIKE LOWER(CONCAT('%',:k,'%'))")
 List<Invoice> searchBills(@Param("k") String keyword);
 @Query(value="SELECT COALESCE(SUM(total_amount),0) FROM invoices WHERE status='PAID'", nativeQuery=true) BigDecimal totalRevenue();
 @Query(value="SELECT COUNT(*) FROM invoices WHERE status='PAID'", nativeQuery=true) Long countPaid();
 @Query(value="SELECT COUNT(*) FROM invoices WHERE status<>'PAID'", nativeQuery=true) Long countUnpaid();
}
