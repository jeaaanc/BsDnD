package BankSdNd.example.BsDnD.service;

import BankSdNd.example.BsDnD.domain.BankUser;
import BankSdNd.example.BsDnD.dto.PersonDto;
import BankSdNd.example.BsDnD.exception.business.DuplicateException;
import BankSdNd.example.BsDnD.exception.business.UserNotFoundException;
import BankSdNd.example.BsDnD.repository.BankUserRepository;
import BankSdNd.example.BsDnD.util.validation.CpfValidator;
import BankSdNd.example.BsDnD.util.validation.PhoneValidator;
import ch.qos.logback.core.util.StringUtil;

import org.springframework.stereotype.Service;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

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

    @Transactional
    public BankUser changePhoneNumber(Long userId, String newPhoneNumber) {
        // v exception
        if (!PhoneValidator.isValidPhoneNumber(newPhoneNumber)){
            throw new IllegalArgumentException("Novo número de telefone inválido.");
        }

        if (personRepository.existsByPhoneNumber(newPhoneNumber)){
            throw new DuplicateException("Número de telefone ", newPhoneNumber + " em uso.");
        }

        BankUser user = personRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("Usuário não encontrado. "));

        user.setPhoneNumber(newPhoneNumber);
        return personRepository.save(user);
    }

    @Transactional
    public BankUser changeName(Long userId, String newFirstName, String newLastName){

        if (!StringUtils.hasText(newFirstName) || !StringUtils.hasText(newLastName)) {
            throw new IllegalArgumentException("Nome e sobrenome não podem ser vazios.");
        }
        BankUser user = personRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("Usuário não encontrado."));

        user.setName(newFirstName);
        user.setLastName(newLastName);

        return personRepository.save(user);
    }
}