package BankSdNd.example.BsDnD.dto;

import BankSdNd.example.BsDnD.util.SecurePasswordHolder;


/**
 * Data Transfer Object (DTO) for handling user login credentials.
 *
 * This class securely carries the user's CPF and raw password (as a char array)
 * from the controller layer to the authentication service. It implements
 * the {@link SecurePasswordHolder} to enforce secure password handling.
 */
public class LoginDto implements SecurePasswordHolder {

    private final String cpf;
    private final char[] rawPassword;


    public LoginDto(String cpf, char[] password) {
        this.cpf = cpf;
        this.rawPassword = password;
    }

    public String getCpf() {
        return cpf;
    }

    public char[] getRawPassword() {
        return rawPassword;
    }
}
