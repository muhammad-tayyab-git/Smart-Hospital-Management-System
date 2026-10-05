package com.shms.service;
import com.shms.dto.AppointmentSlotView;import com.shms.entity.*;import com.shms.repository.*;import org.springframework.stereotype.Service;import java.time.*;import java.util.*;
@Service public class AppointmentService{
 private final AppointmentSlotRepository appointments;private final DoctorScheduleRepository schedules;
 public AppointmentService(AppointmentSlotRepository a,DoctorScheduleRepository s){appointments=a;schedules=s;}
 public List<AppointmentSlotView> availableSlots(Doctor doctor,LocalDate date){
  List<AppointmentSlotView> result=new ArrayList<>();
  for(DoctorSchedule s:schedules.findByDoctorIdAndDayOfWeekAndStatus(doctor.getId(),date.getDayOfWeek(),DoctorSchedule.Status.AVAILABLE)){
   for(LocalTime t=s.getStartTime();t.plusMinutes(s.getSlotDurationMinutes()).compareTo(s.getEndTime())<=0;t=t.plusMinutes(s.getSlotDurationMinutes())){
    LocalTime end=t.plusMinutes(s.getSlotDurationMinutes());
    LocalTime slotStart=t; boolean taken=appointments.findByDoctorIdAndDateBetween(doctor.getId(),date,date).stream().anyMatch(a->a.getStartTime().equals(slotStart)&&a.getStatus()!=AppointmentSlot.AppointmentStatus.CANCELLED);
    if(!taken)result.add(new AppointmentSlotView(doctor.getId(),date,t,end,true));
   }
  } return result;
 }
 public AppointmentSlot createBooking(Doctor doctor,Patient patient,LocalDate date,LocalTime start,LocalTime end,String reason){
  AppointmentSlot a=new AppointmentSlot();a.setAppointmentNumber("APT-"+System.currentTimeMillis());a.setDoctor(doctor);a.setPatient(patient);a.setDepartment(doctor.getDepartment());a.setDate(date);a.setStartTime(start);a.setEndTime(end);a.setReason(reason);a.setStatus(AppointmentSlot.AppointmentStatus.CONFIRMED);return appointments.save(a);
 }
}
