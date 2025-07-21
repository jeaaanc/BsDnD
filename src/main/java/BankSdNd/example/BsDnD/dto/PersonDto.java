package BankSdNd.example.BsDnD.dto;

public class PersonDto {

    private String name;
    private String lastName;
    private String cpf;
    private String phoneNumber;
    private String password;
    private String confirmedPassword;

    public String getConfirmedPassword() {
        return confirmedPassword;
    }

    public PersonDto(){}

    public PersonDto(String name, String lastName, String cpf, String phoneNumber, String passWord, String confirmedPassword) {
        this.name = name;
        this.lastName = lastName;
        this.cpf = cpf;
        this.phoneNumber = phoneNumber;
        this.password = passWord;
        this.confirmedPassword = confirmedPassword;
    }

    public String getName() {
        return name;
    }

    public String getLastName() {
        return lastName;
    }

    public String getCpf() {
        return cpf;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public String getPassword() {
        return password;
    }
}
