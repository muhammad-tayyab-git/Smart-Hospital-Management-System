package com.shms.repository;
import com.shms.entity.Doctor;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;
public interface DoctorRepository extends JpaRepository<Doctor,Long>{ Optional<Doctor> findOptionalByUserId(Long userId); Doctor findByUserId(Long userId); }
