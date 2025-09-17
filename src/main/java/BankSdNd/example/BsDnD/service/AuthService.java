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

    public BankUser login(LoginDto dto) {
        try {

            if (dto == null || dto.getCpf() == null || dto.getCpf().trim().isEmpty()) {
                throw new IllegalArgumentException("CPF é obrigatório.");// excptio!!!
            }

            if (dto.getRawPassword() == null || dto.getRawPassword().length == 0) {
                throw new IllegalArgumentException("Senha é obrigatória.");//Exception
            }

            BankUser user = userRepository.findByCpf(dto.getCpf())
                    .orElseThrow(() -> new UserNotFoundException("Usuário ou Senha incorreta"));

            boolean matches = passwordEncoder.matches(new String(dto.getRawPassword()), user.getPassword());


            if (!matches) {
                throw new InvalidPasswordException("Usuário ou Senha incorreta");
            }
            return user;

        } finally {

            if (dto != null){
                dto.clearPassword();
            }
        }
    }

    public void validatePassword(Long userId, char[] rawPassaword) {

        if (rawPassaword == null || rawPassaword.length == 0){
            throw new InvalidPasswordException("A senha de confirmação não pode ser vazia.");
        }

        BankUser user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("Usuário não encontrado para verificação."));

        if (!passwordEncoder.matches(new String(rawPassaword), user.getPassword())) {
            throw new InvalidPasswordException("Senha incorreta! Operação cancelada.");
        }
    }

}
