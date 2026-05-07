package BankSdNd.example.BsDnD.dto;
import jakarta.validation.constraints.NotBlank;

/**
 * Data Transfer Object (DTO) for handling user login credentials.
 * This record acts as an immutable carrier for the user's CPF and password.
 * It utilizes Jakarta Validation to ensure data integrity (non-blank fields)
 *  before the request reaches the service layer.
 * @param cpf  The user's CPF. Must not be blank.
 * @param password password The user's password. Must not be blank.
 */
public record LoginDto (

    @NotBlank(message = "O CPF é obrigatório")
    String cpf,
    @NotBlank(message = "A senha é obrigatória")
    @jakarta.validation.constraints.Pattern(regexp = "^\\d{6}$", message = "A senha de login deve ter 6 números.")
    String password
) {}