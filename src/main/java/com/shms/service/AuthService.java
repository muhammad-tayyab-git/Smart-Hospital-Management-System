package com.shms.service;

import com.shms.entity.Patient;
import com.shms.entity.Role;
import com.shms.entity.User;
import com.shms.repository.PatientRepository;
import com.shms.repository.RoleRepository;
import com.shms.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.Optional;

@Service
public class AuthService {
 private final UserRepository users; private final PatientRepository patients; private final RoleRepository roles; private final PasswordEncoder encoder;
 public AuthService(UserRepository users,PatientRepository patients,RoleRepository roles,PasswordEncoder encoder){this.users=users;this.patients=patients;this.roles=roles;this.encoder=encoder;}
 public Optional<User> authenticate(String email,String password){
   return users.findByEmailIgnoreCase(email).filter(u -> u.getStatus()==User.Status.ACTIVE && password!=null && u.getPassword()!=null && encoder.matches(password,u.getPassword()));
 }
 @Transactional
 public boolean registerPatient(User user, Patient patient){
   if(user==null || user.getEmail()==null || user.getEmail().isBlank() || user.getPassword()==null || user.getFirstName()==null || user.getLastName()==null) return false;
   if(users.existsByEmailIgnoreCase(user.getEmail())) return false;
   Role role=roles.findByName("PATIENT").orElseThrow(() -> new IllegalStateException("PATIENT role is missing"));
   user.setRoles(new java.util.HashSet<>()); user.getRoles().add(role); user.setStatus(User.Status.ACTIVE); user.setPassword(encoder.encode(user.getPassword()));
   User saved=users.save(user);
   patient.setUser(saved);
   if(patient.getPatientNumber()==null || patient.getPatientNumber().isBlank()) patient.setPatientNumber("PAT-"+String.format("%06d",saved.getId()));
   patients.save(patient);
   return true;
 }
}
