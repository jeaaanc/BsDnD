package BankSdNd.example.BsDnD.adapter.in.web;

import BankSdNd.example.BsDnD.adapter.in.web.dto.PersonRequest;
import BankSdNd.example.BsDnD.adapter.in.web.dto.UserUpdateDtos;
import BankSdNd.example.BsDnD.adapter.in.web.handler.GlobalExceptionHandler;
import BankSdNd.example.BsDnD.core.domain.exception.DuplicateException;
import BankSdNd.example.BsDnD.core.domain.model.BankUser;
import BankSdNd.example.BsDnD.core.port.in.CreatePersonUseCase;
import BankSdNd.example.BsDnD.core.port.in.ManageCredentialsUseCase;
import BankSdNd.example.BsDnD.core.port.in.ManagePersonUseCase;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.math.BigDecimal;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class PersonRestControllerTest {

    private MockMvc mockMvc;

    @Mock
    private CreatePersonUseCase createPersonUseCase;

    @Mock
    private ManagePersonUseCase managePersonUseCase;

    @Mock
    private ManageCredentialsUseCase manageCredentialsUseCase;

    @InjectMocks
    private PersonRestController personRestController;

    private final ObjectMapper objectMapper = new ObjectMapper();

    private final String BASE_URL = "/api/users";
    private final Long USER_ID = 1L;
    private final String USER_URL = BASE_URL + "/" + USER_ID;

    private final String DEFAULT_NAME = "John";
    private final String DEFAULT_LAST_NAME = "Wick";
    private final String DEFAULT_CPF = "12345678900";
    private final String DEFAULT_PHONE = "11999999999";
    private final BigDecimal DEFAULT_INCOME = new BigDecimal("5000");
    private final String DEFAULT_PASSWORD = "123456";
    private final String DEFAULT_TX_PASSWORD = "1234";

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(personRestController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    @Test
    @DisplayName("Should return 201 Created when saving a valid person")
    void shouldCreatePersonSuccessfully() throws Exception {

        PersonRequest request = new PersonRequest(DEFAULT_NAME, DEFAULT_LAST_NAME, DEFAULT_CPF,
                DEFAULT_PHONE, DEFAULT_INCOME, DEFAULT_PASSWORD, DEFAULT_TX_PASSWORD
        );

        BankUser savedUser = BankUser.builder()
                .id(USER_ID)
                .name(DEFAULT_NAME)
                .lastName(DEFAULT_LAST_NAME)
                .cpf(DEFAULT_CPF)
                .phoneNumber(DEFAULT_PHONE)
                .build();
        when(createPersonUseCase.savePerson(any())).thenReturn(savedUser);

        mockMvc.perform(post(BASE_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", USER_URL))
                .andExpect(jsonPath("$.id").value(USER_ID))
                .andExpect(jsonPath("$.name").value(DEFAULT_NAME));
    }

    @Test
    @DisplayName("Should return 409 Conflict when saving person with duplicate data")
    void shouldReturnConflictOnDuplicate() throws Exception {

        PersonRequest request = new PersonRequest(DEFAULT_NAME, DEFAULT_LAST_NAME, DEFAULT_CPF,
                DEFAULT_PHONE, DEFAULT_INCOME, DEFAULT_PASSWORD, DEFAULT_TX_PASSWORD
        );

        String errorMessage = "error.cpf_exists";
        when(createPersonUseCase.savePerson(any())).thenThrow(new DuplicateException(errorMessage));

        mockMvc.perform(post(BASE_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.title").value("Data Conflict"))
                .andExpect(jsonPath("$.detail").value(errorMessage));
    }

    @Test
    @DisplayName("Should return 400 Bad Request when validation fails")
    void shouldReturnBadRequestOnValidationFailure() throws Exception {

        PersonRequest request = new PersonRequest(
                "", "Doe", "",
                DEFAULT_PHONE, DEFAULT_INCOME, "123", "123"
        );

        mockMvc.perform(post(BASE_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.title").value("$$-Validation Error"))
                .andExpect(jsonPath("$.name").exists())
                .andExpect(jsonPath("$.cpf").exists());
    }

    @Test
    @DisplayName("Should return 200 OK when updating name")
    void shouldUpdateNameSuccessfully() throws Exception {

        String newName = "Jane";
        String newLastName = "Marry";
        UserUpdateDtos.Name request = new UserUpdateDtos.Name(newName, newLastName);

        BankUser updatedUser = BankUser.builder()
                .id(USER_ID).name(newName).lastName(newLastName).phoneNumber(DEFAULT_PHONE).build();

        when(managePersonUseCase.updateName(eq(USER_ID), eq(newName), eq(newLastName))).thenReturn(updatedUser);

        mockMvc.perform(patch(USER_URL + "/name")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(USER_ID))
                .andExpect(jsonPath("$.name").value(newName))
                .andExpect(jsonPath("$.lastName").value(newLastName));
    }

    @Test
    @DisplayName("Should return 200 OK when updating phone")
    void shouldUpdatePhoneSuccessfully() throws Exception {

        String newPhone = "11988888888";
        UserUpdateDtos.Phone request = new UserUpdateDtos.Phone(newPhone);

        BankUser updatedUser = BankUser.builder()
                .id(USER_ID).name(DEFAULT_NAME).lastName(DEFAULT_LAST_NAME).phoneNumber(newPhone).build();

        when(managePersonUseCase.updatePhoneNumber(eq(USER_ID), eq(newPhone))).thenReturn(updatedUser);

        mockMvc.perform(patch(USER_URL + "/phone")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.phoneNumber").value(newPhone));
    }

    @Test
    @DisplayName("Should return 204 No Content when updating password")
    void shouldUpdatePasswordSuccessfully() throws Exception {

        String newPassword = "654321";
        UserUpdateDtos.password request = new UserUpdateDtos.password(DEFAULT_PASSWORD, newPassword);

        mockMvc.perform(patch(USER_URL + "/password")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNoContent());

        verify(manageCredentialsUseCase).updatePassword(eq(USER_ID), eq(DEFAULT_PASSWORD), eq(newPassword));
    }

    @Test
    @DisplayName("Should return 204 No Content when updating transaction password")
    void shouldUpdateTransactionPasswordSuccessfully() throws Exception {

        String newTxPassword = "4321";
        UserUpdateDtos.TransactionPassword request = new UserUpdateDtos.TransactionPassword(DEFAULT_TX_PASSWORD, newTxPassword );

        mockMvc.perform(patch(USER_URL + "/transaction-password")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNoContent());

        verify(manageCredentialsUseCase).updateTransactionPassword(eq(USER_ID), eq(DEFAULT_TX_PASSWORD), eq(newTxPassword));
    }
}
