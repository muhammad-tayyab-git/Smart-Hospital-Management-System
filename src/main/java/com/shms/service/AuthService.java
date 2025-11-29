
package com.shms.service;
import com.shms.entity.Patient;
import com.shms.entity.User;
import com.shms.repository.PatientRepository;
import com.shms.repository.UserRepository;
import org.springframework.stereotype.Service;
import java.util.Optional;
@Service
public class AuthService {
    private final UserRepository userRepository;
    private final PatientRepository patientRepository;
    public AuthService(UserRepository userRepository, PatientRepository patientRepository) {
        this.userRepository = userRepository;
        this.patientRepository = patientRepository;
    }

    public Optional<User> authenticate(String username, String password) {
        return userRepository.findByUsername(username)
                .filter(u -> u.getPassword().equals(password));
    }

    public boolean registerPatient(User user, Patient patient) {
        if (userRepository.existsByUsername(user.getUsername()) || userRepository.existsByEmail(user.getEmail())) {
            return false;
        }

        user.setRole("PATIENT");
        userRepository.save(user);

        patient.setUser(user);
        patientRepository.save(patient);

        return true;
    }
}
