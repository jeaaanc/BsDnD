package BankSdNd.example.BsDnD.service;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class PasswordValidationService {

    private final PasswordEncoder PasswordEncoder;

    public PasswordValidationService(PasswordEncoder passwordEncoder) {
        this.PasswordEncoder = passwordEncoder;
    }

    public boolean validatePassword(String rawPassword, String encodedPassword){
        return  PasswordEncoder.matches(rawPassword, encodedPassword);
    }
}
