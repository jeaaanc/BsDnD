package BankSdNd.example.BsDnD.dto;

import java.math.BigDecimal;

public class PersonDto {

    private String name;
    private String lastName;
    private String cpf;
    private BigDecimal income;
    private String phoneNumber;
    private String rawpassword;
    private String confirmedrawPassword;

    public String getConfirmedrawPassword() {
        return confirmedrawPassword;
    }

    public PersonDto(){}

    public PersonDto(String name, String lastName, String cpf, String phoneNumber, BigDecimal income, String passWord, String confirmedPassword) {
        this.name = name;
        this.lastName = lastName;
        this.cpf = cpf;
        this.phoneNumber = phoneNumber;
        this.income = income;
        this.rawpassword = passWord;
        this.confirmedrawPassword = confirmedPassword;
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

    public String getRawpassword() {
        return rawpassword;
    }

    public BigDecimal getIncome() {
        return income;
    }
}
