package BankSdNd.example.BsDnD.dto;

import java.math.BigDecimal;

public class PersonDto {

    private String name;
    private String lastName;
    private String cpf;
    private BigDecimal income;
    private String phoneNumber;
    private String encodedPassword;

    public PersonDto(){}

    public PersonDto(String name, String lastName, String cpf, String phoneNumber, BigDecimal income, String encodedPassword) {
        this.name = name;
        this.lastName = lastName;
        this.cpf = cpf;
        this.phoneNumber = phoneNumber;
        this.income = income;
        this.encodedPassword = encodedPassword;
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

    public String getEncodedPassword() {
        return encodedPassword;
    }

    public BigDecimal getIncome() {
        return income;
    }
}
