package BankSdNd.example.BsDnD.domain;

import jakarta.persistence.*;

import java.math.BigDecimal;



/**
 * Represents a user (customer) of the bank.
 *
 * This is a JPA entity that maps to the {@code bank_user} table in the database.
 * It contains the user's personal identification data, income information, and security credentials.
 * Objects of this class are typically created using the nested {@link Builder}.
 */
@Entity
@Table(name = "bank_user")
public class BankUser {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private  Long id;

    @Column(name = "name")
    private String name;

    @Column(name = "lastname")
    private String lastName;

    @Column(name = "cpf", unique = true, nullable = false)
    private String cpf;

    @Column(name = "phone_number", unique = true)
    private String phoneNumber;

    @Column(name = "password", nullable = false)
    private String password;

    @Column(name = "income", precision = 15, scale = 2)
    private BigDecimal income;

    /**
     * JPA-required constructor. Should not be used directly.
     * Use the {@link Builder} to create new instances.
     */
    protected BankUser(){}

    public void setPassword(String password) {
        this.password = password;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public void setName(String name) {
        this.name = name;
    }

    public BigDecimal getIncome() {
        return income;
    }

    public void setIncome(BigDecimal income) {
        this.income = income;
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


    public Long getId() {
        return id;
    }



    /**
     * The Builder class for creating {@link BankUser} instances.
     * This follows the Builder design pattern to allow for clean and readable object creation.
     */
    public static class Builder {
        private String name;
        private String lastName;
        private String cpf;
        private String phoneNumber;
        private BigDecimal income;
        private String passWord;

        public Builder name(String name){
            this.name = name;
            return this;
        }
        public Builder lastName(String lastName){
            this.lastName = lastName;
            return this;
        }
        public Builder cpf(String cpf){
            this.cpf = cpf;
            return this;
        }
        public Builder phoneNumber(String phoneNumber){
            this.phoneNumber = phoneNumber;
            return this;
        }
        public Builder income(BigDecimal income){
            this.income = income;
            return this;
        }
        public Builder passWord(String passWord){
            this.passWord = passWord;
            return this;
        }

        /**
         * Builds and returns a new {@link BankUser} instance with the configured data.
         * @return a new {@code BankUser} object.
         */
        public BankUser build(){
            BankUser person = new BankUser();
            person.name = this.name;
            person.lastName = this.lastName;
            person.cpf = this.cpf;
            person.phoneNumber = this.phoneNumber;
            person.income = this.income;
            person.password = this.passWord;

            return person;
        }

    }
}
