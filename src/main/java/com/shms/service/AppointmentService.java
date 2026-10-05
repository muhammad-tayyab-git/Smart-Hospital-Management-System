package com.shms.service;
import com.shms.dto.AppointmentView;import com.shms.entity.*;import com.shms.repository.*;import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;import java.time.*;import java.util.*;
@Service public class AppointmentService{
 private final AppointmentRepository appointments;private final DoctorScheduleRepository schedules;
 public AppointmentService(AppointmentRepository a,DoctorScheduleRepository s){appointments=a;schedules=s;}
 public List<AppointmentView> availableSlots(Doctor doctor,LocalDate date){
  List<AppointmentView> result=new ArrayList<>();
  for(DoctorSchedule s:schedules.findByDoctorIdAndDayOfWeekAndStatus(doctor.getId(),date.getDayOfWeek(),DoctorSchedule.Status.AVAILABLE)){
   for(LocalTime t=s.getStartTime();t.plusMinutes(s.getSlotDurationMinutes()).compareTo(s.getEndTime())<=0;t=t.plusMinutes(s.getSlotDurationMinutes())){
    LocalTime end=t.plusMinutes(s.getSlotDurationMinutes());
    LocalTime slotStart=t; boolean taken=appointments.findByDoctorIdAndDateBetween(doctor.getId(),date,date).stream().anyMatch(a->a.getStartTime().equals(slotStart)&&a.getStatus()!=Appointment.AppointmentStatus.CANCELLED);
    if(!taken)result.add(new AppointmentView(doctor.getId(),date,t,end,true));
   }
  } return result;
 }
 @Transactional
 public Appointment createBooking(Doctor doctor,Patient patient,LocalDate date,LocalTime start,LocalTime end,String reason){
  if(date==null || date.isBefore(LocalDate.now())) throw new IllegalArgumentException("Appointment date must be today or later");
  if(start==null || end==null || !start.isBefore(end)) throw new IllegalArgumentException("Invalid appointment time");
  boolean overlap=appointments.findByDoctorIdAndDateBetween(doctor.getId(),date,date).stream()
          .filter(a->a.getStatus()!=Appointment.AppointmentStatus.CANCELLED)
          .anyMatch(a->start.isBefore(a.getEndTime()) && end.isAfter(a.getStartTime()));
  if(overlap) throw new IllegalStateException("Appointment slot is already booked");
  Appointment a=new Appointment();a.setAppointmentNumber("APT-"+System.currentTimeMillis());a.setDoctor(doctor);a.setPatient(patient);a.setDepartment(doctor.getDepartment());a.setDate(date);a.setStartTime(start);a.setEndTime(end);a.setReason(reason);a.setStatus(Appointment.AppointmentStatus.CONFIRMED);return appointments.save(a);
 }
}
