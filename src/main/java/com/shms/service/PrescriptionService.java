package com.shms.service;

import com.shms.entity.*;
import com.shms.repository.PrescriptionRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
public class PrescriptionService {
    private final PrescriptionRepository prescriptions;
    public PrescriptionService(PrescriptionRepository prescriptions){this.prescriptions=prescriptions;}
    public List<Prescription> findForPatient(Long patientId){return prescriptions.findByPatientId(patientId);}
    @Transactional
    public Prescription create(MedicalRecord record, Doctor doctor, Patient patient, String instructions, List<PrescriptionItem> items){
        Prescription p=new Prescription(); p.setMedicalRecord(record); p.setDoctor(doctor); p.setPatient(patient); p.setInstructions(instructions);
        if(items!=null){items.forEach(i->i.setPrescription(p));p.getItems().addAll(items);}
        return prescriptions.save(p);
    }
}
