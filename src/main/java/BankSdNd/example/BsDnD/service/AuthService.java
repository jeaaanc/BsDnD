package BankSdNd.example.BsDnD.service;

import BankSdNd.example.BsDnD.domain.Account;
import BankSdNd.example.BsDnD.domain.BankUser;
import BankSdNd.example.BsDnD.dto.LoginDto;
import BankSdNd.example.BsDnD.exception.business.InvalidPasswordException;
import BankSdNd.example.BsDnD.exception.business.UserNotFoundException;
import BankSdNd.example.BsDnD.repository.BankUserRepository;
import BankSdNd.example.BsDnD.util.InputUtils;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Scanner;

@Service
public class AuthService {

    private final BankUserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public AuthService(BankUserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public BankUser login(LoginDto dto){
        BankUser user = userRepository.findByCpf(dto.getCpf())
                .orElseThrow(() -> new UserNotFoundException(" Usuário com CPF: " + dto.getCpf() + " não encontrado"));

        if (!passwordEncoder.matches(dto.getRawPassword(), user.getPassword())){
            throw new InvalidPasswordException(" Senha incorreta");
        }
        return user;
        }

    public boolean verifyTransactionPassword(Long userId, String rawPassword){
        BankUser user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(" Usuário não encontrado"));

        return passwordEncoder.matches(rawPassword, user.getPassword());
    }

    public void confirmTransactionPassword(Long userId, String rawPassword){

        boolean valid = verifyTransactionPassword(userId, rawPassword);
        if (!valid){
            throw new InvalidPasswordException("Senha incorreta! Tranferência cancelada.");
        }
    }

}
