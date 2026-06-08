package BankSdNd.example.BsDnD.core.domain.model;

import lombok.*;

import java.math.BigDecimal;

/**
 * Represents a user (customer) of the bank.
 * This is a pure domain model, free of infrastructure or framework dependencies.
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BankUser {
    private Long id;
    private String name;
    private String lastName;
    private String cpf;
    private String phoneNumber;
    private String password;
    private String transactionPassword;
    private BigDecimal income;
}
