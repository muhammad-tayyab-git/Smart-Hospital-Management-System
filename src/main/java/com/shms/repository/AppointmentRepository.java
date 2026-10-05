package com.shms.repository;
import com.shms.entity.AppointmentSlot;
import org.springframework.data.jpa.repository.JpaRepository;
public interface AppointmentRepository extends JpaRepository<AppointmentSlot,Long>{}
