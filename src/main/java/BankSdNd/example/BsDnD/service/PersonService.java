package BankSdNd.example.BsDnD.service;

import BankSdNd.example.BsDnD.domain.BankUser;
import BankSdNd.example.BsDnD.dto.PersonDto;
import BankSdNd.example.BsDnD.exception.business.DuplicateException;
import BankSdNd.example.BsDnD.repository.BankUserRepository;
import BankSdNd.example.BsDnD.util.validation.CpfValidator;
import BankSdNd.example.BsDnD.util.validation.PhoneValidator;
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

    public BankUser savePerson(PersonDto dto) {

        if (!CpfValidator.isValid(dto.getCpf())) {
            throw new IllegalArgumentException("CPF inválido");

        }
        if (personRepository.existsByCpf(dto.getCpf())) {
            throw new DuplicateException("CPF", dto.getCpf());
        }

        if (personRepository.existsByPhoneNumber(dto.getPhoneNumber())) {
            throw new DuplicateException("Número de telefone", dto.getPhoneNumber());
        }
        if (!PhoneValidator.isValidPhoneNumber(dto.getPhoneNumber())) {
            throw new IllegalArgumentException("Número de telefone inválido. Use DDD + número");

        }

        BankUser person = new BankUser.Builder()
                .name(dto.getName())
                .lastName(dto.getLastName())
                .cpf(dto.getCpf())
                .phoneNumber(dto.getPhoneNumber())
                .income(dto.getIncome())
                .passWord(dto.getEncodedPassword())
                .build();

        return personRepository.save(person);
    }
}