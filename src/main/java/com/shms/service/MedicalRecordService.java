package com.shms.service;

import com.shms.entity.*;
import com.shms.repository.MedicalRecordRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDate;
import java.util.List;

@Service
public class MedicalRecordService {
    private final MedicalRecordRepository records;
    public MedicalRecordService(MedicalRecordRepository records){this.records=records;}
    public List<MedicalRecord> findForPatient(Long patientId){return records.findByPatientId(patientId);}
    @Transactional
    public MedicalRecord create(Patient patient, Doctor doctor, Appointment appointment, String complaint,
                                String symptoms, String notes, String diagnosis, String treatment, LocalDate followUp){
        MedicalRecord record=new MedicalRecord(); record.setPatient(patient); record.setDoctor(doctor); record.setAppointment(appointment);
        record.setChiefComplaint(complaint); record.setSymptoms(symptoms); record.setClinicalNotes(notes);
        record.setDiagnosisSummary(diagnosis); record.setTreatmentPlan(treatment); record.setFollowUpDate(followUp);
        return records.save(record);
    }
}
