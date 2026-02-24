package BankSdNd.example.BsDnD.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record AccountRequest(
        @NotBlank(message = "O CPF é obrigatorio")
        @Pattern(regexp = "\\d{11}", message = "O CPF deve conter 11 dígitos númericos")
        String cpf
) {}
