package BankSdNd.example.BsDnD.core.application;

import BankSdNd.example.BsDnD.core.domain.exception.*;
import BankSdNd.example.BsDnD.core.domain.model.BankUser;
import BankSdNd.example.BsDnD.core.port.in.dto.CreatePersonCommand;
import BankSdNd.example.BsDnD.core.port.out.BankUserRepositoryPort;
import BankSdNd.example.BsDnD.core.port.out.PasswordEncoderPort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class PersonServiceTest {

    private PersonService personService;
    private BankUserRepositoryPort personRepository;
    private PasswordEncoderPort passwordEncoder;

    @BeforeEach
    void setUp() {
        personRepository = mock(BankUserRepositoryPort.class);
        passwordEncoder = mock(PasswordEncoderPort.class);
        personService = new PersonService(personRepository, passwordEncoder);
    }

    @Test
    @DisplayName("Should save a person when command is valid")
    void savePerson_ShouldSaveWhenValid() {
        CreatePersonCommand command = new CreatePersonCommand("John", "Doe", "11144477735", "11999999999", new BigDecimal("5000"), "123456", "1234");
        when(personRepository.existsByCpf(any())).thenReturn(false);
        when(personRepository.existsByPhoneNumber(any())).thenReturn(false);
        when(passwordEncoder.encode(any())).thenReturn("encoded");
        when(personRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        BankUser saved = personService.savePerson(command);

        assertNotNull(saved);
        assertEquals("John", saved.getName());
        verify(personRepository).save(any());
    }

    @Test
    @DisplayName("Should throw ValidationException when CPF is invalid")
    void savePerson_ShouldThrowValidationExceptionWhenCpfInvalid() {
        CreatePersonCommand command = new CreatePersonCommand("John", "Doe", "123", "11999999999", new BigDecimal("5000"), "123456", "1234");
        
        ValidationException exception = assertThrows(ValidationException.class, () -> personService.savePerson(command));
        assertEquals("error.cpf_invalid", exception.getMessageKey());
    }

    @Test
    @DisplayName("Should throw DuplicateException when CPF already exists")
    void savePerson_ShouldThrowDuplicateExceptionWhenCpfExists() {
        CreatePersonCommand command = new CreatePersonCommand("John", "Doe", "11144477735", "11999999999", new BigDecimal("5000"), "123456", "1234");
        when(personRepository.existsByCpf(any())).thenReturn(true);

        DuplicateException exception = assertThrows(DuplicateException.class, () -> personService.savePerson(command));
        assertEquals("error.cpf_exists", exception.getMessageKey());
    }

    @Test
    @DisplayName("Should throw ValidationException when phone number is invalid")
    void savePerson_ShouldThrowValidationExceptionWhenPhoneInvalid() {
        CreatePersonCommand command = new CreatePersonCommand("John", "Doe", "11144477735", "11888888888", new BigDecimal("5000"), "123456", "1234");
        when(personRepository.existsByCpf(any())).thenReturn(false);

        ValidationException exception = assertThrows(ValidationException.class, () -> personService.savePerson(command));
        assertEquals("error.phone_invalid_format", exception.getMessageKey());
    }

    @Test
    @DisplayName("Should update phone number when valid")
    void updatePhoneNumber_ShouldUpdateWhenValid() {
        when(personRepository.existsByPhoneNumber(any())).thenReturn(false);
        BankUser user = BankUser.builder().build();
        when(personRepository.findById(1L)).thenReturn(Optional.of(user));
        when(personRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        BankUser updated = personService.updatePhoneNumber(1L, "11988888888");

        assertEquals("11988888888", updated.getPhoneNumber());
    }

    @Test
    @DisplayName("Should throw InvalidInputException when name is empty")
    void updateName_ShouldThrowInvalidInputExceptionWhenEmpty() {
        InvalidInputException exception1 = assertThrows(InvalidInputException.class, () -> personService.updateName(1L, "", "Doe"));
        assertEquals("error.name_required", exception1.getMessageKey());

        InvalidInputException exception2 = assertThrows(InvalidInputException.class, () -> personService.updateName(1L, "John", " "));
        assertEquals("error.name_required", exception2.getMessageKey());
    }
}
