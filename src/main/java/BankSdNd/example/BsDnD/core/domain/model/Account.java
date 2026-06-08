package BankSdNd.example.BsDnD.core.domain.model;

import BankSdNd.example.BsDnD.core.domain.exception.InsufficientBalanceException;

import java.math.BigDecimal;

/**
 * Represents a bank account in the system.
 * This is a pure domain model, free of infrastructure or framework dependencies.
 */
public class Account {

    private Long id;
    private String accountNumber;
    private BigDecimal balance;
    private BankUser holder;
    private boolean active = true;

    protected Account(){}

    public Account(String numberAccount, BankUser holder) {
        this.accountNumber = numberAccount;
        this.holder = holder;
        this.balance = BigDecimal.ZERO;
    }

    public boolean isOwnedBy(Long userId) {
        return this.holder != null && this.holder.getId().equals(userId);
    }

    public void deposit(BigDecimal amount) {
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0){
            throw new IllegalArgumentException("Deposit must be greater than 0");
        }
        this.balance = this.balance.add(amount);
    }

    public void withdraw(BigDecimal amount) {
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0){
            throw new IllegalArgumentException("Withdrawal must be greater than 0.");
        }
        if (balance.compareTo(amount) < 0){
            throw  new InsufficientBalanceException("Insufficient balance.");
        }
        this.balance = this.balance.subtract(amount);
    }

    public void transferTo(Account destination, BigDecimal amount){
        this.withdraw(amount);
        destination.deposit(amount);
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setBalance(BigDecimal balance) {
        this.balance = balance;
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

    public void setHolder(BankUser holder) {
        this.holder = holder;
    }

    public BankUser getHolder() {
        return holder;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }
}
