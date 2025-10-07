package BankSdNd.example.BsDnD.domain;

import BankSdNd.example.BsDnD.exception.business.InsufficientBalanceException;
import jakarta.persistence.*;

import java.math.BigDecimal;


/**
 * Represents a bank account in the system.
 *
 * This is a JPA entity that maps to the {@code user_account} table. It holds the account's balance
 * and is associated with a {@link BankUser} owner. The state of the balance is protected by
 * business logic within the {@code deposit} and {@code withdraw} methods.
 */
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

    /**
     * JPA-required constructor. Should not be used directly.
     */
    protected Account(){}

    public Account(String numberAccount, BankUser titular, BigDecimal initialDeposit) {
        this.accountNumber = numberAccount;
        this.titular = titular;
        this.balance = BigDecimal.ZERO;
        if (initialDeposit != null && initialDeposit.compareTo(BigDecimal.ZERO) > 0){
            deposit(initialDeposit);
        }
    }


    /**
     * Deposits a given amount into the account.
     *
     * @param amount The amount to be deposited. Must be a positive value.
     * @throws IllegalArgumentException if the amount is null, zero, or negative.
     */
    public void deposit(BigDecimal amount) {
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0){
            throw new IllegalArgumentException("Deposit must be greater than 0");
        }
        this.balance = this.balance.add(amount);
    }


    /**
     * Withdraws a given amount from the account.
     *
     * @param amount The amount to be withdrawn. Must be a positive value.
     * @throws IllegalArgumentException if the amount is null, zero, or negative.
     * @throws InsufficientBalanceException if the amount is greater than the current balance.
     */
    public void withdraw(BigDecimal amount) {

        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0){

            throw new IllegalArgumentException("Withdrawal must be greater than 0.");
        }

        if (balance.compareTo(amount) < 0){

            throw  new InsufficientBalanceException("Insufficient balance.");
        }
        this.balance = this.balance.subtract(amount);
    }



    /**
     * Transfers a given amount from this account to a destination account.
     * This method is a convenience wrapper around withdraw() and deposit() methods.
     * For transactional integrity, this operation should be managed by a {@code @Transactional} service method.
     *
     * @param destination The destination {@code Account}.
     * @param amount The amount to be transferred.
     */
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
