package BankSdNd.example.BsDnD.dto;

import BankSdNd.example.BsDnD.util.SecurePasswordHolder;

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
