package BankSdNd.example.BsDnD.service;

import BankSdNd.example.BsDnD.domain.BankUser;
import BankSdNd.example.BsDnD.dto.PersonDto;
import BankSdNd.example.BsDnD.exception.business.DuplicateException;
import BankSdNd.example.BsDnD.exception.business.UserNotFoundException;
import BankSdNd.example.BsDnD.repository.BankUserRepository;
import BankSdNd.example.BsDnD.util.validation.CpfValidator;
import BankSdNd.example.BsDnD.util.validation.PhoneValidator;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;



/**
 * Service class responsible for managing the personal data of {@link BankUser} entities.
 *
 * This service handles the creation of new users and the updating of their
 * personal information, such as name and phone number, performing all necessary validations.
 */
@Service
public class PersonService {

    private BankUserRepository personRepository;

    public PersonService(BankUserRepository personRepository) {
        this.personRepository = personRepository;
    }


    /**
     * Creates and persists a new {@code BankUser} after validating the provided data.
     * It expects the password in the DTO to be already encoded.
     *
     * @param dto The {@code PersonDto} containing the new user's information.
     * @return The newly created and persisted {@code BankUser} entity.
     * @throws IllegalArgumentException if the CPF or phone number format is invalid.
     * @throws DuplicateException if the CPF or phone number already exists in the database.
     */
    public BankUser savePerson(PersonDto dto) {

        if (!CpfValidator.isValid(dto.getCpf())) {
            throw new IllegalArgumentException("Invalid CPF");

        }
        if (personRepository.existsByCpf(dto.getCpf())) {
            throw new DuplicateException("CPF", dto.getCpf());
        }

        if (personRepository.existsByPhoneNumber(dto.getPhoneNumber())) {
            throw new DuplicateException("Phone number", dto.getPhoneNumber());
        }
        if (!PhoneValidator.isValidPhoneNumber(dto.getPhoneNumber())) {
            throw new IllegalArgumentException("Invalid phone number. Use area code DDD + number");

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

    /**
     * Updates an existing user's phone number after performing validations.
     * This operation is transactional.
     *
     * @param userId The ID of the user to update.
     * @param newPhoneNumber The new phone number to set.
     * @return The updated {@code BankUser} entity.
     * @throws UserNotFoundException if no user is found with the given ID.
     * @throws IllegalArgumentException if the new phone number has an invalid format.
     * @throws DuplicateException if the new phone number is already in use by another user.
     */
    @Transactional
    public BankUser changePhoneNumber(Long userId, String newPhoneNumber) {
        // v exception
        if (!PhoneValidator.isValidPhoneNumber(newPhoneNumber)){
            throw new IllegalArgumentException("New phone number is invalid.");
        }

        if (personRepository.existsByPhoneNumber(newPhoneNumber)){
            throw new DuplicateException("Phone number ", newPhoneNumber + " is already in use.");
        }

        BankUser user = personRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found."));

        user.setPhoneNumber(newPhoneNumber);
        return personRepository.save(user);
    }

    /**
     * Updates an existing user's first and last name.
     * This operation is transactional.
     *
     * @param userId The ID of the user to update.
     * @param newFirstName The new first name.
     * @param newLastName The new last name.
     * @return The updated {@code BankUser} entity.
     * @throws UserNotFoundException if no user is found with the given ID.
     * @throws IllegalArgumentException if the new first name or last name are null or empty.
     */
    @Transactional
    public BankUser changeName(Long userId, String newFirstName, String newLastName){

        if (!StringUtils.hasText(newFirstName) || !StringUtils.hasText(newLastName)) {
            throw new IllegalArgumentException("First name and last name cannot be empty.");
        }
        BankUser user = personRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found."));

        user.setName(newFirstName);
        user.setLastName(newLastName);

        return personRepository.save(user);
    }
}