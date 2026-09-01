package BankSdNd.example.BsDnD.core.application;

import BankSdNd.example.BsDnD.core.domain.exception.DuplicateException;
import BankSdNd.example.BsDnD.core.domain.exception.InvalidInputException;
import BankSdNd.example.BsDnD.core.domain.exception.ValidationException;
import BankSdNd.example.BsDnD.core.domain.model.BankUser;
import BankSdNd.example.BsDnD.core.port.in.dto.CreatePersonCommand;
import BankSdNd.example.BsDnD.core.port.out.BankUserRepositoryPort;
import BankSdNd.example.BsDnD.core.port.out.PasswordEncoderPort;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PersonServiceTest {

    @Mock
    private BankUserRepositoryPort personRepository;
    @Mock
    private PasswordEncoderPort passwordEncoder;

    @InjectMocks
    private PersonService personService;

    private final String VALID_NAME = "John";
    private final String VALID_LAST_NAME = "Doe";
    private final String VALID_CPF = "11144477735";
    private final String INVALID_CPF = "123";
    private final String VALID_PHONE = "11999999999";
    private final String INVALID_PHONE_FORMAT = "11888888888";
    private final BigDecimal VALID_INCOME = new BigDecimal("5000");
    private final String VALID_PASSWORD = "123456";
    private final String VALID_TX_PASSWORD = "1234";
    private final String ENCODED_PASSWORD = "encoded";
    private final Long DEFAULT_USER_ID = 1L;


    @Test
    @DisplayName("Should save a person when command is valid")
    void savePerson_ShouldSaveWhenValid() {

        CreatePersonCommand command = buildValidCommand();
        when(personRepository.existsByCpf(any())).thenReturn(false);
        when(personRepository.existsByPhoneNumber(any())).thenReturn(false);
        when(passwordEncoder.encode(any())).thenReturn(ENCODED_PASSWORD);
        when(personRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        BankUser saved = personService.savePerson(command);

        assertNotNull(saved);
        assertEquals(VALID_NAME, saved.getName());
        verify(personRepository).save(any());
    }

    @Test
    @DisplayName("Should throw ValidationException when CPF is invalid")
    void savePerson_ShouldThrowValidationExceptionWhenCpfInvalid() {

        CreatePersonCommand command = new CreatePersonCommand(
                VALID_NAME, VALID_LAST_NAME, INVALID_CPF, VALID_PHONE, VALID_INCOME, VALID_PASSWORD, VALID_TX_PASSWORD
        );

        ValidationException exception = assertThrows(ValidationException.class, () ->
                personService.savePerson(command));

        assertEquals("error.cpf_invalid", exception.getMessage());
    }

    @Test
    @DisplayName("Should throw DuplicateException when CPF already exists")
    void savePerson_ShouldThrowDuplicateExceptionWhenCpfExists() {

        CreatePersonCommand command = buildValidCommand();
        when(personRepository.existsByCpf(any())).thenReturn(true);

        DuplicateException exception = assertThrows(DuplicateException.class, () ->
                personService.savePerson(command));

        assertEquals("error.cpf_exists", exception.getMessage());
    }

    @Test
    @DisplayName("Should throw ValidationException when phone number is invalid")
    void savePerson_ShouldThrowValidationExceptionWhenPhoneInvalid() {

        CreatePersonCommand command = new CreatePersonCommand(
                VALID_NAME, VALID_LAST_NAME, VALID_CPF, INVALID_PHONE_FORMAT, VALID_INCOME, VALID_PASSWORD, VALID_TX_PASSWORD
        );
        when(personRepository.existsByCpf(any())).thenReturn(false);

        ValidationException exception = assertThrows(ValidationException.class, () ->
                personService.savePerson(command));

        assertEquals("error.phone_invalid_format", exception.getMessage());
    }

    @Test
    @DisplayName("Should update phone number when valid")
    void updatePhoneNumber_ShouldUpdateWhenValid() {

        BankUser user = BankUser.builder().build();
        when(personRepository.existsByPhoneNumber(any())).thenReturn(false);
        when(personRepository.findById(DEFAULT_USER_ID)).thenReturn(Optional.of(user));
        when(personRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        BankUser updated = personService.updatePhoneNumber(DEFAULT_USER_ID, VALID_PHONE);

        assertEquals(VALID_PHONE, updated.getPhoneNumber());
    }

    @Test
    @DisplayName("Should throw InvalidInputException when name is empty")
    void updateName_ShouldThrowInvalidInputExceptionWhenEmpty() {

        InvalidInputException exception1 = assertThrows(InvalidInputException.class, () ->
                personService.updateName(DEFAULT_USER_ID, "", VALID_LAST_NAME)
        );
        assertEquals("error.name_required", exception1.getMessage());

        InvalidInputException exception2 = assertThrows(InvalidInputException.class, () ->
                personService.updateName(DEFAULT_USER_ID, VALID_NAME, " ")
        );

        assertEquals("error.name_required", exception2.getMessage());
    }


    private CreatePersonCommand buildValidCommand() {
        return new CreatePersonCommand(
                VALID_NAME, VALID_LAST_NAME, VALID_CPF, VALID_PHONE, VALID_INCOME,VALID_PASSWORD, VALID_TX_PASSWORD
        );
    }
}
