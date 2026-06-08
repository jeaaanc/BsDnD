package BankSdNd.example.BsDnD.adapter.in.web.dto;

import jakarta.validation.constraints.*;
import java.math.BigDecimal;

public record PersonRequest(
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
