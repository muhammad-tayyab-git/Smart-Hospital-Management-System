package com.shms.repository;
import com.shms.entity.Bill;
import com.shms.entity.Patient;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface BillRepository extends JpaRepository<Bill, Long> {
    List<Bill> findByPatient(Patient patient);
    @Query("""
        SELECT b FROM Bill b
        LEFT JOIN b.appointment a
        LEFT JOIN a.doctor d
        WHERE
         LOWER(b.patient.fullName) LIKE LOWER(CONCAT('%', :keyword, '%'))
         OR LOWER(d.fullName) LIKE LOWER(CONCAT('%', :keyword, '%'))
         OR LOWER(b.description) LIKE LOWER(CONCAT('%', :keyword, '%'))
         OR LOWER(b.status) LIKE LOWER(CONCAT('%', :keyword, '%'))
         OR CAST(b.amount AS string) LIKE CONCAT('%', :keyword, '%')
        """)
    List<Bill> searchBills(@Param("keyword") String keyword);
    List<Bill> findByStatus(String status);
    @Query("SELECT SUM(b.amount) FROM Bill b WHERE b.status = 'PAID'")
    Double totalRevenue();

    @Query("SELECT COUNT(b) FROM Bill b WHERE b.status = 'PAID'")
    Long countPaid();

    @Query("SELECT COUNT(b) FROM Bill b WHERE b.status != 'PAID'")
    Long countUnpaid();
}
