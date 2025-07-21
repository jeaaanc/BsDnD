package BankSdNd.example.BsDnD.model;

import jakarta.persistence.*;


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
    @Column(name = "cpf")
    private String cpf;
    @Column(name = "phone_number")
    private String phoneNumber;
    @Column(name = "password")
    private String passWord;

    private BankUser(){}

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
        return passWord;
    }


    public Long getId() {
        return id;
    }

    public static class Builder {
        private String name;
        private String lastName;
        private String cpf;
        private String phoneNumber;
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
        public Builder passWord(String passWord){
            this.passWord = passWord;
            return this;
        }
        public BankUser build(){
            BankUser person = new BankUser();
            person.name = this.name;
            person.lastName = this.lastName;
            person.cpf = this.cpf;
            person.phoneNumber = this.phoneNumber;
            person.passWord = this.passWord;

            return person;
        }

    }


    public void info (){
        System.out.println("\nNome: " + name + "\nSobrenome: " + lastName + "\nCPF: " + cpf + "\nTelefone: " + phoneNumber );
    }
}
