
package com.shms.repository;
import com.shms.entity.Doctor;
import org.springframework.data.jpa.repository.JpaRepository;
public interface DoctorRepository extends JpaRepository<Doctor, Long> {
    Doctor findByUserId(Long userId);
}
