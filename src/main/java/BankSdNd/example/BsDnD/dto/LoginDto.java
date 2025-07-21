package BankSdNd.example.BsDnD.dto;

public class LoginDto {
    private String cpf;
    private String password;

    public LoginDto(String cpf, String password) {
        this.cpf = cpf;
        this.password = password;
    }

    public String getCpf() {
        return cpf;
    }

    public String getPassword() {
        return password;
    }
}
