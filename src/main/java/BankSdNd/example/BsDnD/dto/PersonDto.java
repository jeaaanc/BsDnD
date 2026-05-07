package BankSdNd.example.BsDnD.dto;

import jakarta.validation.constraints.*;

import java.math.BigDecimal;

/**
 * Data Transfer Object (DTO) for registering a new user.
 * * This record acts as an immutable carrier for registration data (personal info, credentials, income).
 * It utilizes Jakarta Validation annotations to ensure data integrity (e.g., non-blank fields, positive income)
 * before the request reaches the service layer.
 *
 * @param name  The user's first name.
 * @param lastName  The user's last name.
 * @param cpf  The user's CPF (validated by business logic later).
 * @param  phoneNumber The user's phone number.
 * @param  income The user's monthly income. Must be positive.
 * @param password  The user's raw password. Must meet minimum size requirements.
 * @param transactionPassword the ...!!!!
 */
public record PersonDto(

    @NotBlank(message = "O nome é obrigatório")
    String name,

    @NotBlank(message = "O sobrenome é obrigatório")
    String lastName,

    @NotBlank(message = "O CPF é obrigatório")
    String cpf,

    @NotBlank(message = "O telefone é obrigatório")
    String phoneNumber,

    @NotNull(message = "A renda é obrigatório")
    @Positive(message = "A renda deve ser um valor positivo")
    BigDecimal income,

    @NotBlank(message = "A senha de login é obrigatória")
    @Pattern(regexp = "^\\d{6}$", message = "A senha de login deve ter 6 números.")
    String password,

    @NotBlank(message = "A senha de transação é obrigatória")
    @Pattern(regexp = "^\\d{4}$", message = "A senha de transação deve ter 4 números.")
    String transactionPassword
) {}