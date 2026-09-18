package BankSdNd.example.BsDnD.core.port.in;

import BankSdNd.example.BsDnD.core.domain.model.Account;

/**
 * Input port for account creation use cases.
 */
public interface CreateAccountUseCase {
    Account createAccount(String cpf, String transactionPassword);
    Account createAccount(String cpf, char[] transactionPassword);
}
