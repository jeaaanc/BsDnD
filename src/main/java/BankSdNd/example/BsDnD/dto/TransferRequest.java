package BankSdNd.example.BsDnD.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;

public record TransferRequest(

        @NotBlank(message = "Origin account is required")
        String sourceAccountNumber,

        @NotBlank(message = "Destination account is required")
        String destinationAccountNumber,

        @NotNull(message = "Value is required")
        @Positive(message = "The value must be greater than zero")
        BigDecimal amount,

        @NotBlank(message = "Password is required to authorize the transfer")
        String password
) {}
