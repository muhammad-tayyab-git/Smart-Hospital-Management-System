package com.shms.repository;
import com.shms.entity.DoctorSchedule;import org.springframework.data.jpa.repository.JpaRepository;import java.time.DayOfWeek;import java.util.List;
public interface DoctorScheduleRepository extends JpaRepository<DoctorSchedule,Long>{List<DoctorSchedule> findByDoctorIdAndDayOfWeekAndStatus(Long doctorId,DayOfWeek day,DoctorSchedule.Status status);}
