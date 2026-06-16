package BankSdNd.example.BsDnD.core.application;

import BankSdNd.example.BsDnD.core.domain.model.BankUser;
import BankSdNd.example.BsDnD.core.domain.exception.*;
import BankSdNd.example.BsDnD.core.port.in.CreatePersonUseCase;
import BankSdNd.example.BsDnD.core.port.in.GetPersonUseCase;
import BankSdNd.example.BsDnD.core.port.in.ManagePersonUseCase;
import BankSdNd.example.BsDnD.core.port.in.dto.CreatePersonCommand;
import BankSdNd.example.BsDnD.core.port.out.BankUserRepositoryPort;
import BankSdNd.example.BsDnD.core.port.out.PasswordEncoderPort;
import BankSdNd.example.BsDnD.core.domain.validation.CpfValidator;
import BankSdNd.example.BsDnD.core.domain.validation.PhoneValidator;

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
            throw new ValidationException("error.cpf_invalid");
        }
        if (personRepository.existsByCpf(command.cpf())) {
            throw new DuplicateException("error.cpf_exists", command.cpf());
        }

        if (personRepository.existsByPhoneNumber(command.phoneNumber())) {
            throw new DuplicateException("error.phone_exists", command.phoneNumber());
        }
        if (!PhoneValidator.isValidPhoneNumber(command.phoneNumber())) {
            throw new ValidationException("error.phone_invalid_format");
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
            throw new ValidationException("error.phone_invalid_format");
        }

        if (personRepository.existsByPhoneNumber(newPhoneNumber)) {
            throw new DuplicateException("error.phone_exists", newPhoneNumber);
        }

        BankUser user = personRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("error.user_not_found"));

        user.setPhoneNumber(newPhoneNumber);
        return personRepository.save(user);
    }

    public BankUser updateName(Long userId, String newFirstName, String newLastName) {
        if (newFirstName == null || newFirstName.trim().isEmpty() || newLastName == null || newLastName.trim().isEmpty()) {
            throw new InvalidInputException("error.name_required");
        }
        BankUser user = personRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("error.user_not_found"));

        user.setName(newFirstName);
        user.setLastName(newLastName);

        return personRepository.save(user);
    }

    public List<BankUser> findAll() {
        return personRepository.findAll();
    }
}
