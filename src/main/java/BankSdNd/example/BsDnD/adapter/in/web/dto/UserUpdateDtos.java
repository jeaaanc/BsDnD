package BankSdNd.example.BsDnD.adapter.in.web.dto;

import jakarta.validation.constraints.NotBlank;

public class UserUpdateDtos {

    public record Name(
            @NotBlank(message = "O nome é obrigatório") String name,
            @NotBlank(message = "O sobrenome é obrigatório") String lastName
    ) {}

    public record Phone(
            @NotBlank(message = "O telefone é obrigatório") String phoneNumber
    ) {}

    public record password(
            @NotBlank(message = "A senha antiga é obrigatória") String oldPassword,
            @NotBlank(message = "A senha nova é obrigatória")
            @jakarta.validation.constraints.Pattern(regexp = "^\\d{6}$", message = "A senha de login deve ter 6 números.")
            String newPassword
    ) {}

    public record TransactionPassword(
            @NotBlank(message = "A senha de transação antiga é obrigatória") String oldTransactionPassword,
            @NotBlank(message = "A senha de transação nova é obrigatória")
            @jakarta.validation.constraints.Pattern(regexp = "^\\d{4}$", message = "A senha de transação deve ter 4 números.")
            String newTransactionPassword
    ) {}

    public record UserResponse (
            Long id,
            String name,
            String lastName,
            String phoneNumber
    ) {}

}
