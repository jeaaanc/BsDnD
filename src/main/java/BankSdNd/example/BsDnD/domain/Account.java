package BankSdNd.example.BsDnD.domain;

import jakarta.persistence.*;

import java.math.BigDecimal;

@Entity
@Table(name = "user_account")
public class Account {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "account_number", unique = true, nullable = false, length = 9)
    private String accountNumber;
    @Column(name = "balance")
    private BigDecimal balance;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private BankUser  titular;

    protected Account(){}

    public Account(String numberAccount, BankUser titular, BigDecimal initialDeposit) {
        this.accountNumber = numberAccount;
        this.titular = titular;
        this.balance = BigDecimal.ZERO;
        if (initialDeposit != null && initialDeposit.compareTo(BigDecimal.ZERO) > 0){
            deposit(initialDeposit);
        }
    }

    public void deposit(BigDecimal amount) {
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0){
            throw new IllegalArgumentException("Deposito deve ser maior do que 0.");
        }
        this.balance = this.balance.add(amount);
    }

    public void withdraw(BigDecimal amount) {

        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0){

            throw new IllegalArgumentException("Retirada deve ser maior que 0.");
        }

        if (balance.compareTo(amount) < 0){

            throw  new IllegalArgumentException("Saldo insuficiente.");
        }
        this.balance = this.balance.subtract(amount);
    }

    public void transferTo(Account destination, BigDecimal amount){

        this.withdraw(amount);
        destination.deposit(amount);
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

    public void setTitular(BankUser titular) {
        this.titular = titular;
    }

    public BankUser getTitular() {
        return titular;
    }

}
