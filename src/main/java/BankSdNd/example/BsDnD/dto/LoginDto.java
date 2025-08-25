package BankSdNd.example.BsDnD.dto;

public class LoginDto {
    private String cpf;
    private String rawPassword;

    public LoginDto(String cpf, String password) {
        this.cpf = cpf;
        this.rawPassword = password;
    }

    public String getCpf() {
        return cpf;
    }

    public String getRawPassword() {
        return rawPassword;
    }
}
