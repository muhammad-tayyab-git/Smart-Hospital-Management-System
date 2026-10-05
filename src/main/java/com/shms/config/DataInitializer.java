package com.shms.config;

import com.shms.entity.Role;
import com.shms.entity.User;
import com.shms.repository.RoleRepository;
import com.shms.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;
import java.util.HashSet;
import java.util.Map;

@Configuration
public class DataInitializer {
 @Bean
 CommandLineRunner normalizeDemoAccounts(UserRepository users, RoleRepository roles, PasswordEncoder encoder){
  return args -> {
   Map<String,String> demoRoles=Map.of(
     "admin@smarthospital.local","ADMIN",
     "doctor.sarah@smarthospital.local","DOCTOR",
     "doctor.david@smarthospital.local","DOCTOR",
     "reception@smarthospital.local","RECEPTIONIST",
     "nurse@smarthospital.local","NURSE",
     "lab@smarthospital.local","LAB_TECHNICIAN",
     "patient.john@example.com","PATIENT",
     "patient.maria@example.com","PATIENT",
     "patient.ali@example.com","PATIENT"
   );
   demoRoles.forEach((email,roleName)->users.findByEmailIgnoreCase(email).ifPresent(u->{
     Role role=roles.findByName(roleName).orElse(null); if(role==null)return;
     if(u.getRoles()==null)u.setRoles(new HashSet<>()); u.getRoles().clear(); u.getRoles().add(role);
     if(!encoder.matches("Password123!",u.getPassword()))u.setPassword(encoder.encode("Password123!"));
     users.save(u);
   }));
  };
 }
}
