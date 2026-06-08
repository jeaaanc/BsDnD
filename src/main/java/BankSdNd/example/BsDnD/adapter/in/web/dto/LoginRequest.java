package BankSdNd.example.BsDnD.adapter.in.web.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record LoginRequest(
    @NotBlank(message = "O CPF é obrigatório")
    String cpf,
    @NotBlank(message = "A senha é obrigatória")
    @Pattern(regexp = "^\\d{6}$", message = "A senha de login deve ter 6 números.")
    String password
) {}
