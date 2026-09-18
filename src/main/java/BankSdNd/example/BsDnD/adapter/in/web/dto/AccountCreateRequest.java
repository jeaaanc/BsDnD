package BankSdNd.example.BsDnD.adapter.in.web.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record AccountCreateRequest(
        @NotBlank(message = "A senha de transação é obrigatória")
        @Size(min = 4, max = 4, message = "A senha de transação deve ter exatamente 4 caracteres")
        String transactionPassword
) {}
