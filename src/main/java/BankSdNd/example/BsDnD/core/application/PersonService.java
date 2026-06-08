package BankSdNd.example.BsDnD.core.application;

import BankSdNd.example.BsDnD.core.domain.model.BankUser;
import BankSdNd.example.BsDnD.core.domain.exception.DuplicateException;
import BankSdNd.example.BsDnD.core.domain.exception.UserNotFoundException;
import BankSdNd.example.BsDnD.core.port.in.CreatePersonUseCase;
import BankSdNd.example.BsDnD.core.port.in.GetPersonUseCase;
import BankSdNd.example.BsDnD.core.port.in.ManagePersonUseCase;
import BankSdNd.example.BsDnD.core.port.in.dto.CreatePersonCommand;
import BankSdNd.example.BsDnD.core.port.out.BankUserRepositoryPort;
import BankSdNd.example.BsDnD.core.port.out.PasswordEncoderPort;
import BankSdNd.example.BsDnD.core.domain.validation.CpfValidator;
import BankSdNd.example.BsDnD.core.domain.validation.PhoneValidator;
import org.springframework.util.StringUtils;

import java.util.List;

public class PersonService implements CreatePersonUseCase, ManagePersonUseCase, GetPersonUseCase {

    private BankUserRepositoryPort personRepository;
    private final PasswordEncoderPort passwordEncoder;

    public PersonService(BankUserRepositoryPort personRepository, PasswordEncoderPort passwordEncoder) {
        this.personRepository = personRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public BankUser savePerson(CreatePersonCommand command) {
        if (!CpfValidator.isValid(command.cpf())) {
            throw new IllegalArgumentException("Invalid CPF");
        }
        if (personRepository.existsByCpf(command.cpf())) {
            throw new DuplicateException("CPF already exists: " + command.cpf());
        }

        if (personRepository.existsByPhoneNumber(command.phoneNumber())) {
            throw new DuplicateException("Phone number already exists: " + command.phoneNumber());
        }
        if (!PhoneValidator.isValidPhoneNumber(command.phoneNumber())) {
            throw new IllegalArgumentException("Invalid phone number. Use area code DDD + number");
        }

        String encryptedPassword = passwordEncoder.encode(command.password());
        String encryptedTransactionPassword = passwordEncoder.encode(command.transactionPassword());

        BankUser person = BankUser.builder()
                .name(command.name())
                .lastName(command.lastName())
                .cpf(command.cpf())
                .phoneNumber(command.phoneNumber())
                .income(command.income())
                .password(encryptedPassword)
                .transactionPassword(encryptedTransactionPassword)
                .build();

        return personRepository.save(person);
    }

    public BankUser updatePhoneNumber(Long userId, String newPhoneNumber) {
        if (!PhoneValidator.isValidPhoneNumber(newPhoneNumber)) {
            throw new IllegalArgumentException("New phone number is invalid.");
        }

        if (personRepository.existsByPhoneNumber(newPhoneNumber)) {
            throw new DuplicateException("Phone number " + newPhoneNumber + " is already in use.");
        }

        BankUser user = personRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found."));

        user.setPhoneNumber(newPhoneNumber);
        return personRepository.save(user);
    }

    public BankUser updateName(Long userId, String newFirstName, String newLastName) {
        if (!StringUtils.hasText(newFirstName) || !StringUtils.hasText(newLastName)) {
            throw new IllegalArgumentException("First name and last name cannot be empty.");
        }
        BankUser user = personRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found."));

        user.setName(newFirstName);
        user.setLastName(newLastName);

        return personRepository.save(user);
    }

    public List<BankUser> findAll() {
        return personRepository.findAll();
    }
}
