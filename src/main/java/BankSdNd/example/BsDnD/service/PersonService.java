package BankSdNd.example.BsDnD.service;

import BankSdNd.example.BsDnD.domain.BankUser;
import BankSdNd.example.BsDnD.dto.PersonDto;
import BankSdNd.example.BsDnD.exception.business.DuplicateException;
import BankSdNd.example.BsDnD.exception.business.InvalidPasswordException;
import BankSdNd.example.BsDnD.repository.BankUserRepository;
import BankSdNd.example.BsDnD.util.CpfValidator;
import BankSdNd.example.BsDnD.util.PhoneValidator;
import org.springframework.stereotype.Service;

import org.springframework.security.crypto.password.PasswordEncoder;
@Service
public class PersonService {
    private BankUserRepository personRepository;

    private final PasswordValidationService passwordValidationService;
    private final PasswordEncoder passwordEncoder;

    public PersonService(BankUserRepository personRepository, PasswordValidationService passwordValidationService,
                         PasswordEncoder passwordEncoder) {
        this.personRepository = personRepository;
        this.passwordValidationService = passwordValidationService;
        this.passwordEncoder = passwordEncoder;
    }

    public BankUser savePerson (PersonDto dto) {

        if (!CpfValidator.isValid(dto.getCpf())){
            throw new IllegalArgumentException("CPF inválido");
        }
        if (personRepository.existsByCpf(dto.getCpf())){
            throw new DuplicateException("CPF", dto.getCpf());
        }
        // Decidir se usar Unique key no Banco
        if (personRepository.existsByPhoneNumber(dto.getPhoneNumber())){
            throw new DuplicateException("Número de telefone", dto.getPhoneNumber());
        }
        if (!PhoneValidator.isValidPhoneNumber(dto.getPhoneNumber())){
            throw new IllegalArgumentException("Número de telefone inválido. Use DDD + número");
        }

        if (!dto.getRawpassword().equals(dto.getConfirmedrawPassword())){
            throw new InvalidPasswordException("As senhas não coincidem");
        }

        String encodedPassword = passwordEncoder.encode(dto.getRawpassword());

        BankUser person = new BankUser.Builder()
                .name(dto.getName())
                .lastName(dto.getLastName())
                .cpf(dto.getCpf())
                .phoneNumber(dto.getPhoneNumber())
                .income(dto.getIncome())
                .passWord(encodedPassword)
                .build();

        return personRepository.save(person);
    }

    //!!!!! suspeito
    public boolean checkUserPassword(Long userId, String rawPassword){
        BankUser user =personRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));
        return passwordValidationService.validatePassword(rawPassword, user.getPassword());
    }
}
