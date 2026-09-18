package BankSdNd.example.BsDnD.core.port.in.dto;

import java.math.BigDecimal;

/**
 * Command for registering a new person.
 */
public record CreatePersonCommand(
    String name,
    String lastName,
    String cpf,
    String phoneNumber,
    BigDecimal income,
    String password,
    String transactionPassword
) {}
