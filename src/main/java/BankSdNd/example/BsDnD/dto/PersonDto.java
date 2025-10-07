package BankSdNd.example.BsDnD.dto;

import java.math.BigDecimal;



/**
 * Data Transfer Object (DTO) for carrying new user registration data.
 *
 * This class is used to transfer all necessary information for creating a new {@code BankUser}
 * from the controller layer to the service layer. It is designed to hold the user's password
 * in an already-encoded format for security.
 */
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
