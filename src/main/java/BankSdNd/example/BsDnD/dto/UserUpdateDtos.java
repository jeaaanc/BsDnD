package BankSdNd.example.BsDnD.dto;

import jakarta.validation.constraints.NotBlank;

public class UserUpdateDtos {

    public record Name(
            @NotBlank(message = "O nome é obrigatório") String name,
            @NotBlank(message = "O sobrenome é obrigatório") String lastName
    ) {}

    public record Phone(
            @NotBlank(message = "O telefone é obrigatório") String phoneNumber
    ) {}

    public record UserResponse (
            Long id,
            String name,
            String lastName,
            String phoneNumber
    ) {}

}
