package com.shms.service;

import com.shms.entity.*;
import com.shms.repository.LabOrderRepository;
import com.shms.repository.LabResultRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
public class LaboratoryService {
    private final LabOrderRepository orders; private final LabResultRepository results;
    public LaboratoryService(LabOrderRepository orders, LabResultRepository results){this.orders=orders;this.results=results;}
    public List<LabOrder> findOrdersForPatient(Long patientId){return orders.findByPatientId(patientId);}
    @Transactional public LabOrder createOrder(Patient patient, Doctor doctor, Appointment appointment, String testName, LabOrder.Priority priority){
        LabOrder o=new LabOrder();o.setPatient(patient);o.setDoctor(doctor);o.setAppointment(appointment);o.setTestName(testName);o.setPriority(priority==null?LabOrder.Priority.NORMAL:priority);return orders.save(o);
    }
    @Transactional public LabResult recordResult(LabOrder order,String value,String unit,String reference,String text,User verifier){
        LabResult r=new LabResult();r.setLabOrder(order);r.setResultValue(value);r.setUnit(unit);r.setReferenceRange(reference);r.setResultText(text);r.setVerifiedBy(verifier);if(verifier!=null)r.setVerifiedAt(java.time.LocalDateTime.now());
        order.setStatus(LabOrder.Status.COMPLETED);orders.save(order);return results.save(r);
    }
}
