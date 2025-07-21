package BankSdNd.example.BsDnD.model;

import jakarta.persistence.*;

import java.math.BigDecimal;

@Entity
@Table(name = "user_account")
public class Account {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "account_number")
    private String accountNumber;
    @Column(name = "balance")
    private BigDecimal balance;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private BankUser  titular;

    public Account(){}

    public Account(String numberAccount, BigDecimal  balance, BankUser titular) {
        this.accountNumber = numberAccount;
        this.balance = balance;
        this.titular = titular;
    }

    public Long getId() {
        return id;
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public BigDecimal  getBalance() {
        return balance;
    }

    public void setAccountNumber(String accountNumber) {
        this.accountNumber = accountNumber;
    }

    public void setBalance(BigDecimal  balance) {
        this.balance = balance;
    }

    public void setTitular(BankUser titular) {
        this.titular = titular;
    }

    public BankUser getTitular() {
        return titular;
    }

}
