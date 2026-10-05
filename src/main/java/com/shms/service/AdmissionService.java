package com.shms.service;

import com.shms.entity.*;
import com.shms.repository.AdmissionRepository;
import com.shms.repository.BedRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
public class AdmissionService {
    private final AdmissionRepository admissions; private final BedRepository beds;
    public AdmissionService(AdmissionRepository admissions,BedRepository beds){this.admissions=admissions;this.beds=beds;}
    public List<Admission> findForPatient(Long patientId){return admissions.findByPatientId(patientId);}
    @Transactional
    public Admission admit(Patient patient,Doctor doctor,Bed bed,String reason,String diagnosis,String notes){
        if(bed.getStatus()!=Bed.Status.AVAILABLE) throw new IllegalStateException("Bed is not available");
        Admission a=new Admission();a.setAdmissionNumber("ADM-"+System.currentTimeMillis());a.setPatient(patient);a.setDoctor(doctor);a.setBed(bed);a.setReason(reason);a.setDiagnosis(diagnosis);a.setNotes(notes);
        bed.setStatus(Bed.Status.OCCUPIED);beds.save(bed);return admissions.save(a);
    }
    @Transactional
    public Admission discharge(Admission admission){admission.setStatus(Admission.Status.DISCHARGED);admission.setDischargeDate(java.time.LocalDateTime.now());Bed bed=admission.getBed();if(bed!=null){bed.setStatus(Bed.Status.AVAILABLE);beds.save(bed);}return admissions.save(admission);}
}
