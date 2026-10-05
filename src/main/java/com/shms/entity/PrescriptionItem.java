package com.shms.entity;
import jakarta.persistence.*;
@Entity @Table(name="prescription_items") public class PrescriptionItem {
 @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id; @ManyToOne(fetch=FetchType.LAZY) @JoinColumn(name="prescription_id",nullable=false) private Prescription prescription;
 @Column(name="medicine_name",nullable=false) private String medicineName; private String dosage; private String frequency; private String duration; private String route; @Column(columnDefinition="TEXT") private String instructions;
 public Long getId(){return id;} public void setId(Long v){id=v;} public Prescription getPrescription(){return prescription;} public void setPrescription(Prescription v){prescription=v;} public String getMedicineName(){return medicineName;} public void setMedicineName(String v){medicineName=v;} public String getDosage(){return dosage;} public void setDosage(String v){dosage=v;} public String getFrequency(){return frequency;} public void setFrequency(String v){frequency=v;} public String getDuration(){return duration;} public void setDuration(String v){duration=v;} public String getRoute(){return route;} public void setRoute(String v){route=v;} public String getInstructions(){return instructions;} public void setInstructions(String v){instructions=v;}
}
