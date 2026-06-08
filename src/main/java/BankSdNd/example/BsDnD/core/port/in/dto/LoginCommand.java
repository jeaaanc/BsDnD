package BankSdNd.example.BsDnD.core.port.in.dto;

/**
 * Command for user login.
 */
public record LoginCommand(String cpf, String password) {}
